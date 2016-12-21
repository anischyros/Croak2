package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.panels.InfoPanel;
import org.anischyros.croak2.profile.Profile;
import org.anischyros.croak2.tabs.*;

public class InsertRowMenuItem extends JMenuItem
{
    private final Profile profile;
    private final String databaseName;
    private final String tableName;
    
    public InsertRowMenuItem(Profile profile, String databaseName,
        String tableName)
    {
        super("Insert row");
        this.profile = profile;
        this.databaseName = databaseName;
        this.tableName = tableName;
        addActionListener(e -> onAction(this.profile));
    }
        
    public void onAction(Profile profile)
    {
        InsertRowTab tab = new InsertRowTab(profile, databaseName, tableName);
        String tabTitle =
            String.format("Insert into %s.%s", databaseName, tableName);
        InfoPanel.getInstance().addTab(tabTitle, tab);
    }
}
