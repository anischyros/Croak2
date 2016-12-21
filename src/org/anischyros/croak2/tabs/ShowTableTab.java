package org.anischyros.croak2.tabs;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.utils.*;

public class ShowTableTab extends CustomTab
{
    private final Connection connection;
    private final String sqlQuery;
    private final ReloadTableButton reloadTableButton;
    private final DeleteAllRowsButton deleteAllRowsButton;
    private final SqlTableModel tableModel;
    private final DatabaseTable table;
    private final String tableName;
    
     public ShowTableTab(Profile profile, String databaseName, String tableName)
        throws SQLException
    {
        super(profile, databaseName);
        
        sqlQuery = String.format("select * from %s", tableName);
        
        connection = Utils.createDatabaseConnection(profile, databaseName);
        
        reloadTableButton = new ReloadTableButton();
        deleteAllRowsButton = new DeleteAllRowsButton();
        tableModel = new SqlTableModel(connection);
        table = new DatabaseTable(tableModel);
        this.tableName = tableName;
        
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
        hbox.add(Box.createHorizontalStrut(10));
        hbox.add(deleteAllRowsButton);
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
    }

    private class DeleteAllRowsButton extends JButton
    {
        public DeleteAllRowsButton()
        {
            super("Delete all rows");
            setMnemonic(KeyEvent.VK_D);
            addActionListener(e -> doAction());
        }
        
        private void doAction()
        {
            int response = JOptionPane.showConfirmDialog(null, 
                "Are you sure you want to delete all rows from this table?", 
                "Delete all rows", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (response != JOptionPane.YES_OPTION)
                return;
            
            try (Connection c = 
                Utils.createDatabaseConnection(getProfile(), getDatabaseName()))
            {
                try (Statement s = c.createStatement())
                {
                    String query = 
                        String.format("delete from %s where 0 = 0", tableName);
                    s.execute(query);
                    tableModel.makeQuery(sqlQuery);
                }
            }
            catch (SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }    
}
