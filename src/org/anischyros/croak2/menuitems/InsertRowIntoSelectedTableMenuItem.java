package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.panels.InfoPanel;
import org.anischyros.croak2.profile.Profile;
import org.anischyros.croak2.tabs.*;

public class InsertRowIntoSelectedTableMenuItem extends JMenuItem
{
    private Profile profile = null;
    private String databaseName = null;
    private String tableName = null;
    
    public InsertRowIntoSelectedTableMenuItem()
    {
        super("Insert row");
        addActionListener(e -> onAction());
        setEnabled(false);
    }
        
    public void onAction()
    {
        if (profile == null || databaseName == null || tableName == null)
            return;
        
        InsertRowTab tab = new InsertRowTab(profile, databaseName, tableName);
        String tabTitle =
            String.format("Insert into %s.%s", databaseName, tableName);
        InfoPanel.getInstance().addTab(tabTitle, tab);
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
    
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }
    
    public String getTableName()
    {
        return tableName;
    }
    
    public void setEnabled(boolean enabled)
    {
        if (enabled)
            setText(String.format(
                "<html>Insert row into <font color=\"green\">%s:%s</font>" +
                "</html>", databaseName, tableName));
        else
            setText("Insert row");
        super.setEnabled(enabled);
    }}
