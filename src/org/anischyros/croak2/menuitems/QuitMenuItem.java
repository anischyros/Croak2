package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.*;

public class QuitMenuItem extends JMenuItem
{
    public QuitMenuItem(MainFrame mainFrame)
    {
        super("Quit");
        setMnemonic('Q');
        addActionListener(e ->
        {
           if (mainFrame.okToQuit())
               System.exit(0);
        });
    }
}
