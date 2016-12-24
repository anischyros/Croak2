package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.*;
import org.anischyros.croak2.tabs.*;

public class SaveScriptAsMenuItem extends JMenuItem
{
    private QueryDatabaseTab activeTabInstance = null;

    public SaveScriptAsMenuItem(MainFrame mainFrame)
    {
        super("Save script as");
        setMnemonic('S');
        addActionListener(e -> activeTabInstance.saveCurrentScriptInNewFile());
        setEnabled(false);
    }
     
    public void setActiveTabInstance(QueryDatabaseTab instance)
    {
        activeTabInstance = instance;
    }
}
