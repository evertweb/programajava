package com.forestech.ui.utils;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.util.function.Consumer;

/**
 * Coordina solicitudes de carga para evitar saturar el EDT.
 * Ahora incluye cancelación de workers previos para evitar acumulación.
 */
public final class AsyncLoadManager {

    private final String moduleName;
    private final Consumer<String> logConsumer;
    private final Consumer<String> loadAction;

    private boolean loading;
    private String pendingOrigin;
    private String lastOrigin = "Inicial";

    // Referencia al SwingWorker actual (para poder cancelarlo)
    private SwingWorker<?, ?> currentWorker;

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
        logConsumer.accept(moduleName + ": solicitud de carga desde " + safeOrigin);

        // Ejecutar la acción de carga (el panel debe llamar registerWorker() antes de ejecutar)
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
        currentWorker = null; // Limpiar referencia al worker completado
        logConsumer.accept(moduleName + ": hilo EDT libre");

        if (pendingOrigin != null) {
            String origin = pendingOrigin;
            pendingOrigin = null;
            logConsumer.accept(String.format(
                "%s: ejecutando carga pendiente (%s) tras %.0f ms",
                moduleName,
                origin,
                (double) (System.currentTimeMillis() - startedAtMillis)
            ));
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
            currentWorker = null;
            loading = false;
        }
    }

    public String getLastOrigin() {
        return lastOrigin;
    }

    private String sanitize(String origin) {
        return (origin == null || origin.isBlank()) ? "Desconocido" : origin;
    }
}
