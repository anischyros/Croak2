package org.anischyros.croak2.menuitems;

import javax.swing.*;

public class RevertMenuItem extends JMenuItem
{
    public RevertMenuItem()
    {
        super("Revert");
        addActionListener(e -> onAction());
        setEnabled(false);
    }
    
    private void onAction()
    {
        
    }
}
