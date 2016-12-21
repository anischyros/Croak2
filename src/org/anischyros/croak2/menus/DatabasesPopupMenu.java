package org.anischyros.croak2.menus;

import org.anischyros.croak2.menuitems.*;
import org.anischyros.croak2.treenodes.*;
import org.anischyros.croak2.profile.*;

public class DatabasesPopupMenu extends ProfileNodePopupMenu
{
    public DatabasesPopupMenu(DatabasesTreeNode selectedNode,
        Profile profile)
    {
        super(selectedNode, profile);
        add(new CreateDatabaseMenuItem(profile));
    }
}
    
