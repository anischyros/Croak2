package org.anischyros.croak2;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.menuitems.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.properties.Properties;
import org.anischyros.croak2.tabs.*;
import org.anischyros.croak2.utils.*;

public class MainFrame extends JFrame
{
    private static MainFrame instance;
    
    private Map<Class, JMenuItem> menuItemMap = new HashMap<>();
    
    public MainFrame()
    {
        super("Croak v. 2");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        layoutMenu();
        try
        {
            layoutComponents();
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        pack();

        configureSize();
        
        forcedProfileCreationCheck();
        
        addComponentListener(new CustomComponentListener());
        addWindowListener(new CustomWindowListener());
        
        instance = this;
    }
    
    private void addMenuItemsToMap(JMenu menu)
    {
        for (int i = 0; i < menu.getItemCount(); i++)
        {
            Object mi = menu.getMenuComponent(i);
            if (mi instanceof JMenuItem)
                menuItemMap.put(mi.getClass(), (JMenuItem) mi);
        }
    }
    
    private void createFileMenu(JMenuBar mb)
    {
        JMenu m = new JMenu("File");
        m.setMnemonic('F');

        m.add(new LoadScriptMenuItem(this));
        m.add(new SaveScriptMenuItem(this));
        m.add(new SaveScriptAsMenuItem(this));
        m.addSeparator();
        m.add(new QuitMenuItem(this));

        mb.add(m);
        
        addMenuItemsToMap(m);
    }
    
    private void createProfileMenu(JMenuBar mb)
    {
        JMenu m = new JMenu("Profile");
        m.setMnemonic('P');

        m.add(new CreateNewServerProfileMenuItem());
        m.add(new EditSelectedProfileMenuItem());
        m.add(new CloneSelectedProfileMenuItem());
        m.add(new DeleteSelectedProfileMenuItem());
        
        mb.add(m);
        
        addMenuItemsToMap(m);
    }
    
    private void createDatabaseMenu(JMenuBar mb)
    {
        JMenu m = new JMenu("Database");
        m.setMnemonic('D');
        
        m.add(new CreateDatabaseMenuItem());
        m.add(new DropSelectedDatabaseMenuItem());
        m.addSeparator();
        m.add(new PerformQueryInSelectedDatabaseMenuItem());
        
        mb.add(m);
        
        addMenuItemsToMap(m);
    }
    
    private void createTableMenu(JMenuBar mb)
    {
        JMenu m = new JMenu("Table");
        m.setMnemonic('T');
        
        m.add(new ShowSelectedTableMenuItem());
        m.add(new ShowSelectedTableDescriptionMenuItem());
        m.add(new InsertRowIntoSelectedTableMenuItem());
        m.add(new DropSelectedTableMenuItem());
        
        mb.add(m);
        
        addMenuItemsToMap(m);
    }

    private void layoutMenu()
    {
        JMenuBar mb = new JMenuBar();
        setJMenuBar(mb);

        createFileMenu(mb);
        createProfileMenu(mb);
        createDatabaseMenu(mb);
        createTableMenu(mb);
    }

    private void layoutComponents() throws SQLException
    {
        TreePanel treePanel = TreePanel.getInstance();
        InfoPanel infoPanel = InfoPanel.getInstance();

        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            treePanel, infoPanel);
        add(sp);
    }
    
    private void configureSize()
    {
        // Get current screen width from last run
        Properties props = Properties.getInstance();
        int width = 1024;
        int height = 768;
        try
        {
            String widthProp = props.get("width");
            if (widthProp != null)
                width = new Integer(widthProp);
            String heightProp = props.get("height");
            if (heightProp != null)
                height = new Integer(heightProp);
        }
        catch (NumberFormatException e)
        {
        }

        // Set window size, and make sure it's not too big for the display.
        DisplayMode displayMode = 
            getGraphicsConfiguration().getDevice().getDisplayMode();
        setSize(displayMode.getWidth() < width ? displayMode.getWidth() : width,
            displayMode.getHeight() < height ? displayMode.getHeight() : height);
        
        // Update properties as they may have since changed.
        props.set("width", width);
        props.set("height", height);
        try
        {
            props.update();
        }
        catch (IOException e)
        {
        }        
    }
    
    private void forcedProfileCreationCheck()
    {
        SwingUtilities.invokeLater(() -> 
        {
            try
            {
                if (ProfileManager.getInstance().isEmpty())
                    Utils.createNewProfile();
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(this, e.getMessage());

            }
        });
    }
    
    public boolean okToQuit()
    {
        // There appears to be an off-by-one bug in JTabbedPane created because
        // of the way InfoPanel uses it.  Subtracting one from from the count is
        // the workaround.
        InfoPanel infoPanel = InfoPanel.getInstance();
        int count = infoPanel.getComponentCount() - 1;

        // Check each tab and make sure it can be closed.
        for (int i = 0; i < count; i++)
        {
            CustomTab tab = (CustomTab) infoPanel.getComponentAt(i);
            if (!tab.okToClose())
                return false;
        }
        
        return true;
    }
    
    public JMenuItem getMenuItem(Class cl)
    {
        return menuItemMap.get(cl);
    }
    
    public synchronized static MainFrame getInstance()
    {
        return instance;
    }
    
    private class CustomComponentListener extends ComponentAdapter
    {
        public void componentResized(ComponentEvent e)
        {
            Dimension fs = MainFrame.this.getSize();
            Properties props = Properties.getInstance();
            props.set("width", Integer.toString(fs.width));
            props.set("height", Integer.toString(fs.height));
            try
            {
                props.update();
            }
            catch (IOException f)
            {
            }
        }
    }
    
    public class CustomWindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            if (okToQuit())
                System.exit(0);
        }
    }
}
