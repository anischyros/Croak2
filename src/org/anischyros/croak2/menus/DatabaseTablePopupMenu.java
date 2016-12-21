package org.anischyros.croak2.menus;

import org.anischyros.croak2.menuitems.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.treenodes.*;

public class DatabaseTablePopupMenu extends ProfileNodePopupMenu
{
    public DatabaseTablePopupMenu(DatabaseTableTreeNode selectedNode,
        Profile profile)
    {
        super(selectedNode, profile);
        add(new ShowTableMenuItem(profile, selectedNode.getDatabaseName(),
            selectedNode.getTableName()));
        add(new ShowDescriptionMenuItem(profile, selectedNode.getDatabaseName(),
            selectedNode.getTableName()));
        add(new InsertRowMenuItem(profile, selectedNode.getDatabaseName(),
            selectedNode.getTableName()));
        add(new DropTableMenuItem(profile, selectedNode.getDatabaseName(),
            selectedNode.getTableName()));
    }
}