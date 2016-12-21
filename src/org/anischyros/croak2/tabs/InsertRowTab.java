package org.anischyros.croak2.tabs;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.utils.*;

public class InsertRowTab extends CustomTab
{
    private final String tableName;
    private final JComboBox columnNamesList;
    private final UndoableTextField valueField;
    private final CustomTableModel tableModel;
    private final JTable columnValueTable;
    private final AddButton addButton;
    private final RemoveButton removeButton;
    private final InsertIntoDatabaseButton insertButton;
    
    public InsertRowTab(Profile profile, String databaseName, String tableName)
    {
        super(profile, databaseName);
        this.tableName = tableName;
        
        columnNamesList = new JComboBox(getColumnNames());
        valueField = new UndoableTextField(16);
        tableModel = new CustomTableModel();
        columnValueTable = new JTable(tableModel);
        columnValueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        columnValueTable.getSelectionModel().addListSelectionListener(
            new TableListSelectionListener());
        addButton = new AddButton();
        removeButton = new RemoveButton();
        insertButton = new InsertIntoDatabaseButton();
       
        layoutComponents();
    }
    
    public void disposeResources()
    {
    }
   
    private String[] getColumnNames()
    {
        String[] columnNames;
        
        try (Connection c = 
            Utils.createDatabaseConnection(getProfile(), getDatabaseName()))
        {
            try (Statement s = c.createStatement())
            {
                String query = 
                    String.format("select * from %s where 1 != 1", tableName);
                try (ResultSet rs = s.executeQuery(query))
                {
                    ResultSetMetaData md = rs.getMetaData();
                    columnNames = new String[md.getColumnCount()];
                    for (int i = 0; i < columnNames.length; i++)
                        columnNames[i] = md.getColumnName(i + 1);
                    
                    Arrays.sort(columnNames);
                    return columnNames;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return new String[0];
        }
    }
   
    private void layoutComponents()
    {
        setLayout(new BorderLayout());
        
        Box valueBox = Box.createHorizontalBox();
        valueBox.add(new JLabel("Column"));
        valueBox.add(Box.createHorizontalStrut(5));
        valueBox.add(columnNamesList);
        valueBox.add(Box.createHorizontalStrut(10));
        valueBox.add(new JLabel("Value"));
        valueBox.add(Box.createHorizontalStrut(5));
        valueBox.add(valueField);
        valueBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(valueBox, BorderLayout.NORTH);
        
        JScrollPane sp = new JScrollPane(columnValueTable,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(sp, BorderLayout.CENTER);
        
        Box buttonsBox = Box.createHorizontalBox();
        buttonsBox.add(Box.createHorizontalGlue());
        buttonsBox.add(addButton);
        buttonsBox.add(Box.createHorizontalStrut(10));
        buttonsBox.add(removeButton);
        buttonsBox.add(Box.createHorizontalStrut(50));
        buttonsBox.add(insertButton);
         buttonsBox.add(Box.createHorizontalGlue());
        buttonsBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(buttonsBox, BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private String addQuotes(String value)
    {
        StringBuilder buf = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++)
        {
            char ch = value.charAt(i);
            if (ch == '"')
                buf.append("\\\"");
            else
                buf.append(ch);
        }
        buf.append('"');
        return buf.toString();
    }

    private boolean insertIntoDatabase()
    {
        StringBuilder query = 
            new StringBuilder("insert into " + tableName + "(");
        
        for (int row = 0; row < tableModel.getRowCount(); row++)
        {
            if (row > 0)
                query.append(", ");
            query.append(tableModel.getValueAt(row, 0));
        }
        query.append(") values (");
        for (int row = 0; row < tableModel.getRowCount(); row++)
        {
            if (row > 0)
                query.append(", ");
            query.append(addQuotes(tableModel.getValueAt(row, 1).toString()));
        }
        query.append(")");
        
        try (Connection c = 
            Utils.createDatabaseConnection(getProfile(), getDatabaseName()))
        {
            try (Statement s = c.createStatement())
            {
                s.execute(query.toString());
                return true;
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private class CustomTableModel implements TableModel
    {
        private final List<TableModelListener> listenerList = new ArrayList<>();
        private final List<String> nameColumnList = new ArrayList<>();
        private final List<String> valueList = new ArrayList<>();
        
        public void addTableModelListener(TableModelListener listener)
        {
            listenerList.add(listener);
        }

        public void removeTableModelListener(TableModelListener listener)
        {
            listenerList.remove(listener);
        }

        public Class<?> getColumnClass(int columnIndex)
        {
            return String.class;
        }

        public int getColumnCount()
        {
            return 2;
        }

        public String getColumnName(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    return "Column name";
                case 1:
                    return "Value";
                default:
                    return null;
            }
        }

        public int getRowCount()
        {
            return nameColumnList.size();
        }
        
        public void setValueAt(Object value, int rowIndex, int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    nameColumnList.set(rowIndex, value.toString());
                    break;
                case 1:
                    valueList.set(rowIndex, value.toString());
                    break;
            }
        }
        
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    return nameColumnList.get(rowIndex);
                case 1:
                    return valueList.get(rowIndex);
                default:
                    return null;
            }
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return false;
        }
        
        public void addRow(String columnName, String value)
        {
            // Search for existing row
            int index = 0;
            boolean found = false;
            for (String entry: nameColumnList)
            {
                if (entry.equals(columnName))
                {
                    found = true;
                    break;
                }
                index++;
            }
            
            // Update table model
            if (found)
            {
                nameColumnList.set(index, columnName);
                valueList.set(index, value);
            }
            else
            {
                nameColumnList.add(columnName);
                valueList.add(value);
            }
            
            // Notify listeners
            for (TableModelListener listener: listenerList)
                listener.tableChanged(new TableModelEvent(this));
        }
        
        public void removeRow(int rowNbr)
        {
            nameColumnList.remove(rowNbr);
            valueList.remove(rowNbr);
            for (TableModelListener listener: listenerList)
                listener.tableChanged(new TableModelEvent(this));
        }
        
        public void removeAllRows()
        {
            nameColumnList.clear();
            valueList.clear();
            for (TableModelListener listener: listenerList)
                listener.tableChanged(new TableModelEvent(this));
        }
    }
    
    private class AddButton extends JButton
    {
        public AddButton()
        {
            super("Add");
            setMnemonic(KeyEvent.VK_A);
            addActionListener(e -> doAction());
        }
        
        private void doAction()
        {
            String columnName = columnNamesList.getSelectedItem().toString();
            String value = valueField.getText();
            tableModel.addRow(columnName, value);
            insertButton.setEnabled(true);

        }
    }
    
    private class RemoveButton extends JButton
    {
        public RemoveButton()
        {
            super("Remove");
            setMnemonic(KeyEvent.VK_R);
            setEnabled(false);
            addActionListener(e -> doAction());
        }
        
        private void doAction()
        {
            int selectedRow = columnValueTable.getSelectedRow();
            tableModel.removeRow(selectedRow);
            removeButton.setEnabled(false);
            insertButton.setEnabled(tableModel.getRowCount() > 0);
        }
    }
    
    private class InsertIntoDatabaseButton extends JButton
    {
        public InsertIntoDatabaseButton()
        {
            super("Insert into database");
            setMnemonic(KeyEvent.VK_I);
            setEnabled(false);
            addActionListener(e ->
            {
                if (insertIntoDatabase())
                    tableModel.removeAllRows();
            });
        }
    }
    
    private class TableListSelectionListener implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent event)
        {
            removeButton.setEnabled(true);
        }
    }
}
