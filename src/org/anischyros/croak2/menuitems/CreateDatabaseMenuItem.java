package org.anischyros.croak2.menuitems;

import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class CreateDatabaseMenuItem extends JMenuItem
{
    private Profile profile = null;
    
    public CreateDatabaseMenuItem()
    {
        super("Create database");
        addActionListener(e -> onAction());
        setEnabled(false);
    }
    
    public CreateDatabaseMenuItem(Profile profile)
    {
        super("Create database");
        addActionListener(e -> onAction());
        this.profile = profile;
    }

    private void onAction()
    {
        if (profile == null)
            return;
        
        String databaseName = JOptionPane.showInputDialog(this, 
            "Enter the name of the new database to be created", 
            "Create database", JOptionPane.PLAIN_MESSAGE);
        if (databaseName == null)
            return;
        
        String url = ProfileManager.createURLString(profile, "mysql");
        try (Connection c = DriverManager.getConnection(url))
        {
            Statement s = c.createStatement();
            s.execute(String.format("create database %s", databaseName));
        }
        catch (SQLException f)
        {
            JOptionPane.showMessageDialog(this, f.getMessage(), 
                "Database error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try
        {
            TreePanel.getInstance().addDatabase(profile, databaseName);
        }
        catch (SQLException f)
        {
            JOptionPane.showMessageDialog(null, f.getMessage());
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
}
    
