package org.anischyros.croak2.menuitems;

import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class DropSelectedDatabaseMenuItem extends JMenuItem
{
    private Profile profile = null;
    private String databaseName = null;
    
    public DropSelectedDatabaseMenuItem()
    {
       super("Drop database");
       addActionListener(e -> onAction());
       setEnabled(false);
    }
    
    private void onAction()
    {
        if (profile == null || databaseName == null)
            return;
        
        int response = JOptionPane.showConfirmDialog(this,
            String.format("Are you sure you want to drop database \"%s\"?\n" +
                "This change will be permanent and will take effect " +
                "immediately.", databaseName), "Drop database", 
                JOptionPane.OK_CANCEL_OPTION);
        if (response != JOptionPane.OK_OPTION)
            return;
        
        String url = ProfileManager.createURLString(profile, "mysql");
        try (Connection c = DriverManager.getConnection(url))
        {
            Statement s = c.createStatement();
            s.execute(String.format("drop database %s", databaseName));
            TreePanel.getInstance().dropDatabase(profile, databaseName);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Database error", JOptionPane.ERROR_MESSAGE);
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

    public void setEnabled(boolean enabled)
    {
        if (profile != null && enabled)
            setText(String.format("<html>Drop database <font color=\"green\">" +
                "%s</font><html>", databaseName));
        else
        if (!enabled)
            setText("Drop database");
        super.setEnabled(enabled);
    }
}
    
