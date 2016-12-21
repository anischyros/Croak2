package org.anischyros.croak2.utils;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import org.anischyros.croak2.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.dialogs.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;

public class Utils 
{
    private static DefaultMutableTreeNode findNode(TreePath path, 
        DefaultMutableTreeNode root)
    {
        if (path == null || path.getPathCount() == 0)
            return null;
        
        String userObject = root.getUserObject().toString();
        String pathComponent = path.getPathComponent(0).toString();
        if (!userObject.equals(pathComponent))
            return null;
        if (path.getPathCount() == 1)
            return root;
        
        DefaultMutableTreeNode node = root;
        for (int i = 1; i < path.getPathCount(); i++)
        {
            for (int j = 0; j < node.getChildCount(); j++)
            {
                DefaultMutableTreeNode thisNode =
                     (DefaultMutableTreeNode) node.getChildAt(j);
                userObject = thisNode.getUserObject().toString();
                pathComponent = path.getPathComponent(i).toString();
                if (userObject.equals(pathComponent))
                {
                    node = thisNode;
                    break;
                }
            }
        }
        return node;
    }
    
    public static final DefaultMutableTreeNode findNode(JTree tree, int row)
    {
        TreePath path = tree.getPathForRow(row);
        TreeModel tm = tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tm.getRoot();
        return findNode(path, root);
    }
    
    public static final ImageIcon loadImageIcon(String path)
    {
        ClassLoader cl = Utils.class.getClassLoader();
        URL url = cl.getResource(path);
        if (url == null)
            return null;
        ImageIcon icon = new ImageIcon(url);
        Image image = icon.getImage();
        image = image.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);
        return icon;
    }
    
    public static final Connection createDatabaseConnection(Profile profile,
        String databaseName) throws SQLException
    {
        // This is probably no longer needed
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        
        String url = ProfileManager.createURLString(profile, databaseName);
        return DriverManager.getConnection(url);
    }

    public static String[] getDatabases(Profile profile) throws SQLException 
    {
       List<String> list = new ArrayList<>();
        try (Connection c = createDatabaseConnection(profile, "mysql")) 
        {
            Statement s = c.createStatement();
            try (ResultSet rs = s.executeQuery("show databases")) 
            {
                while (rs.next())
                    list.add(rs.getString(1));
            }
        }
        
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }

    public static String[] getTablesForDatabase(Profile profile, 
        String databaseName) throws SQLException 
    {
       List<String> list = new ArrayList<>();
        try (Connection c = createDatabaseConnection(profile, databaseName)) 
        {
            Statement s = c.createStatement();
            try (ResultSet rs = s.executeQuery("show tables")) 
            {
                while (rs.next())
                    list.add(rs.getString(1));
            }
        }
        
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }
    
    public static void createNewProfile() throws IOException
    {
        Profile result = ServerProfileDialog.display(MainFrame.getInstance(), 
            "Create new database server profile", "Create", KeyEvent.VK_R);
        if (result == null)
            return;
        
        try
        {
            ProfileManager pm = ProfileManager.getInstance();
                TreePanel.getInstance().addProfile(result);
                pm.updateProfile(result);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), 
                e.getMessage());
        }
    }
    
    public static void cloneProfile(Profile profile)
        throws IOException
    {
        // Get new profile name
        String newName = 
            (String) JOptionPane.showInputDialog(MainFrame.getInstance(),
            "Enter new profile name");
        if (newName == null)
            return;
        newName = newName.trim();
        if (newName.length() == 0)
            return;

        // Does a profile with this name already exist?
        if (ProfileManager.getInstance().getProfile(newName) != null)
        {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                "A profile with that name already exists.", "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Profile newProfile = new Profile(newName, profile.getAddress(),
            profile.getPort(), profile.getUserName(), profile.getPassword(),
            profile.getParameters());
        try
        {
            ProfileManager.getInstance().updateProfile(newProfile);
            TreePanel.getInstance().addProfile(newProfile);
        }
        catch (SQLException e)
        {
            // This should never happen.
        }
    }
    
    public static void center(Window window)
    {
        // Get the current display's width since it's possible, perhaps even
        // likely, that we are running on a multi-display system.
        DisplayMode displayMode = 
            window.getGraphicsConfiguration().getDevice().getDisplayMode();

        window.setLocation(new Point(
            (displayMode.getWidth() - window.getSize().width) / 2,
            (displayMode.getHeight() - window.getSize().height) / 2));
    }
}
