package org.anischyros.croak2.menuitems;

import java.io.*;
import javax.swing.*;
import org.anischyros.croak2.utils.*;

public class CreateNewServerProfileMenuItem extends JMenuItem
{
    public CreateNewServerProfileMenuItem()
    {
        super("Create new server profile");
        setMnemonic('N');
        addActionListener(e -> onAction());
    }
    
    private void onAction()
    {
        try
        {
            Utils.createNewProfile();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
