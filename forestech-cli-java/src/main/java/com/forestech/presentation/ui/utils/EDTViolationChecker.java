package com.forestech.presentation.ui.utils;

import javax.swing.SwingUtilities;
import java.util.function.Consumer;

/**
 * Utility to detect and log EDT (Event Dispatch Thread) violations.
 * Helps identify long-running operations that block the UI thread.
 */
public final class EDTViolationChecker {

    private static final long EDT_WARNING_THRESHOLD_MS = 100;
    private static boolean enabled = true;

    private EDTViolationChecker() {
        // Utility class, no instances
    }

    /**
     * Enable or disable EDT violation checking.
     * Useful to disable in production builds.
     *
     * @param enabled true to enable checking, false to disable
     */
    public static void setEnabled(boolean enabled) {
        EDTViolationChecker.enabled = enabled;
    }

    /**
     * Checks if the current operation is running on the EDT and if it takes too
     * long.
     * Should be called at the start and end of potentially long operations.
     *
     * @param operationName Name of the operation being performed
     * @param logger        Consumer to log warnings
     * @return Start time in milliseconds (use with checkEnd)
     */
    public static long checkStart(String operationName, Consumer<String> logger) {
        if (!enabled) {
            return System.currentTimeMillis();
        }

        if (SwingUtilities.isEventDispatchThread()) {
            logger.accept(String.format("⚠️ EDT: %s started on EDT", operationName));
        }

        return System.currentTimeMillis();
    }

    /**
     * Checks the duration of an operation that started with checkStart.
     * Logs a warning if the operation took too long on the EDT.
     *
     * @param operationName Name of the operation
     * @param startTime     Start time from checkStart
     * @param logger        Consumer to log warnings
     */
    public static void checkEnd(String operationName, long startTime, Consumer<String> logger) {
        if (!enabled) {
            return;
        }

        long duration = System.currentTimeMillis() - startTime;

        if (SwingUtilities.isEventDispatchThread() && duration > EDT_WARNING_THRESHOLD_MS) {
            logger.accept(String.format(
                    "🚨 EDT VIOLATION: %s blocked EDT for %d ms (threshold: %d ms)",
                    operationName,
                    duration,
                    EDT_WARNING_THRESHOLD_MS));
        }
    }

    /**
     * Runs an operation and automatically checks for EDT violations.
     *
     * @param operationName Name of the operation
     * @param operation     The operation to run
     * @param logger        Consumer to log warnings
     */
    public static void checkOperation(String operationName, Runnable operation, Consumer<String> logger) {
        long start = checkStart(operationName, logger);
        try {
            operation.run();
        } finally {
            checkEnd(operationName, start, logger);
        }
    }

    /**
     * Ensures the current code is NOT running on the EDT.
     * Throws an exception if it is (useful for debugging background operations).
     *
     * @param operationName Name of the operation
     */
    public static void assertNotEDT(String operationName) {
        if (enabled && SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException(
                    "Operation " + operationName + " must NOT run on EDT but is currently on EDT");
        }
    }

    /**
     * Ensures the current code IS running on the EDT.
     * Throws an exception if it isn't (useful for debugging UI operations).
     *
     * @param operationName Name of the operation
     */
    public static void assertOnEDT(String operationName) {
        if (enabled && !SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException(
                    "Operation " + operationName + " must run on EDT but is currently on " +
                            Thread.currentThread().getName());
        }
    }
}
