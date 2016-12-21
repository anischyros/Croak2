package org.anischyros.croak2.dialogs;

import java.awt.*;
import javax.swing.*;

public class ShowCellContentsDialog extends JDialog
{
    final private JTextPane textArea;
    final private JScrollPane sp;
    final private CloseButton closeButton;
    
    private ShowCellContentsDialog(JFrame parent, String title, String contents)
    {
        super(parent, title);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setText(contents);
        textArea.setCaretPosition(0);
        
        sp = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        closeButton = new CloseButton();

        setModal(false);
        layoutComponents();
        pack();
        setSize(320, 180);
        
        SwingUtilities.invokeLater(() -> closeButton.requestFocus());
    }
    
    private void layoutComponents()
    {
        Box vbox = Box.createVerticalBox();
        vbox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        vbox.add(sp);
        vbox.add(Box.createVerticalStrut(10));
        Box hbox = Box.createHorizontalBox();
        hbox.add(Box.createHorizontalGlue());
        hbox.add(closeButton);
        hbox.add(Box.createHorizontalGlue());
        vbox.add(hbox);
        
        add(vbox);
    }
    
    private void center()
    {
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension fs = getSize();
        setLocation((ss.width - fs.width) / 2, (ss.height - fs.height) / 2);
    }
    
    public static void showDialog(Container parent, String title, 
        String contents)
    {
        while (parent != null && !(parent instanceof JFrame))
        {
            parent = parent.getParent();
        }
        if (parent == null)
            return;    // This should never happen
        
        ShowCellContentsDialog dialog = 
            new ShowCellContentsDialog((JFrame) parent, title, contents);
        dialog.center();
        dialog.setVisible(true);
    }
    
    private class CloseButton extends JButton
    {
        public CloseButton()
        {
            super("Close");
            addActionListener(e ->
            {
                ShowCellContentsDialog.this.setVisible(false);
                ShowCellContentsDialog.this.dispose();
            });
        }
    }
}
