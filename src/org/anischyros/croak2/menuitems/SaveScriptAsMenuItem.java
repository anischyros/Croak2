package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.*;

public class SaveScriptAsMenuItem extends JMenuItem
{
    public SaveScriptAsMenuItem(MainFrame mainFrame)
    {
        super("Save script as");
        setMnemonic('S');
        addActionListener(e ->
        {
        });
        setEnabled(false);
    }
    
}
