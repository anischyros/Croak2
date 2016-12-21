package org.anischyros.croak2.menuitems;

import java.io.IOException;
import java.sql.SQLException;
import javax.swing.*;
import org.anischyros.croak2.panels.TreePanel;
import org.anischyros.croak2.profile.Profile;
import org.anischyros.croak2.profile.ProfileManager;

public class DeleteSelectedProfileMenuItem extends JMenuItem
{
    private Profile profile = null;
    
    public DeleteSelectedProfileMenuItem()
    {
        super("Delete selected profile");
        addActionListener(e -> onAction());
        setEnabled(false);
    }
    
    private void onAction()
    {
        if (profile == null)
            return;
        
        int result = JOptionPane.showConfirmDialog(null, 
            String.format("Are you sure you want to delete profile \"%s\"?",
            profile.getProfileName()), "Confirm deletion", 
            JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION)
            return;

        try
        {
            ProfileManager.getInstance().removeProfile(profile.getProfileName());
            TreePanel.getInstance().removeProfile(profile);
        }
        catch (IOException | SQLException f)
        {
            JOptionPane.showMessageDialog(this,
                "Exception thrown while saving profiles: " + f.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
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
            setText(String.format("<html>Delete selected profile " +
                "<font color=\"green\">%s</font></html>", 
                profile.getProfileName()));
        else
        if (!enabled)
            setText("Delete selected profile");
        super.setEnabled(enabled);
    }
}
