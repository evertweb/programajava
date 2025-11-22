package com.forestech.simpleui.design;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * FCard
 * A container with a white background, rounded corners, and subtle shadow.
 * Used to group related content (Policy 1.3 Proximity).
 */
public class FCard extends JPanel {

    public FCard() {
        setOpaque(false); // We paint the background
        setBorder(javax.swing.BorderFactory.createEmptyBorder(ThemeConstants.PADDING_MEDIUM,
                ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow (simplified)
        g2.setColor(new java.awt.Color(200, 200, 200));
        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, ThemeConstants.BORDER_RADIUS,
                ThemeConstants.BORDER_RADIUS);

        // Background
        g2.setColor(ThemeConstants.CARD_BACKGROUND);
        g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, ThemeConstants.BORDER_RADIUS,
                ThemeConstants.BORDER_RADIUS);

        g2.dispose();
        super.paintComponent(g);
    }
}
