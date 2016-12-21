package org.anischyros.croak2.menus;

import javax.swing.*;
import javax.swing.tree.*;

public abstract class CustomPopupMenu extends JPopupMenu
{
    private final DefaultMutableTreeNode selectedNode;
        
    public CustomPopupMenu(DefaultMutableTreeNode selectedNode)
    {
        super();

        this.selectedNode = selectedNode;
    }
        
    public DefaultMutableTreeNode getSelectedNode()
    {
        return selectedNode;
    }
}
    
