package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.*;

public class SaveScriptMenuItem extends JMenuItem
{
    public SaveScriptMenuItem(MainFrame mainFrame)
    {
        super("Save script");
        setMnemonic('S');
        addActionListener(e ->
        {
        });
        setEnabled(false);
    }
    
}
