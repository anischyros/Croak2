package org.anischyros.croak2.menuitems;

import java.io.*;
import javax.swing.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.utils.*;

public class CloneSelectedProfileMenuItem extends JMenuItem
{
    private Profile profile = null;
    
    public CloneSelectedProfileMenuItem()
    {
        super("Clone selected profile");
        addActionListener(e -> onAction());
        setEnabled(false);
    }
    
    private void onAction()
    {
        if (profile == null)
            return;
        
        // Clone profile
        try
        {
            Utils.cloneProfile(profile);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, 
                "The clonsed profile could not be updated on disk.", "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }
    
    public Profile getProfile()
    {
        return profile;
    }
    
    public void setEnabled(boolean enabled)
    {
        if (profile != null && enabled)
            setText(String.format("<html>Clone selected profile " +
                "<font color=\"green\">%s</font></html>",
                profile.getProfileName()));
        else
        if (!enabled)
            setText("Clone selected profile");
        super.setEnabled(enabled);
    }
}
