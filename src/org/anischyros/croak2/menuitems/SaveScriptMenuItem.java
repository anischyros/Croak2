package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.*;
import org.anischyros.croak2.tabs.*;

public class SaveScriptMenuItem extends JMenuItem
{
    private QueryDatabaseTab activeTabInstance = null;

    public SaveScriptMenuItem(MainFrame mainFrame)
    {
        super("Save script");
        setMnemonic('S');
        addActionListener(e -> activeTabInstance.saveCurrentScript());
        setEnabled(false);
    }
    
    public void setActiveTabInstance(QueryDatabaseTab instance)
    {
        activeTabInstance = instance;
    }
}
