package org.anischyros.croak2.components;

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class SqlTableModel implements TableModel
{
    public static enum SortOrder { ASCENDING, DESCENDING };
    
    private final Connection connection;

    Statement statement = null;
    ResultSet resultSet = null;
    SQLException thrownSQLException = null;
    
    private final List<TableModelListener> tableModelListenerList =
        new ArrayList<>();
    private List<String> columnNameList;
    private List<Class> columnClassList;
    private int rowCount = 0;
    
    private final Object lock = new Object();
    
    public SqlTableModel(Connection connection)
    {
        this.connection = connection;
   }
    
    public void disposeResources()
    {
        synchronized(lock)
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                    resultSet = null;
                }
            }
            catch (SQLException e)
            {
            }
            try
            {
                if (statement != null)
                {
                    statement.close();
                    statement = null;
                }
            }
            catch (SQLException e)
            {
            }
            
            rowCount = 0;
            thrownSQLException = null;
            columnNameList = new ArrayList<>();
            columnClassList = new ArrayList<>();
            
            fireTableStructureChangedNotification();    

        }
   }
    
    private void loadColumnData(ResultSet resultSet)
        throws SQLException
    {
        // Get column names and column classes
        ResultSetMetaData metaData = resultSet.getMetaData();
        int nbrColumns = metaData.getColumnCount();
        columnNameList = new ArrayList<>(nbrColumns);
        columnClassList = new ArrayList<>(nbrColumns);
        for (int i = 1; i <= nbrColumns; i++)
        {
            columnNameList.add(metaData.getColumnName(i));
            columnClassList.add(createClassObject(
                metaData.getColumnClassName(i)));
        }
    }
    
    public boolean makeQuery(String query)
    {
        try
        {
            thrownSQLException = null;
            disposeResources();
            statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery(query);
            loadColumnData(resultSet);
            if (resultSet.last())
                rowCount = resultSet.getRow();
            else
                rowCount = 0;
            return true;
        }
        catch (SQLException e)
        {
            thrownSQLException = e;
            rowCount = 0;
            columnNameList = new ArrayList<>(0);
            columnClassList = new ArrayList<>(0);
            return false;
        }
        finally
        {
            fireTableStructureChangedNotification();    
        }
    }
    
    public SQLException getThrownSQLException()
    {
        return thrownSQLException;
    }
    
    public Class<?> getColumnClass(int columnIndex)
    {
        synchronized(lock)
        {
            if (columnClassList == null)
                return Object.class;
            return columnClassList.get(columnIndex);
        }
    }

    public int getColumnCount() 
    {
        synchronized(lock)
        {
            if (columnNameList == null)
                return 0;
            return columnNameList.size();
        }
    }

    public String getColumnName(int index) 
    {
        synchronized(lock)
        {
            if (columnNameList == null)
                return "";
            return columnNameList.get(index);
        }
    }

    public int getRowCount()
    {
        synchronized(lock)
        {
            return rowCount;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) 
    {
        synchronized(lock)
        {
            try
            {
                resultSet.absolute(rowIndex + 1);
                resultSet.updateObject(columnIndex + 1, value);
                resultSet.updateRow();
                fireTableDataChangedNotification();
            }
            catch (SQLException e)
            {
                System.err.println(
                     "SQLException thrown while trying to get value at " +
                     rowIndex + ", " + columnIndex);
                e.printStackTrace(System.err);
            }
        }
    }
    
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        synchronized(lock)
        {
            try
            {
                resultSet.absolute(rowIndex + 1);
                return resultSet.getObject(columnIndex + 1);
            }
            catch (SQLException e)
            {
                System.err.println(
                     "SQLException thrown while trying to get value at " +
                     rowIndex + ", " + columnIndex);
                e.printStackTrace(System.err);
                return null;
            }
        }
    }

    public void addTableModelListener(TableModelListener listener)
    {
        if (listener != null)
            tableModelListenerList.add(listener);
    }

    public void removeTableModelListener(TableModelListener listener)
    {
        if (listener != null)
            tableModelListenerList.remove(listener);
    }

    private void fireTableStructureChangedNotification()
    {
        // Should always be called on the event thread
        if (SwingUtilities.isEventDispatchThread())
        {
            TableModelEvent event = 
                new TableModelEvent(this, TableModelEvent.HEADER_ROW);
            for (TableModelListener listener: tableModelListenerList)
                listener.tableChanged(event);
        }
        else
        {
            SwingUtilities.invokeLater(
                () -> fireTableStructureChangedNotification());
            
        }
    }
    
    private void fireTableDataChangedNotification()
    {
        // Should always be called on the event thread
        if (SwingUtilities.isEventDispatchThread())
        {
            TableModelEvent event = new TableModelEvent(this);
            for (TableModelListener listener: tableModelListenerList)
                listener.tableChanged(event);
        }
        else
        {
            SwingUtilities.invokeLater(
                () -> fireTableDataChangedNotification());
        }
    }
    
    private Class createClassObject(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            return Object.class;
        }
    }
}
