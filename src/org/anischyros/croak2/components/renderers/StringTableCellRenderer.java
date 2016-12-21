package org.anischyros.croak2.components.renderers;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class StringTableCellRenderer extends JEditorPane
    implements TableCellRenderer
{
    private static final int MAX_ROW_HEIGHT = 7;
    
    private JTable table = null;
    private int row = -1;

    private String htmlText = "";
    private String rawText = "";
    private final String fontString;
    
    public StringTableCellRenderer()
    {
        super();
        setEditable(false);
        setContentType("text/html");
        fontString = "<font face=\"" + getFont().getName() + 
            "\" size=3 />";
    }

    private int calculateNumberOfRows(String rawText)
    {
        int count = 0;
        for (int i = 0; i < rawText.length(); i++)
        {
            if (rawText.charAt(i) == '\n')
                count++;
        }
        if (rawText.length() > 0 && rawText.charAt(rawText.length() - 1) == '\n')
            count--;
        if (count < 1)
             count = 1;
        return count;
    }
    
    private void adjustRowHeight(String rawText)
    {
        if (table == null)
             return;
        
        int nbrRows = calculateNumberOfRows(rawText);
        if (nbrRows > MAX_ROW_HEIGHT)
            nbrRows = MAX_ROW_HEIGHT;
            
        FontMetrics fm = 
            Toolkit.getDefaultToolkit().getFontMetrics(getFont());
        int desiredRowHeight = (int) (fm.getAscent() + fm.getDescent() + 
            fm.getLeading() + 0.5) * nbrRows + 5;
        if (table.getRowHeight(row) < desiredRowHeight)
            table.setRowHeight(row, desiredRowHeight);
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, 
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        this.table = table;
        this.row = row; 

        if (value != null)
            setText(value.toString());
        else
            setText("");
       setToolTipText(htmlText);
        if (isSelected)
            setBackground(table.getSelectionBackground());
        else
            setBackground(table.getBackground());
        
        return this;
    }
    
    public void setText(String text)
    {
        rawText = text;
        htmlText = "<html><body>" + fontString + 
            text.replace("\r", "").replace("\n", "<br>") + "</body></html>";
        super.setText(htmlText);
        adjustRowHeight(rawText);
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