package org.anischyros.croak2.menus;

import javax.swing.tree.*;
import org.anischyros.croak2.profile.*;

public abstract class ProfileNodePopupMenu extends CustomPopupMenu
{
    private final Profile profile;
        
    public ProfileNodePopupMenu(DefaultMutableTreeNode selectedNode,
        Profile profile)
    {
        super(selectedNode);
        this.profile = profile;
    }
        
    public Profile getProfile()
    {
        return profile;
    }
}
    