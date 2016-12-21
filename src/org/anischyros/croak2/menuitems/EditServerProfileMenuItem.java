package org.anischyros.croak2.menuitems;

import java.awt.event.*;
import javax.swing.*;
import org.anischyros.croak2.*;
import org.anischyros.croak2.dialogs.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class EditServerProfileMenuItem extends JMenuItem 
{
    private final Profile profile;
    
    public EditServerProfileMenuItem(Profile profile)
    {
        super("Edit server profile");
        this.profile = profile;
        addActionListener(e -> onAction(this.profile));
    }
    
    private void onAction(Profile profile)
    {
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
}
