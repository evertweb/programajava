package com.forestech.simpleui.util;

import javax.swing.SwingWorker;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * AsyncServiceTask
 * A generic wrapper around SwingWorker to enforce Policy 5.3 (Multithreading).
 * Ensures no network calls happen on the EDT.
 * 
 * @param <T> The result type of the background task.
 */
public class AsyncServiceTask<T> extends SwingWorker<T, Void> {

    private final Supplier<T> backgroundTask;
    private final Consumer<T> onSuccess;
    private final Consumer<Throwable> onError;

    public AsyncServiceTask(Supplier<T> backgroundTask, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        this.backgroundTask = backgroundTask;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    protected T doInBackground() throws Exception {
        // This runs on a background thread
        return backgroundTask.get();
    }

    @Override
    protected void done() {
        // This runs on the EDT
        try {
            T result = get();
            if (onSuccess != null) {
                onSuccess.accept(result);
            }
        } catch (Exception e) {
            if (onError != null) {
                onError.accept(e);
            } else {
                e.printStackTrace(); // Fallback logging
            }
        }
    }

    /**
     * Helper to execute a task immediately.
     */
    public static <T> void execute(Supplier<T> task, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        new AsyncServiceTask<>(task, onSuccess, onError).execute();
    }
}
