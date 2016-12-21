package org.anischyros.croak2.menuitems;

import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class DropSelectedTableMenuItem extends JMenuItem
{
    private Profile profile = null;
    private String databaseName = null;
    private String tableName = null;
    
    public DropSelectedTableMenuItem()
    {
       super("Drop table");
       addActionListener(e -> onAction());
       setEnabled(false);
    }
        
    private void onAction()
    {
        if (profile == null || databaseName == null || tableName == null)
            return;
        
        int response = JOptionPane.showConfirmDialog(this,
            String.format("Are you sure you want to drop table \"%s\" from " +
                "database %s?\nThis change will be permanent and will take " +
                "effect immediately.", tableName, databaseName), "Drop table", 
                JOptionPane.OK_CANCEL_OPTION);
        if (response != JOptionPane.OK_OPTION)
            return;
        
        String url = ProfileManager.createURLString(profile, databaseName);
        try (Connection c = DriverManager.getConnection(url))
        {
            Statement s = c.createStatement();
            s.execute(String.format("drop table %s", tableName));
            TreePanel.getInstance().dropTable(profile, databaseName, tableName);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
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
                "<html>Drop table <font color=\"green\">%s:%s</font></html>", 
                databaseName, tableName));
        else
            setText("Drop table");
        super.setEnabled(enabled);
    }
}
    
