package org.anischyros.croak2.treenodes;

import org.anischyros.croak2.profile.*;

public class ProfileInfoTreeNode extends ProfileTreeNode
{
    public ProfileInfoTreeNode(Profile profile)
    {
        super(profile, "Profile info");
        setAllowsChildren(true);
    }
}
    
