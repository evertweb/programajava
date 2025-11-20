package com.forestech.presentation.ui.utils;

import javax.swing.SwingWorker;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Centralized executor service for all background tasks in the application.
 * 
 * <p>
 * <strong>Purpose:</strong> Prevents UI freezing by limiting concurrent
 * background operations.
 * </p>
 * 
 * <p>
 * <strong>Problem it solves:</strong>
 * </p>
 * <ul>
 * <li>SwingWorker creates unbounded threads (one per task)</li>
 * <li>Rapid navigation creates 10+ concurrent workers</li>
 * <li>Each worker requests database connections</li>
 * <li>Connection pool exhaustion → freezing</li>
 * </ul>
 * 
 * <p>
 * <strong>Solution:</strong>
 * </p>
 * <ul>
 * <li>Single thread pool shared across entire app</li>
 * <li>Limited to 5 concurrent tasks maximum</li>
 * <li>Additional tasks queue instead of failing</li>
 * <li>Metrics available for monitoring</li>
 * </ul>
 */
public final class BackgroundTaskExecutor {

    private static final int CORE_POOL_SIZE = 8;
    private static final int MAX_POOL_SIZE = 12;
    private static final int QUEUE_CAPACITY = 50;
    private static final long KEEP_ALIVE_TIME = 60L; // seconds

    private static ThreadPoolExecutor executor;
    private static AtomicInteger tasksSubmitted = new AtomicInteger(0);
    private static AtomicInteger tasksCompleted = new AtomicInteger(0);
    private static boolean enabled = true;
    private static Consumer<String> globalLogger;

    static {
        initialize();
    }

    private BackgroundTaskExecutor() {
        // Utility class, no instances
    }

    /**
     * Initializes the thread pool executor with bounded resources.
     */
    private static synchronized void initialize() {
        if (executor != null && !executor.isShutdown()) {
            return;
        }

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                workQueue,
                new NamedThreadFactory("ForestechBG"),
                new ThreadPoolExecutor.AbortPolicy() // Reject if queue full (better than blocking EDT)
        );

        executor.allowCoreThreadTimeOut(true);

        log("BackgroundTaskExecutor initialized: core=" + CORE_POOL_SIZE +
                ", max=" + MAX_POOL_SIZE + ", queue=" + QUEUE_CAPACITY);
    }

    /**
     * Sets a global logger for executor messages.
     * 
     * @param logger Consumer to receive log messages
     */
    public static void setGlobalLogger(Consumer<String> logger) {
        globalLogger = logger;
    }

    /**
     * Submits a SwingWorker to the centralized thread pool.
     * 
     * <p>
     * Use this instead of worker.execute() to prevent unbounded thread creation.
     * </p>
     * 
     * @param worker SwingWorker to execute
     */
    public static void submit(SwingWorker<?, ?> worker) {
        if (!enabled) {
            // Fallback to default SwingWorker execution
            worker.execute();
            return;
        }

        tasksSubmitted.incrementAndGet();
        int submitted = tasksSubmitted.get();
        int active = executor.getActiveCount();
        int queued = executor.getQueue().size();

        log(String.format("Task submitted #%d (active:%d, queued:%d)", submitted, active, queued));

        try {
            // Wrap the worker to track completion
            executor.execute(() -> {
                String threadName = Thread.currentThread().getName();
                DiagnosticLogger.logTask(submitted, "INICIANDO", threadName);

                try {
                    // Execute the worker properly - this calls doInBackground()
                    // and schedules done() on EDT when complete
                    worker.run();
                    DiagnosticLogger.logTask(submitted, "COMPLETADO", threadName);
                } catch (Exception ex) {
                    DiagnosticLogger.logException("Task #" + submitted, ex);
                } finally {
                    tasksCompleted.incrementAndGet();
                    log(String.format("Task completed #%d (total:%d/%d)",
                            tasksCompleted.get(), tasksCompleted.get(), tasksSubmitted.get()));
                }
            });
        } catch (java.util.concurrent.RejectedExecutionException e) {
            // Pool is saturated - fallback to SwingWorker's default execution
            // This is better than blocking the EDT with CallerRunsPolicy
            DiagnosticLogger.logCritical("Pool SATURADO para task #" + submitted);
            log("WARNING: Pool saturated, using fallback execution for task #" + submitted);
            worker.execute();
        }
    }

    /**
     * Gets current executor metrics.
     * 
     * @return Metrics string with active, queued, completed tasks
     */
    public static String getMetrics() {
        if (executor == null) {
            return "Executor not initialized";
        }

        return String.format(
                "Threads: %d/%d (max:%d) | Queue: %d/%d | Tasks: %d submitted, %d completed",
                executor.getActiveCount(),
                executor.getPoolSize(),
                MAX_POOL_SIZE,
                executor.getQueue().size(),
                QUEUE_CAPACITY,
                tasksSubmitted.get(),
                tasksCompleted.get());
    }

    /**
     * Gets active thread count.
     * 
     * @return Number of currently executing tasks
     */
    public static int getActiveCount() {
        return executor != null ? executor.getActiveCount() : 0;
    }

    /**
     * Gets queue size.
     * 
     * @return Number of tasks waiting in queue
     */
    public static int getQueueSize() {
        return executor != null ? executor.getQueue().size() : 0;
    }

    /**
     * Checks if executor is busy (at or near capacity).
     * 
     * @return true if executor is running 4+ tasks or queue has 5+ items
     */
    public static boolean isBusy() {
        if (executor == null)
            return false;
        return executor.getActiveCount() >= 4 || executor.getQueue().size() >= 5;
    }

    /**
     * Enables or disables the centralized executor.
     * When disabled, falls back to standard SwingWorker.execute().
     * 
     * @param enabled true to use centralized pool, false for default behavior
     */
    public static void setEnabled(boolean enabled) {
        BackgroundTaskExecutor.enabled = enabled;
        log("BackgroundTaskExecutor " + (enabled ? "ENABLED" : "DISABLED"));
    }

    /**
     * Gracefully shuts down the executor.
     * Should be called when application closes.
     */
    public static void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            log("Shutting down BackgroundTaskExecutor...");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    log("BackgroundTaskExecutor force shutdown");
                } else {
                    log("BackgroundTaskExecutor shutdown complete");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Custom thread factory to name threads for easier debugging.
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }

    private static void log(String message) {
        String logLine = "[BackgroundTaskExecutor] " + message;
        System.out.println(logLine);
        if (globalLogger != null) {
            globalLogger.accept(logLine);
        }
    }
}
