package com.forestech.simpleui.util;

/**
 * RetryPolicy
 * Implements retry logic with exponential backoff for handling transient
 * failures.
 * 
 * Configuration:
 * - Max retries: 3 attempts
 * - Initial backoff: 500ms
 * - Exponential multiplier: 2x (500ms → 1s → 2s)
 * - Retryable exceptions: ServiceUnavailableException, ServiceTimeoutException
 */
public class RetryPolicy {

    private final int maxRetries;
    private final long initialBackoffMs;
    private final double backoffMultiplier;

    /**
     * Creates a RetryPolicy with default settings.
     * Max retries: 3, Initial backoff: 500ms, Multiplier: 2.0
     */
    public RetryPolicy() {
        this(3, 500, 2.0);
    }

    /**
     * Creates a RetryPolicy with custom settings.
     * 
     * @param maxRetries        Maximum number of retry attempts
     * @param initialBackoffMs  Initial backoff delay in milliseconds
     * @param backoffMultiplier Multiplier for exponential backoff
     */
    public RetryPolicy(int maxRetries, long initialBackoffMs, double backoffMultiplier) {
        this.maxRetries = maxRetries;
        this.initialBackoffMs = initialBackoffMs;
        this.backoffMultiplier = backoffMultiplier;
    }

    /**
     * Executes a task with retry logic.
     * 
     * @param task The task to execute
     * @param <T>  The return type of the task
     * @return The result of the task
     * @throws Exception if all retry attempts fail
     */
    public <T> T executeWithRetry(SupplierWithException<T> task) throws Exception {
        Exception lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return task.get();
            } catch (ServiceUnavailableException | ServiceTimeoutException e) {
                lastException = e;

                // Don't sleep after the last attempt
                if (attempt < maxRetries) {
                    long backoffMs = (long) (initialBackoffMs * Math.pow(backoffMultiplier, attempt));

                    System.out.println(String.format(
                            "[RetryPolicy] Intento %d/%d falló: %s. Reintentando en %dms...",
                            attempt + 1, maxRetries + 1, e.getDetailedMessage(), backoffMs));

                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ServiceException("RetryPolicy", "sleep", ie);
                    }
                } else {
                    System.err.println(String.format(
                            "[RetryPolicy] Todos los intentos (%d) fallaron para: %s",
                            maxRetries + 1, e.getDetailedMessage()));
                }
            } catch (Exception e) {
                // Non-retryable exception, throw immediately
                throw e;
            }
        }

        // All retries exhausted
        throw lastException;
    }

    /**
     * Checks if an exception is retryable.
     * 
     * @param exception The exception to check
     * @return true if the exception should trigger a retry
     */
    public static boolean isRetryable(Exception exception) {
        return exception instanceof ServiceUnavailableException ||
                exception instanceof ServiceTimeoutException;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getInitialBackoffMs() {
        return initialBackoffMs;
    }

    public double getBackoffMultiplier() {
        return backoffMultiplier;
    }
}
