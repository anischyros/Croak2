package org.anischyros.croak2.treenodes;

import org.anischyros.croak2.profile.*;

public class DatabaseTableTreeNode extends ProfileTreeNode
{
    private final String databaseName;
    
    public DatabaseTableTreeNode(Profile profile, String databaseName,
        String tableName)
    {
        super(profile, tableName);
        this.databaseName = databaseName;
    }
    
    public String getDatabaseName()
    {
        return databaseName;
    }
    
    public String getTableName()
    {
        return this.getUserObject().toString();
    }
}
