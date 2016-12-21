package org.anischyros.croak2.utils;

import java.sql.*;
import java.util.*;

public class RequestProcessor 
{
    private static String dup(char ch, int n)
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < n; i++)
            buf.append(ch);
        return buf.toString();
    }
    
    public static String formatResultSet(ResultSet resultSet) 
        throws SQLException
    {
        // Get column metadata
        ResultSetMetaData md = resultSet.getMetaData();
        int columnCount = md.getColumnCount();
        if (columnCount == 0)
            return "Result set contains no columns\n";
        
        List<String[]> rows = new ArrayList<String[]>();
        int rowCount = 0;
        boolean largeDataSet = false;
        while (resultSet.next())
        {
            if (rowCount >= 1000)
            {
                largeDataSet = true;
                break;
            }
            
            String[] row = new String[columnCount];
            for (int col = 0; col < columnCount; col++)
            {
                Object obj = resultSet.getObject(col + 1);
                if (obj == null)
                    row[col] = "null";
                else
                    row[col] = obj.toString();
            }
            rows.add(row);
            rowCount++;
        }
        resultSet.close();
        
        // Get column names
        String[] column = new String[columnCount];
        int[] columnWidth = new int[columnCount];
        for (int col = 0; col < columnCount; col++)
        {
            column[col] = md.getColumnName(col + 1);
            columnWidth[col] = column[col].length() + 2;
        }

        // If row data width is wider than its header width, use the row 
        // data width
        for (String[] row: rows)
        {
            for (int col = 0; col < row.length; col++)
            {
                int dataWidth = row[col].length() + 2;
                if (dataWidth > columnWidth[col])
                    columnWidth[col] = dataWidth;
            }
        }
        
        // Print notification of truncated data set if necessary.
        StringBuilder result = new StringBuilder();
        if (largeDataSet)
        {
            result.append("NOTE: Large data set returned from server.\n");
            result.append("Displayed result set truncated.\n");
        }

        // Print column header
        for (int col = 0; col < columnCount; col++)
        {
            result.append("+");
            result.append(dup('-', columnWidth[col] + 2));
        }
        result.append("+\n");
        for (int col = 0; col < columnCount; col++)
        {
            result.append("| ");
            result.append(column[col]);
            if (column[col].length() < columnWidth[col])
            {
                result.append(dup(' ', 
                    columnWidth[col] - column[col].length()));
            }
            result.append(" ");
        }
        result.append("|\n");
        for (int col = 0; col < columnCount; col++)
        {
            result.append("+");
            result.append(dup('-', columnWidth[col] + 2));
        }
        result.append("+\n");
        
        // Print each row
        for (String[] row: rows)
        {
            for (int col = 0; col < columnCount; col++)
            {
                result.append("| ");
                result.append(row[col]);
                if (row[col].length() < columnWidth[col])
                {
                    result.append(dup(' ', 
                        columnWidth[col] - row[col].length()));
                }
                result.append(" ");
            }
            result.append("|\n");
        }
        
        for (int col = 0; col < columnCount; col++)
        {
            result.append("+");
            result.append(dup('-', columnWidth[col] + 2));
        }
        result.append("+\n");
            
        return result.toString();
    }
    
    public static String formatUpdateResult(int updateCount) 
        throws SQLException
    {
        return String.format("%d row%s updated.", updateCount,
            (updateCount == 1 ? "" : "s"));
    }    
}
