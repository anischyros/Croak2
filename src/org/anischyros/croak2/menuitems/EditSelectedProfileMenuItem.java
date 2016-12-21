package org.anischyros.croak2.menuitems;

import java.awt.event.*;
import javax.swing.*;
import org.anischyros.croak2.*;
import org.anischyros.croak2.dialogs.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class EditSelectedProfileMenuItem extends JMenuItem
{
    private Profile profile = null;

    public EditSelectedProfileMenuItem()
    {
        super("Edit selected profile");
        addActionListener(e -> onAction());
        setEnabled(false);
    }
    
    private void onAction()
    {
        if (profile == null)
            return;
        
        Profile result = ServerProfileDialog.display(MainFrame.getInstance(), 
            "Edit database server profile", "Save", KeyEvent.VK_S, profile);
        if (result == null)
            return;
        
        ProfileManager pm = ProfileManager.getInstance();
        try
        {
            pm.updateProfile(result);
            TreePanel.getInstance().updateProfileInfo(result);
        }
        catch (Throwable e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
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
            setText(String.format("<html>Edit selected profile " +
                "<font color=\"green\">%s</font></html>", 
                profile.getProfileName()));
        else
        if (!enabled)
            setText("Edit selected profile");
        super.setEnabled(enabled);
    }
}
