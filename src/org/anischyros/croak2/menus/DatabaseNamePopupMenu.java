package org.anischyros.croak2.menus;

import org.anischyros.croak2.menuitems.*;
import org.anischyros.croak2.treenodes.*;
import org.anischyros.croak2.profile.*;

public class DatabaseNamePopupMenu extends ProfileNodePopupMenu
{
    public DatabaseNamePopupMenu(DatabaseNameTreeNode selectedNode, 
        Profile profile)
    {
        super(selectedNode, profile);
        add(new PerformQueryMenuItem(profile, selectedNode.getDatabaseName()));
        add(new DropDatabaseMenuItem(profile, selectedNode.getDatabaseName()));
       }
    }
    
