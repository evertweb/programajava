package com.forestech.presentation.ui.core;

import com.forestech.presentation.ui.utils.DiagnosticLogger;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Manages navigation between different views (Cards) in the application.
 * Handles concurrency control, throttling, and view lifecycle events.
 */
public class NavigationManager {

    private static final long NAVIGATION_COOLDOWN_MS = 1200L;

    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final Consumer<String> logCallback;

    // Map of view names to their refresh actions
    private final Map<String, Runnable> viewRefreshActions = new HashMap<>();
    // Map of view names to their cancel actions (to stop loading)
    private final Map<String, Runnable> viewCancelActions = new HashMap<>();

    private String currentView = "dashboard";
    private final Map<String, Long> lastLoadTimeByView = new HashMap<>();

    // STRUCTURAL FIX: Global navigation lock
    private volatile boolean isNavigating = false;
    private String pendingNavigation = null;

    public NavigationManager(CardLayout cardLayout, JPanel mainContainer, Consumer<String> logCallback) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
        this.logCallback = logCallback;
    }

    /**
     * Registers a view with the navigation manager.
     *
     * @param viewName      Unique name for the view
     * @param panel         The JPanel to display
     * @param refreshAction Action to run when navigating to this view (can be null)
     * @param cancelAction  Action to run when navigating away from this view (can
     *                      be null)
     */
    public void registerView(String viewName, JPanel panel, Runnable refreshAction, Runnable cancelAction) {
        mainContainer.add(panel, viewName);
        if (refreshAction != null) {
            viewRefreshActions.put(viewName, refreshAction);
        }
        if (cancelAction != null) {
            viewCancelActions.put(viewName, cancelAction);
        }
    }

    public void navigateTo(String viewName) {
        navigateTo(viewName, false);
    }

    public void navigateTo(String viewName, boolean force) {
        if (viewName == null) {
            return;
        }

        // DIAGNOSTIC LOGGING
        DiagnosticLogger.log("NAV-MGR-1 >> Requesting navigation to: " + viewName);

        if (shouldIgnoreNavigation(viewName, force)) {
            DiagnosticLogger.log("NAV-MGR-2 >> Navigation ignored (cooldown/same view)");
            return;
        }

        // CONCURRENCY CONTROL
        if (isNavigating) {
            DiagnosticLogger.log("NAV-MGR-3 >> Queuing navigation (isNavigating=true)");
            pendingNavigation = viewName;
            log("Navegación a " + viewName + " ENCOLADA (navegación en curso)");
            return;
        }

        // ACQUIRE LOCK
        isNavigating = true;
        DiagnosticLogger.log("NAV-MGR-4 >> Lock acquired. Cancelling current view...");

        try {
            // 1. Cancel current view loading if any
            cancelCurrentViewLoad();

            // 2. Switch View
            DiagnosticLogger.log("NAV-MGR-5 >> Showing card...");
            cardLayout.show(mainContainer, viewName);
            currentView = viewName;

            // 3. Trigger Refresh
            DiagnosticLogger.log("NAV-MGR-6 >> Triggering refresh...");
            Runnable refresh = viewRefreshActions.get(viewName);
            if (refresh != null) {
                refresh.run();
            }

            lastLoadTimeByView.put(viewName, System.currentTimeMillis());
            DiagnosticLogger.log("NAV-MGR-7 >> Navigation complete.");

        } catch (Exception e) {
            DiagnosticLogger.log("NAV-MGR-ERROR >> " + e.getMessage());
            e.printStackTrace();
        } finally {
            // RELEASE LOCK & PROCESS PENDING
            SwingUtilities.invokeLater(() -> {
                isNavigating = false;
                if (pendingNavigation != null) {
                    String next = pendingNavigation;
                    pendingNavigation = null;
                    log("Procesando navegación pendiente: " + next);
                    navigateTo(next, force);
                }
            });
        }
    }

    private boolean shouldIgnoreNavigation(String viewName, boolean force) {
        if (force)
            return false;

        if (viewName.equals(currentView)) {
            return false; // Allow refreshing same view? Original logic said NO, but let's verify.
            // Original logic: if (!vista.equals(vistaActual)) return false; -> if
            // DIFFERENT, don't ignore.
            // So if SAME view, check cooldown?
            // Actually original logic was:
            // if (!vista.equals(vistaActual)) return false; (If switching views, proceed)
            // Wait, that means cooldown ONLY applied when refreshing the SAME view?
            // Let's re-read ForestechProfessionalApp.java:485
            // if (!vista.equals(vistaActual)) { return false; }
            // This implies: If I am going to a NEW view, I NEVER ignore it (no cooldown).
            // Cooldown only applies if I click the SAME view button again?

            // CORRECT LOGIC RE-INTERPRETATION:
            // The original code allowed rapid switching between DIFFERENT views.
            // It only throttled repeated clicks on the SAME view.
        }

        // If switching to a different view, always allow (unless locked by
        // isNavigating)
        if (!viewName.equals(currentView)) {
            return false;
        }

        long lastLoad = lastLoadTimeByView.getOrDefault(viewName, 0L);
        long now = System.currentTimeMillis();
        if (now - lastLoad < NAVIGATION_COOLDOWN_MS) {
            log(String.format("Navegación ignorada para %s (cooldown %.0f ms)",
                    viewName, (double) (NAVIGATION_COOLDOWN_MS - (now - lastLoad))));
            return true;
        }
        return false;
    }

    private void cancelCurrentViewLoad() {
        if (currentView != null) {
            Runnable cancel = viewCancelActions.get(currentView);
            if (cancel != null) {
                cancel.run();
            }
        }
    }

    private void log(String message) {
        if (logCallback != null) {
            logCallback.accept(message);
        }
    }

    public String getCurrentView() {
        return currentView;
    }
}
