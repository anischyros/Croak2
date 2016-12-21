package org.anischyros.croak2.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import org.anischyros.croak2.components.renderers.*;
import org.anischyros.croak2.dialogs.*;

public class DatabaseTable extends JTable
{
    public DatabaseTable(SqlTableModel model)
    {
        super(model);
        
        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setModel(model);
        
        this.addMouseListener(new CustomMouseListener());
    }
    
    public void createDefaultColumnsFromModel()
    {
        super.createDefaultColumnsFromModel();
        for (int i = 0; i < getModel().getColumnCount(); i++)
        {
            // Set cell header renderer for column
            TableColumn tableColumn = getColumnModel().getColumn(i);
            CustomHeaderCellRenderer renderer = new CustomHeaderCellRenderer();
            tableColumn.setHeaderRenderer(renderer);
            
            // Set preferred width table for column
            FontMetrics fm = 
                Toolkit.getDefaultToolkit().getFontMetrics(renderer.getFont());
            int width = fm.stringWidth(getModel().getColumnName(i)) + 40;
            tableColumn.setPreferredWidth(width);
            tableColumn.setMinWidth(width);
        }
    }
    
    private class CustomMouseListener extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            if (e.getButton() != MouseEvent.BUTTON3)
                return;
            
            Point point = e.getPoint();
            int row = DatabaseTable.this.rowAtPoint(point);
            int col = DatabaseTable.this.columnAtPoint(point);
            TableModel tm = DatabaseTable.this.getModel();
            Object value = tm.getValueAt(row, col);
            String text = (value != null ? value.toString() : "null");

            ShowCellContentsDialog.showDialog(DatabaseTable.this, 
                tm.getColumnName(col), text);
        }
    }
}
