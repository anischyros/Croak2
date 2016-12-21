package org.anischyros.croak2.menus;

import javax.swing.tree.*;
import org.anischyros.croak2.menuitems.*;

public class ProfilesPopupMenu extends CustomPopupMenu
{
    public ProfilesPopupMenu(DefaultMutableTreeNode selectedNode)
    {
        super(selectedNode);
        add(new CreateNewServerProfileMenuItem());
    }
}