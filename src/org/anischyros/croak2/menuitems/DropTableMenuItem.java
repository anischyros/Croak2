package org.anischyros.croak2.menuitems;

import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class DropTableMenuItem extends JMenuItem
{
    private final Profile profile;
    private final String databaseName;
    private final String tableName;
    
    public DropTableMenuItem(Profile profile, String databaseName,
        String tableName)
    {
       super("Drop table");
       this.profile = profile;
       this.databaseName = databaseName;
       this.tableName = tableName;
       addActionListener(e -> onAction(this.profile));
    }
        
    private void onAction(Profile profile)
    {
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
}
    
