package org.anischyros.croak2;

import javax.swing.*;
import org.anischyros.croak2.utils.*;

public class Croak2 
{
    public static void main(String[] args) 
    {
        // Set Metal look and feel
        try 
        {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
        } 
        catch (Throwable e)
        {
            // This should never happen.
            e.printStackTrace(System.err);
        }

        MainFrame mf = new MainFrame();
        Utils.center(mf);
        mf.setVisible(true);
    }
}
