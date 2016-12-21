package org.anischyros.croak2.menuitems;

import java.io.*;
import javax.swing.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.utils.*;

public class CloneProfileMenuItem extends JMenuItem
{
    private Profile profile = null;
    
    public CloneProfileMenuItem(Profile profile)
    {
        super("Clone profile");
        addActionListener(e -> onAction());
        this.profile = profile;
    }
    
    private void onAction()
    {
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
}