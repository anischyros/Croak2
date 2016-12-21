package org.anischyros.croak2.treenodes;

import javax.swing.tree.*;

public class ProfileInfoValueTreeNode extends DefaultMutableTreeNode
{
    public ProfileInfoValueTreeNode(String value)
    {
        super(value, false);
        setAllowsChildren(false);
    }
}
    
