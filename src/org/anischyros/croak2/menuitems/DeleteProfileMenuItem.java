package org.anischyros.croak2.menuitems;

import java.io.*;
import java.sql.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class DeleteProfileMenuItem extends JMenuItem
{
    private final Profile profile;
    
    public DeleteProfileMenuItem(Profile profile)
    {
        super("Delete profile");
        this.profile = profile;
        addActionListener(e -> onAction(this.profile));
    }
        
    private void onAction(Profile profile)
    {
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
}
    
