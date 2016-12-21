package org.anischyros.croak2.menuitems;

import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.tabs.*;

public class ShowSelectedTableMenuItem extends JMenuItem
{
    private Profile profile = null;
    private String databaseName= null;
    private String tableName = null;
    
    public ShowSelectedTableMenuItem()
    {
        super("Show table");
        addActionListener(e -> onAction());
        setEnabled(false);
    }
        
    private void onAction()
    {
        if (profile == null || databaseName == null || tableName == null)
            return;
        
        try
        {
            ShowTableTab tab = 
                new ShowTableTab(profile, databaseName, tableName);
            String tabTitle =
                String.format("%s.%s View", databaseName, tableName);
            InfoPanel.getInstance().addTab(tabTitle, tab);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
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
                "<html>Show table <font color=\"green\">%s:%s</font></html>", 
                databaseName, tableName));
        else
            setText("Show table");
        super.setEnabled(enabled);
    }
}
    
