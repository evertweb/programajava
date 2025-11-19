package com.forestech.presentation.ui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Sistema de bloqueo global de UI para evitar múltiples operaciones concurrentes.
 *
 * <p><strong>Problema que resuelve:</strong></p>
 * <ul>
 *   <li>Usuario hace clic rápido en múltiples pestañas</li>
 *   <li>Se acumulan operaciones de BD</li>
 *   <li>Pool de conexiones/threads se satura</li>
 *   <li>Aplicación se congela</li>
 * </ul>
 *
 * <p><strong>Solución:</strong></p>
 * <ul>
 *   <li>Bloquea TODA la UI mientras hay operación en curso</li>
 *   <li>Muestra mensajes de estado claros</li>
 *   <li>Libera UI solo cuando la operación termina</li>
 * </ul>
 */
public class GlobalUIBlocker {

    private static GlobalUIBlocker instance;

    private final JFrame owner;
    private final Consumer<String> logger;
    private final JPanel glassPane;
    private final JLabel lblStatus;
    private final JProgressBar progressBar;
    private final AtomicBoolean isBlocked = new AtomicBoolean(false);
    private final AtomicInteger operationCount = new AtomicInteger(0);

    // Timeout para auto-desbloqueo de seguridad (10 segundos - más agresivo)
    private Timer safetyTimer;
    private static final int SAFETY_TIMEOUT_MS = 10000;

    private GlobalUIBlocker(JFrame owner, Consumer<String> logger) {
        this.owner = owner;
        this.logger = logger;

        // Crear el GlassPane bloqueador
        glassPane = new JPanel(new GridBagLayout());
        glassPane.setOpaque(false);
        glassPane.setBackground(new Color(0, 0, 0, 100)); // Semi-transparente

        // Consumir todos los eventos del mouse para bloquear interacción
        glassPane.addMouseListener(new MouseAdapter() {});
        glassPane.addMouseMotionListener(new MouseAdapter() {});

        // Bloquear teclas
        glassPane.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                e.consume();
            }
        });
        glassPane.setFocusable(true);

        // Panel central con mensaje de estado
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(ColorScheme.BACKGROUND_PANEL);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.PRIMARY, 2),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));

        lblStatus = new JLabel("Procesando...");
        lblStatus.setFont(FontScheme.SUBHEADER);
        lblStatus.setForeground(ColorScheme.PRIMARY);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 20));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblWait = new JLabel("Por favor espera...");
        lblWait.setFont(FontScheme.SMALL_ITALIC);
        lblWait.setForeground(ColorScheme.FOREGROUND_SECONDARY);
        lblWait.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(lblStatus);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(progressBar);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(lblWait);

        glassPane.add(centerPanel);
    }

    /**
     * Inicializa el bloqueador global. Debe llamarse una vez al inicio.
     */
    public static void initialize(JFrame owner, Consumer<String> logger) {
        if (instance == null) {
            instance = new GlobalUIBlocker(owner, logger);
            owner.setGlassPane(instance.glassPane);
            logger.accept("[UIBlocker] Inicializado correctamente");
        }
    }

    /**
     * Obtiene la instancia del bloqueador.
     */
    public static GlobalUIBlocker getInstance() {
        return instance;
    }

    /**
     * Bloquea la UI mostrando un mensaje de estado.
     *
     * @param message Mensaje a mostrar (ej: "Cargando productos...")
     */
    public void block(String message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> block(message));
            return;
        }

        int count = operationCount.incrementAndGet();

        // LOG A ARCHIVO
        DiagnosticLogger.logBlock(message, count,
            com.forestech.config.HikariCPDataSource.getPoolStats());

        if (isBlocked.compareAndSet(false, true)) {
            lblStatus.setText(message);
            glassPane.setVisible(true);
            glassPane.requestFocus();
            owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Iniciar timer de seguridad
            startSafetyTimer();

            logger.accept("[UIBlocker] BLOQUEADO: " + message + " (ops: " + count + ")");
        } else {
            // Ya bloqueado, solo actualizar mensaje
            lblStatus.setText(message);
            logger.accept("[UIBlocker] Actualizado: " + message + " (ops: " + count + ")");
        }
    }

    /**
     * Desbloquea la UI y muestra mensaje de completado.
     *
     * @param successMessage Mensaje opcional de éxito (puede ser null)
     */
    public void unblock(String successMessage) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> unblock(successMessage));
            return;
        }

        int count = operationCount.decrementAndGet();

        // LOG A ARCHIVO
        DiagnosticLogger.logUnblock(successMessage, count,
            com.forestech.config.HikariCPDataSource.getPoolStats());

        // Solo desbloquear si no hay más operaciones pendientes
        if (count <= 0) {
            operationCount.set(0); // Reset por seguridad

            if (isBlocked.compareAndSet(true, false)) {
                cancelSafetyTimer();
                glassPane.setVisible(false);
                owner.setCursor(Cursor.getDefaultCursor());

                String msg = successMessage != null ? successMessage : "Operacion completada";
                logger.accept("[UIBlocker] DESBLOQUEADO: " + msg);

                // Mostrar notificación breve de éxito
                showCompletionNotification(msg);
            }
        } else {
            logger.accept("[UIBlocker] Operación completada, pero quedan " + count + " pendientes");
        }
    }

    /**
     * Desbloquea forzadamente (para errores o timeouts).
     */
    public void forceUnblock(String reason) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> forceUnblock(reason));
            return;
        }

        operationCount.set(0);
        cancelSafetyTimer();

        if (isBlocked.compareAndSet(true, false)) {
            glassPane.setVisible(false);
            owner.setCursor(Cursor.getDefaultCursor());
            logger.accept("[UIBlocker] FORZADO: " + reason);
        }
    }

    /**
     * Verifica si la UI está bloqueada.
     */
    public boolean isBlocked() {
        return isBlocked.get();
    }

    /**
     * Obtiene el contador de operaciones activas.
     */
    public int getActiveOperations() {
        return operationCount.get();
    }

    private void startSafetyTimer() {
        cancelSafetyTimer();

        safetyTimer = new Timer(SAFETY_TIMEOUT_MS, e -> {
            logger.accept("[UIBlocker] TIMEOUT de seguridad alcanzado (" + SAFETY_TIMEOUT_MS/1000 + "s)");
            forceUnblock("Timeout de seguridad");
        });
        safetyTimer.setRepeats(false);
        safetyTimer.start();
    }

    private void cancelSafetyTimer() {
        if (safetyTimer != null) {
            safetyTimer.stop();
            safetyTimer = null;
        }
    }

    private void showCompletionNotification(String message) {
        // Crear un pequeño toast notification que desaparece
        JWindow toast = new JWindow(owner);
        JLabel label = new JLabel("  " + message + "  ");
        label.setFont(FontScheme.BODY_REGULAR);
        label.setForeground(Color.WHITE);
        label.setBackground(new Color(46, 125, 50)); // Verde éxito
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        toast.add(label);
        toast.pack();

        // Posicionar en la parte inferior central
        Point ownerLocation = owner.getLocationOnScreen();
        int x = ownerLocation.x + (owner.getWidth() - toast.getWidth()) / 2;
        int y = ownerLocation.y + owner.getHeight() - toast.getHeight() - 50;
        toast.setLocation(x, y);

        toast.setVisible(true);

        // Desaparecer después de 2 segundos
        Timer hideTimer = new Timer(2000, e -> {
            toast.setVisible(false);
            toast.dispose();
        });
        hideTimer.setRepeats(false);
        hideTimer.start();
    }
}
