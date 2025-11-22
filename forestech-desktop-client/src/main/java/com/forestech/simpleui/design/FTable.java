package com.forestech.simpleui.design;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * FTable
 * A styled JTable.
 * Policy 1.1 (Consistency).
 */
public class FTable extends JTable {

    public FTable() {
        setShowVerticalLines(true);
        setShowHorizontalLines(true);
        setGridColor(ThemeConstants.BORDER_COLOR);
        setRowHeight(30);
        setIntercellSpacing(new Dimension(1, 1));
        setFillsViewportHeight(true);
        setFont(ThemeConstants.FONT_REGULAR);
        setSelectionBackground(ThemeConstants.PRIMARY_COLOR.brighter());
        setSelectionForeground(Color.WHITE);

        JTableHeader header = getTableHeader();
        header.setFont(ThemeConstants.FONT_BOLD);
        header.setBackground(Color.WHITE);
        header.setForeground(ThemeConstants.TEXT_MUTED);
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeConstants.BORDER_COLOR));
    }

    public void setModel(Object[][] data, String[] columns) {
        setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }
}
