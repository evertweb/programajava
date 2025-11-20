package com.forestech.presentation.ui.utils;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.util.function.Consumer;

/**
 * Coordina solicitudes de carga para evitar saturar el EDT.
 * Ahora incluye cancelación de workers previos, timeout tracking, y métricas de
 * rendimiento.
 */
public final class AsyncLoadManager {

    private static final long WORKER_TIMEOUT_MS = 30000; // 30 segundos timeout

    private final String moduleName;
    private final Consumer<String> logConsumer;
    private final Consumer<String> loadAction;

    private boolean loading;
    private String pendingOrigin;
    private String lastOrigin = "Inicial";

    // Referencia al SwingWorker actual (para poder cancelarlo)
    private SwingWorker<?, ?> currentWorker;
    private long workerStartTime;
    private Timer timeoutTimer;

    // Métricas de rendimiento
    private long totalLoads = 0;
    private long minLoadTime = Long.MAX_VALUE;
    private long maxLoadTime = 0;
    private long totalLoadTime = 0;

    public AsyncLoadManager(String moduleName,
            Consumer<String> logConsumer,
            Consumer<String> loadAction) {
        this.moduleName = moduleName;
        this.logConsumer = logConsumer;
        this.loadAction = loadAction;
    }

    /**
     * Solicita una nueva carga de datos.
     * Si hay un worker en curso, lo cancela antes de proceder.
     *
     * @param origin Descripción del origen de la solicitud
     */
    public void requestLoad(String origin) {
        String safeOrigin = sanitize(origin);

        // CRÍTICO: Cancelar worker previo si existe
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
            logConsumer.accept(moduleName + ": worker previo CANCELADO (nuevo origen: " + safeOrigin + ")");
            cancelTimeoutTimer();
            currentWorker = null;
        }

        if (loading) {
            pendingOrigin = safeOrigin;
            logConsumer.accept(moduleName + ": solicitud en espera desde " + safeOrigin +
                    " (en curso: " + lastOrigin + ")");
            return;
        }

        loading = true;
        lastOrigin = safeOrigin;
        workerStartTime = System.currentTimeMillis();
        logConsumer.accept(moduleName + ": solicitud de carga desde " + safeOrigin);

        // Iniciar timeout timer
        startTimeoutTimer();

        // Ejecutar la acción de carga (el panel debe llamar registerWorker() antes de
        // ejecutar)
        loadAction.accept(safeOrigin);
    }

    /**
     * Registra el SwingWorker actual para poder cancelarlo después.
     * Debe llamarse ANTES de worker.execute()
     *
     * @param worker El SwingWorker a registrar
     */
    public void registerWorker(SwingWorker<?, ?> worker) {
        this.currentWorker = worker;
    }

    public void finish(long startedAtMillis) {
        loading = false;
        long duration = System.currentTimeMillis() - startedAtMillis;

        // Actualizar métricas
        updateMetrics(duration);

        cancelTimeoutTimer();
        currentWorker = null; // Limpiar referencia al worker completado

        logConsumer.accept(String.format("%s: hilo EDT libre (duración: %d ms)", moduleName, duration));

        // TEMPORALMENTE DESACTIVADO: GlobalUIBlocker
        // GlobalUIBlocker blocker = GlobalUIBlocker.getInstance();
        // if (blocker != null) {
        //     blocker.unblock(moduleName + " cargado correctamente");
        // }
        DiagnosticLogger.log(moduleName + " >> Carga completada (blocker desactivado)");

        if (pendingOrigin != null) {
            String origin = pendingOrigin;
            pendingOrigin = null;
            logConsumer.accept(String.format(
                    "%s: ejecutando carga pendiente (%s) tras %.0f ms",
                    moduleName,
                    origin,
                    (double) (System.currentTimeMillis() - startedAtMillis)));
            SwingUtilities.invokeLater(() -> requestLoad(origin));
        }
    }

    /**
     * Cancela la carga actual si existe.
     * Útil cuando el usuario navega a otra vista.
     */
    public void cancelCurrentLoad() {
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
            logConsumer.accept(moduleName + ": carga CANCELADA externamente");
            cancelTimeoutTimer();
            currentWorker = null;
            loading = false;
        }
    }

    public String getLastOrigin() {
        return lastOrigin;
    }

    /**
     * Obtiene las métricas de rendimiento acumuladas.
     *
     * @return String con estadísticas de carga
     */
    public String getMetrics() {
        if (totalLoads == 0) {
            return moduleName + ": Sin cargas realizadas";
        }

        long avgLoadTime = totalLoadTime / totalLoads;
        return String.format(
                "%s: Cargas=%d, Min=%dms, Max=%dms, Avg=%dms",
                moduleName, totalLoads, minLoadTime, maxLoadTime, avgLoadTime);
    }

    private void startTimeoutTimer() {
        cancelTimeoutTimer();

        timeoutTimer = new Timer((int) WORKER_TIMEOUT_MS, e -> {
            if (currentWorker != null && !currentWorker.isDone()) {
                long elapsed = System.currentTimeMillis() - workerStartTime;
                logConsumer.accept(String.format(
                        "⚠️ %s: Worker TIMEOUT detectado (%.1f segundos). Cancelando...",
                        moduleName,
                        elapsed / 1000.0));
                currentWorker.cancel(true);
                currentWorker = null;
                loading = false;
            }
        });
        timeoutTimer.setRepeats(false);
        timeoutTimer.start();
    }

    private void cancelTimeoutTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.stop();
            timeoutTimer = null;
        }
    }

    private void updateMetrics(long duration) {
        totalLoads++;
        totalLoadTime += duration;
        minLoadTime = Math.min(minLoadTime, duration);
        maxLoadTime = Math.max(maxLoadTime, duration);

        // Log si es excepcionalmente lento
        if (duration > 5000) {
            logConsumer.accept(String.format(
                    "⚠️ %s: Carga LENTA detectada (%d ms)",
                    moduleName, duration));
        }
    }

    private String sanitize(String origin) {
        return (origin == null || origin.isBlank()) ? "Desconocido" : origin;
    }
}
