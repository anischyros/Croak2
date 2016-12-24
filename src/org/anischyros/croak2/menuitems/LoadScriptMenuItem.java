package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.*;
import org.anischyros.croak2.tabs.*;

public class LoadScriptMenuItem extends JMenuItem
{
    private QueryDatabaseTab activeTabInstance = null;
    
    public LoadScriptMenuItem(MainFrame mainFrame)
    {
        super("Load script");
        setMnemonic('L');
        addActionListener(e -> activeTabInstance.loadScript());
        setEnabled(false);
    }
    
    public void setActiveTabInstance(QueryDatabaseTab instance)
    {
        activeTabInstance = instance;
    }
}
