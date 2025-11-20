package com.forestech.simpleui.util;

import com.forestech.simpleui.design.ThemeConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * NotificationManager
 * Handles visual feedback (Policy 3.2).
 * Uses a "Toast" style notification that appears and fades out.
 */
public class NotificationManager {

    public enum Type {
        INFO, SUCCESS, ERROR, WARNING
    }

    public static void show(JFrame frame, String message, Type type) {
        // Create a glass pane or a layered pane component
        // For simplicity in Swing, we'll use a temporary JWindow or a panel on the
        // GlassPane

        JDialog toast = new JDialog(frame);
        toast.setUndecorated(true);
        toast.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(getColorForType(type));

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(ThemeConstants.FONT_BOLD);
        panel.add(label);

        toast.add(panel);
        toast.pack();

        // Position: Bottom Center of the frame
        Point loc = frame.getLocationOnScreen();
        int x = loc.x + (frame.getWidth() - toast.getWidth()) / 2;
        int y = loc.y + frame.getHeight() - toast.getHeight() - 50;
        toast.setLocation(x, y);

        toast.setVisible(true);

        // Auto-close after 3 seconds
        Timer timer = new Timer(3000, (ActionEvent e) -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static Color getColorForType(Type type) {
        switch (type) {
            case SUCCESS:
                return ThemeConstants.SUCCESS_COLOR;
            case ERROR:
                return ThemeConstants.DANGER_COLOR;
            case WARNING:
                return ThemeConstants.WARNING_COLOR;
            case INFO:
            default:
                return ThemeConstants.PRIMARY_COLOR;
        }
    }
}
