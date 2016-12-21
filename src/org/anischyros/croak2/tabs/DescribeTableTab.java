package org.anischyros.croak2.tabs;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.utils.*;

public class DescribeTableTab extends CustomTab
{
    private final Connection connection;
    private final String sqlQuery;
    private final SqlTableModel tableModel;
    private final DatabaseTable table;
    private final ReloadTableButton reloadTableButton;
    
     public DescribeTableTab(Profile profile, String databaseName, 
        String tableName) throws SQLException
    {
        super(profile, databaseName);
        
        sqlQuery = String.format("describe %s", tableName);
        
        connection = Utils.createDatabaseConnection(profile, databaseName);
        
        tableModel = new SqlTableModel(connection);
        table = new DatabaseTable(tableModel);
        reloadTableButton = new ReloadTableButton();
        
        layoutComponents();
        if (!tableModel.makeQuery(sqlQuery))
        {
            JOptionPane.showMessageDialog(null, 
                tableModel.getThrownSQLException().getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
     
    private Component layoutButtons()
    {
        Box hbox = Box.createHorizontalBox();
        hbox.add(Box.createHorizontalGlue());
        hbox.add(reloadTableButton);
        hbox.add(Box.createHorizontalGlue());

        hbox.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        return hbox;
    }
    
    private void layoutComponents()
    {
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
  
        JScrollPane sp = new JScrollPane(table, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        add(sp, BorderLayout.CENTER);
        add(layoutButtons(), BorderLayout.SOUTH);
    }
    
    public void disposeResources()
    {
        try
        {
            connection.close();
            tableModel.disposeResources();
        }
        catch (SQLException e)
        {
        }
    }
   
    private class ReloadTableButton extends JButton
    {
        public ReloadTableButton()
        {
            super("Reload Table");
            setToolTipText("Reload this table");
            addActionListener(e -> 
            {
                if (!tableModel.makeQuery(sqlQuery))
                {
                    JOptionPane.showMessageDialog(null, 
                        tableModel.getThrownSQLException().getMessage(), 
                        "Error",  JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }}
