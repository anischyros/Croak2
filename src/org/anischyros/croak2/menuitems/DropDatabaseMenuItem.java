package org.anischyros.croak2.menuitems;

import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class DropDatabaseMenuItem extends JMenuItem
{
    private final Profile profile;
    private final String databaseName;
    
    public DropDatabaseMenuItem(Profile profile, String databaseName)
    {
       super("Drop database");
       this.profile = profile;
       this.databaseName = databaseName;
       addActionListener(e -> onAction());
    }
        
    private void onAction()
    {
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
}
    
