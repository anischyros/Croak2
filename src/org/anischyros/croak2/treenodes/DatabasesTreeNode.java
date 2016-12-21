package org.anischyros.croak2.treenodes;

import org.anischyros.croak2.profile.*;

public class DatabasesTreeNode extends ProfileTreeNode
{
    public DatabasesTreeNode(Profile profile)
    {
        super(profile, "Databases");
        setAllowsChildren(true);
    }
}
    
