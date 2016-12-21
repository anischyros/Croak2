package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.*;

public class LoadScriptMenuItem extends JMenuItem
{
    public LoadScriptMenuItem(MainFrame mainFrame)
    {
        super("Load script");
        setMnemonic('L');
        addActionListener(e ->
        {
        });
        setEnabled(false);
    }
}
