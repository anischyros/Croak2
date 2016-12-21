package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.tabs.*;

public class PerformQueryMenuItem extends JMenuItem
{
    private Profile profile = null;
    private String databaseName = null;

    public PerformQueryMenuItem(Profile profile, String databaseName)
    {
        super("Perform query");
        setProfile(profile);
        setDatabaseName(databaseName);
        addActionListener(e -> onAction());
    }
        
    private void onAction()
    {
        QueryDatabaseTab tab = new QueryDatabaseTab(profile, databaseName);
        InfoPanel.getInstance().addTab("Unnamed.sql", tab);
    }
    
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }
    
    public Profile getProfile()
    {
        return profile;
    }
    
    public void setDatabaseName(String databaseName)
    {
        this.databaseName = databaseName;
    }
    
    public String getDatabaseName()
    {
        return databaseName;
    }
}
    
