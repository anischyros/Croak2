package org.anischyros.croak2.menuitems;

import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.tabs.*;

public class ShowDescriptionMenuItem extends JMenuItem
{
    private final Profile profile;
    private final String databaseName;
    private final String tableName;
    
    public ShowDescriptionMenuItem(Profile profile, String databaseName,
        String tableName)
    {
        super("Show Description");
        this.profile = profile;
        this.databaseName = databaseName;
        this.tableName = tableName;
        addActionListener(e -> onAction(this.profile));
    }
        
    private void onAction(Profile profile)
    {
        try
        {
            DescribeTableTab tab = 
                new DescribeTableTab(profile, databaseName, tableName);
            String tabTitle =
                String.format("%s.%s Description", databaseName, tableName);
            InfoPanel.getInstance().addTab(tabTitle, tab);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

