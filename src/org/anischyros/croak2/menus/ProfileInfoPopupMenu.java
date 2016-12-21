package org.anischyros.croak2.menus;

import javax.swing.tree.*;
import org.anischyros.croak2.menuitems.*;
import org.anischyros.croak2.profile.*;

public class ProfileInfoPopupMenu extends ProfileNodePopupMenu
{
    public ProfileInfoPopupMenu(DefaultMutableTreeNode selectedNode,
        Profile profile)
    {
        super(selectedNode, profile);
        add(new EditServerProfileMenuItem(profile));
    }
}
   
