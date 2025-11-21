package com.forestech.simpleui.design;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * FTextField
 * A composite component containing a Label and a TextField.
 * Implements Policy 1.3 (Proximity) and 3.3 (Error Prevention).
 */
public class FTextField extends JPanel {

    private final JLabel label;
    private final JTextField textField;
    private final JLabel errorLabel;

    public FTextField(String labelText) {
        setLayout(new BorderLayout(0, 5)); // Vertical gap
        setOpaque(false);

        // Label
        label = new JLabel(labelText);
        label.setFont(ThemeConstants.FONT_SMALL);
        label.setForeground(ThemeConstants.TEXT_MUTED);
        add(label, BorderLayout.NORTH);

        // TextField
        textField = new JTextField();
        textField.setFont(ThemeConstants.FONT_REGULAR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8) // Inner padding
        ));
        add(textField, BorderLayout.CENTER);

        // Error Label (Hidden by default)
        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeConstants.FONT_SMALL);
        errorLabel.setForeground(ThemeConstants.DANGER_COLOR);
        add(errorLabel, BorderLayout.SOUTH);

        setupEvents();
    }

    private void setupEvents() {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeConstants.PRIMARY_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(7, 7, 7, 7)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeConstants.BORDER_COLOR, 1, true),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        });
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setError(String message) {
        if (message == null || message.isEmpty()) {
            errorLabel.setText(" ");
        } else {
            errorLabel.setText(message);
        }
    }

    public void clearError() {
        setError(null);
    }

    public void addDocumentListener(javax.swing.event.DocumentListener listener) {
        textField.getDocument().addDocumentListener(listener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    public void setHorizontalAlignment(int alignment) {
        textField.setHorizontalAlignment(alignment);
    }

    public void requestFocus() {
        textField.requestFocus();
    }
}
