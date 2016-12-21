package org.anischyros.croak2.treenodes;

import javax.swing.tree.*;
import org.anischyros.croak2.profile.*;

public abstract class ProfileTreeNode extends DefaultMutableTreeNode
{
    private Profile profile;
        
    public ProfileTreeNode(Profile profile, String nodeName)
    {
        super(nodeName);
        this.profile = profile;
    }
        
    public ProfileTreeNode(Profile profile)
    {
        this(profile, profile.getProfileName());
    }
    
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }
        
    public Profile getProfile()
    {
        return profile;
    }
}
    
