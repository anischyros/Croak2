package org.anischyros.croak2.components.renderers;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

public class TimestampTableCellRenderer extends JTextField 
    implements TableCellRenderer
{
    private static DateFormat formatter = 
        new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    
    public TimestampTableCellRenderer()
    {
        setEditable(false);
        setBorder(BorderFactory.createEmptyBorder());
    }
        
    public Component getTableCellRendererComponent(JTable table, Object value, 
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        String formattedText = formatter.format(value);
        setText(formattedText);
        setToolTipText(formattedText);
        if (isSelected)
            setBackground(table.getSelectionBackground());
        else
            setBackground(table.getBackground());
        return this;
    }
    
    // Borowed from javax.swing.table.DefaultTableCellRenderer
    public boolean isOpaque() 
    {
        Color back = getBackground();
        Component p = getParent();
        if (p != null)
            p = p.getParent();

        // p should now be the JTable.
        boolean colorMatch = (back != null) && (p != null) &&
            back.equals(p.getBackground()) && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }
    
    // Overridden for performance reasons.
    public void invalidate() 
    {
    }

    // Overridden for performance reasons.
    public void validate() 
    {
    }

    // Overridden for performance reasons.
    public void revalidate() 
    {
    }

    // Overridden for performance reasons.
    public void repaint(long tm, int x, int y, int width, int height) 
    {
    }

    // Overridden for performance reasons.
    public void repaint(Rectangle r) 
    { 
    }

    // Overridden for performance reasons.
    public void repaint() 
    {
    }
}