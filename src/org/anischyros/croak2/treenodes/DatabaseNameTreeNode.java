package org.anischyros.croak2.treenodes;

import org.anischyros.croak2.profile.*;

public class DatabaseNameTreeNode extends ProfileTreeNode
{
    public DatabaseNameTreeNode(Profile profile, String databaseName)
    {
        super(profile, databaseName);
        setAllowsChildren(true);
    }
        
    public String getDatabaseName()
    {
        return getUserObject().toString();
    }
}
    
