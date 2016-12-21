package org.anischyros.croak2.menus;

import javax.swing.tree.*;
import org.anischyros.croak2.menuitems.*;
import org.anischyros.croak2.profile.*;

public class ProfileNamePopupMenu extends ProfileNodePopupMenu
{
    public ProfileNamePopupMenu(DefaultMutableTreeNode selectedNode,
        Profile profile)
    {
        super(selectedNode, profile);
        add(new DeleteProfileMenuItem(profile));
        add(new CloneProfileMenuItem(profile));
    }
}
    
