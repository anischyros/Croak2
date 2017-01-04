package org.anischyros.croak2.panels;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.sql.*;
import org.anischyros.croak2.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.utils.*;
import org.anischyros.croak2.menus.*;
import org.anischyros.croak2.menuitems.*;
import org.anischyros.croak2.treenodes.*;

public class TreePanel extends JPanel
{
    private static TreePanel instance = null;
    
    private static ImageIcon rootIcon = null;
    private static ImageIcon databaseServerIcon = null;
    private static ImageIcon databasesIcon = null;
    private static ImageIcon databaseIcon = null;
    private static ImageIcon notebookIcon = null;
    private static ImageIcon tableIcon = null;
    
    static
    {
        rootIcon = Utils.loadImageIcon("Images/frog.png");
        databaseServerIcon = Utils.loadImageIcon("Images/database-server.png");
        databaseIcon = Utils.loadImageIcon("Images/database.png");
        databasesIcon = Utils.loadImageIcon("Images/databases.png");
        notebookIcon = Utils.loadImageIcon("Images/notebook.png");
        tableIcon = Utils.loadImageIcon("Images/database-table.jpeg");
    }
    
    private JTree tree;
    private final RefreshButton refreshButton;
    
    private CustomPopupMenu popupMenu = null;
    
    Profile selectedProfile = null;
    String selectedDatabase = null;
    String selectedTable = null;
    
    private TreePanel() throws SQLException
    {
        super();
        
        refreshButton = new RefreshButton();
        layoutComponents();

        tree.getSelectionModel().addTreeSelectionListener(e -> onTreeNodeSelection(e));
        
        setPreferredSize(new Dimension(250, 550));
    }
     
    private void updateProfileInfoNodes(Profile profile, 
        ProfileInfoTreeNode infoNode)
    {
        List<String> list = new ArrayList<>();
        list.add(String.format("Address: %s", profile.getAddress()));
        list.add(String.format("Port: %s", profile.getPort()));
        list.add(String.format("User name: %s", profile.getUserName()));
        list.add(String.format("Password: %s", profile.getPassword()));
        list.add(String.format("Parameters: %s", profile.getParameters()));

        for (int i = 0; i < infoNode.getChildCount(); i++)
        {
            ProfileInfoValueTreeNode node = 
                (ProfileInfoValueTreeNode) infoNode.getChildAt(i);
            node.setUserObject(list.get(i));
        }
        
        tree.repaint();
    }
    
    public void updateProfileInfo(Profile profile)
    {
        ProfilesTreeNode root = (ProfilesTreeNode) tree.getModel().getRoot();
        
        for (int i = 0; i < root.getChildCount(); i++)
        {
            ProfileNameTreeNode node = (ProfileNameTreeNode) root.getChildAt(i);
            if (node.getProfile().getProfileName().equals(profile.getProfileName()))
            {
                node.setProfile(profile);
                updateProfileInfoNodes(profile, 
                    (ProfileInfoTreeNode) node.getChildAt(0));
            }
        }
    }

    private ProfileInfoTreeNode buildProfileInfoNodeTree(Profile profile)
    {
        ProfileInfoTreeNode infoNode = new ProfileInfoTreeNode(profile);
        
        infoNode.add(new ProfileInfoValueTreeNode(
            String.format("Address: %s", profile.getAddress())));
        infoNode.add(new ProfileInfoValueTreeNode(
            String.format("Port: %s", 
            profile.getPort() != 0 ? profile.getPort() : "-- DEFAULT --")));
        infoNode.add(new ProfileInfoValueTreeNode(
            String.format("User name: %s", profile.getUserName())));
        infoNode.add(new ProfileInfoValueTreeNode(
            String.format("Password: %s", profile.getPassword())));
        infoNode.add(new ProfileInfoValueTreeNode(
            String.format("Parameters: %s", profile.getParameters())));
        
        return infoNode;
    }
    
    private void fillDatabaseNameNode(DatabaseNameTreeNode node,
        String databaseName) throws SQLException
    {
        Profile profile = node.getProfile();
        String[] tableNames = Utils.getTablesForDatabase(profile, databaseName);
        for (String tableName: tableNames)
        {
            node.add(new DatabaseTableTreeNode(profile, databaseName, 
                    tableName));
        }
    }
    
    private DatabasesTreeNode buildDatabasesNodeTree(Profile profile) 
        throws SQLException
    {
        DatabasesTreeNode databasesNode = new DatabasesTreeNode(profile);
        
        String[] databaseName = Utils.getDatabases(profile);
        
        // Add any new databases names to map
        for (String name: databaseName)
        {
            DatabaseNameTreeNode node = new DatabaseNameTreeNode(profile, name);
            databasesNode.add(node);
            fillDatabaseNameNode(node, name);
        }

        return databasesNode;
    }
    
    private ProfileNameTreeNode buildProfileNameNodeTree(Profile profile)
       throws SQLException
    {
        ProfileNameTreeNode profileNode = new ProfileNameTreeNode(profile);
        profileNode.add(buildProfileInfoNodeTree(profile));
        profileNode.add(buildDatabasesNodeTree(profile));
        
        return profileNode;
    }
    
    private ProfilesTreeNode buildNodeTree() throws SQLException
    {
        ProfilesTreeNode root = new ProfilesTreeNode();
        for (Profile profile: ProfileManager.getInstance().getAllProfiles())
            root.add(buildProfileNameNodeTree(profile));

        return root;
    }
    
    private void layoutComponents() throws SQLException
    {
        setLayout(new BorderLayout());
        
        DefaultTreeModel model = new DefaultTreeModel(buildNodeTree());
        tree = new JTree(model);
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new CustomTreeCellRenderer());
        tree.addMouseListener(new CustomMouseListener());
         JScrollPane sp = new JScrollPane(tree,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp, BorderLayout.CENTER);
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(refreshButton);
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(buttonBox, BorderLayout.SOUTH);
    }
    
    public void addProfile(Profile profile) throws SQLException
    {
        DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();

        ProfilesTreeNode root = (ProfilesTreeNode) tm.getRoot();

        ProfileNameTreeNode newNode = buildProfileNameNodeTree(profile);
        
        for (int i = 0; i < root.getChildCount(); i++)
        {
            ProfileNameTreeNode node = (ProfileNameTreeNode) root.getChildAt(i);
            if (newNode.getProfile().getProfileName().compareToIgnoreCase(
                node.getProfile().getProfileName()) < 0)
            {
                tm.insertNodeInto(newNode, root, i);
                return;
            }
        }
        
        // The tree is empty.  Insert the node into the tree and tell the root
        // node to expand itself.
        tm.insertNodeInto(newNode, root, root.getChildCount());
        tree.expandRow(0);
    }
    
    public void removeProfile(Profile profile)
    {
        DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
        
        ProfilesTreeNode root = (ProfilesTreeNode) tm.getRoot();
        for (int i = 0; i < root.getChildCount(); i++)
        {
            ProfileNameTreeNode node = (ProfileNameTreeNode) root.getChildAt(i);
            if (node.getProfile().getProfileName().equals(
                profile.getProfileName()))
            {
                tm.removeNodeFromParent(node);
                break;
            }
        }
    }
    
    private void insertNewDatabaseNode(DatabasesTreeNode databasesNode, 
        String databaseName)
    {
        DatabaseNameTreeNode newNode = 
             new DatabaseNameTreeNode(databasesNode.getProfile(), databaseName);
        
        DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
        
        for (int i = 0; i < databasesNode.getChildCount(); i++)
        {
            DatabaseNameTreeNode node =
                (DatabaseNameTreeNode) databasesNode.getChildAt(i);
            if (node.getDatabaseName().compareToIgnoreCase(databaseName) > 0)
            {
                tm.insertNodeInto(newNode, databasesNode, i);
                return;
            }
        }
        
        tm.insertNodeInto(newNode, databasesNode, databasesNode.getChildCount());
    }
    
    public void addDatabase(Profile profile, String databaseName)
    {
        ProfilesTreeNode root = (ProfilesTreeNode) tree.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); i++)
        {
            ProfileNameTreeNode node = (ProfileNameTreeNode) root.getChildAt(i);
            if (node.getProfile().getProfileName().equals(profile.getProfileName()))
            {
                DatabasesTreeNode databasesNode = 
                    (DatabasesTreeNode) node.getChildAt(1);
                insertNewDatabaseNode(databasesNode, databaseName);
                break;
            }
        }
    }
    
    private void removeDatabaseNode(DatabasesTreeNode databasesNode, 
        String databaseName)
    {
        for (int i = 0; i < databasesNode.getChildCount(); i++)
        {
            DatabaseNameTreeNode node = 
                (DatabaseNameTreeNode) databasesNode.getChildAt(i);
            if (node.getDatabaseName().equals(databaseName))
            {
                DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
                tm.removeNodeFromParent(node);
                break;
            }
        }
    }
    
    public void dropDatabase(Profile profile, String databaseName)
    {
        ProfilesTreeNode root = (ProfilesTreeNode) tree.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); i++)
        {
            ProfileNameTreeNode node = (ProfileNameTreeNode) root.getChildAt(i);
            if (node.getProfile().getProfileName().equals(profile.getProfileName()))
            {
                DatabasesTreeNode databasesNode = 
                    (DatabasesTreeNode) node.getChildAt(1);
                removeDatabaseNode(databasesNode, databaseName);
                break;
            }
        }
    }
    
    private DatabaseNameTreeNode findDatabaseNode(
        DatabasesTreeNode databasesNode, String databaseName)
    {
        for (int i = 0; i < databasesNode.getChildCount(); i++)
        {
            DatabaseNameTreeNode node =
                (DatabaseNameTreeNode) databasesNode.getChildAt(i);
            if (node.getDatabaseName().equals(databaseName))
                return node;
        }

        return null;
    }
    
    private void removeTableNode(DatabaseNameTreeNode databaseNameNode,
        String tableName)
    {
        for (int i = 0; i < databaseNameNode.getChildCount(); i++)
        {
            DatabaseTableTreeNode node =
                (DatabaseTableTreeNode) databaseNameNode.getChildAt(i);
            if (node.getTableName().equals(tableName))
            {
                DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
                tm.removeNodeFromParent(node);
                break;
            }
        }
    }
    
    public void dropTable(Profile profile, String databaseName, String tableName)
    {
        ProfilesTreeNode root = (ProfilesTreeNode) tree.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); i++)
        {
            ProfileNameTreeNode node = (ProfileNameTreeNode) root.getChildAt(i);
            if (node.getProfile().getProfileName().equals(profile.getProfileName()))
            {
                DatabasesTreeNode databasesNode = 
                    (DatabasesTreeNode) node.getChildAt(1);
                DatabaseNameTreeNode databaseNameNode =
                    findDatabaseNode(databasesNode, databaseName);
                if (databaseNameNode != null)
                    removeTableNode(databaseNameNode, tableName);
                break;
            }
        }
    }
    
    private void insertTableName(DatabaseNameTreeNode databaseNameNode, 
        String tableName)
    {
        DatabaseTableTreeNode newNode =
            new DatabaseTableTreeNode(databaseNameNode.getProfile(),
            databaseNameNode.getDatabaseName(), tableName);

        DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
        
        for (int i = 0; i < databaseNameNode.getChildCount(); i++)
        {
            DatabaseTableTreeNode node =
                (DatabaseTableTreeNode) databaseNameNode.getChildAt(i);
            if (tableName.compareTo(node.getTableName()) < 0)
            {
                tm.insertNodeInto(newNode, databaseNameNode, i);
                return;
            }
        }
        
        tm.insertNodeInto(newNode, databaseNameNode, 
            databaseNameNode.getChildCount());

    }
    
    private void refreshTables(DatabaseNameTreeNode databaseNameNode)
        throws SQLException
    {
        String tables[] = Utils.getTablesForDatabase(
            databaseNameNode.getProfile(),
            databaseNameNode.getDatabaseName());
        
        // Build map of existing table nodes
        Map<String, DatabaseTableTreeNode> map = new HashMap<>();
        for (int i = 0; i < databaseNameNode.getChildCount(); i++)
        {
            DatabaseTableTreeNode node = 
                (DatabaseTableTreeNode) databaseNameNode.getChildAt(i);
            map.put(node.getTableName(), node);
        }
        
        // Create and add to table nodes for any table not already in map
        List<String> addList = new ArrayList<>();
        for (String tableName: tables)
        {
            if (!map.containsKey(tableName))
                addList.add(tableName);
        }

        // Remove from tree any nodes that are no longer in the list of tables
        DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
        Set<String> set = new HashSet<>(tables.length);
        set.addAll(Arrays.asList(tables));
        map.keySet().forEach(tableName ->
        {
            if (!set.contains(tableName))
                tm.removeNodeFromParent(map.get(tableName));
        });
        
        // Create nodes from names in addlist to insert into tree
        addList.forEach(
            tableName -> insertTableName(databaseNameNode, tableName));
            
    }
    
    private void refreshDatabaseInfo(ProfileNameTreeNode profileNode) 
        throws SQLException
    {
        DatabasesTreeNode databasesNode = 
            (DatabasesTreeNode) profileNode.getChildAt(1);
        
        String databases[] = Utils.getDatabases(profileNode.getProfile());
        
        // Build map of existing database nodes
        Map<String, DatabaseNameTreeNode> map = new HashMap<>();
        for (int i = 0; i < databasesNode.getChildCount(); i++)
        {
            DatabaseNameTreeNode node =
                (DatabaseNameTreeNode) databasesNode.getChildAt(i);
            map.put(node.getDatabaseName(), node);
        }
        
        // Create and add to map nodes for any databases not already in map
        List<String> addList = new ArrayList<>();
        for (String databaseName: databases)
        {
            if (!map.containsKey(databaseName))
               addList.add(databaseName);
        }
        
        // Remove from tree any nodes that are no longer in the list of 
        // databases
        DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
        Set<String> set = new HashSet<>(databases.length);
        for (String databaseName: databases)
            set.add(databaseName);
        for (String databaseName: map.keySet())
        {
            if (!set.contains(databaseName))
                tm.removeNodeFromParent(map.get(databaseName));
        }
        
        // Create nodes from names in addList into insert into tree
        for (String databaseName: addList)
            insertNewDatabaseNode(databasesNode, databaseName);
        
        // Refresh tables for each database in profile
       for (int i = 0; i < databasesNode.getChildCount(); i++)
        {
            DatabaseNameTreeNode node =
                (DatabaseNameTreeNode) databasesNode.getChildAt(i);
            refreshTables(node);
        }
    }
    
    public void refreshAllDatabaseInfo()
    {
        ProfilesTreeNode root = (ProfilesTreeNode) tree.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); i++)
        {
            ProfileNameTreeNode node = (ProfileNameTreeNode) root.getChildAt(i);
            try
            {
                refreshDatabaseInfo(node);
            }
            catch (SQLException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void processNode(DatabaseNameTreeNode node) 
    {
        selectedDatabase = node.getDatabaseName();
        
        MainFrame frame = MainFrame.getInstance();
        
        CreateDatabaseMenuItem m1 = (CreateDatabaseMenuItem) frame.getMenuItem(
            CreateDatabaseMenuItem.class);
        m1.setProfile(node.getProfile());
        m1.setEnabled(true);
        
        DropSelectedDatabaseMenuItem m2 = 
            (DropSelectedDatabaseMenuItem) frame.getMenuItem(
            DropSelectedDatabaseMenuItem.class);
        m2.setProfile(node.getProfile());
        m2.setDatabaseName(node.getDatabaseName());
        m2.setEnabled(true);
        
        PerformQueryInSelectedDatabaseMenuItem m3 = 
            (PerformQueryInSelectedDatabaseMenuItem) frame.getMenuItem(
            PerformQueryInSelectedDatabaseMenuItem.class);
        m3.setProfile(node.getProfile());
        m3.setDatabaseName(node.getDatabaseName());
        m3.setEnabled(true);

    }
    
    private void processNode(DatabaseTableTreeNode node) 
    {
        selectedTable = node.getTableName();
        
        MainFrame frame = MainFrame.getInstance();
        
        ShowSelectedTableMenuItem m1 =
            (ShowSelectedTableMenuItem) frame.getMenuItem(
            ShowSelectedTableMenuItem.class);
        m1.setProfile(node.getProfile());
        m1.setDatabaseName(node.getDatabaseName());
        m1.setTableName(node.getTableName());
        m1.setEnabled(true);
        
        ShowSelectedTableDescriptionMenuItem m2 =
            (ShowSelectedTableDescriptionMenuItem) frame.getMenuItem(
            ShowSelectedTableDescriptionMenuItem.class);
        m2.setProfile(node.getProfile());
        m2.setDatabaseName(node.getDatabaseName());
        m2.setTableName(node.getTableName());
        m2.setEnabled(true);
        
        InsertRowIntoSelectedTableMenuItem m3 =
            (InsertRowIntoSelectedTableMenuItem) frame.getMenuItem(
            InsertRowIntoSelectedTableMenuItem.class);
        m3.setProfile(node.getProfile());
        m3.setDatabaseName(node.getDatabaseName());
        m3.setTableName(node.getTableName());
        m3.setEnabled(true);
        
        DropSelectedTableMenuItem m4 =
            (DropSelectedTableMenuItem) frame.getMenuItem(
            DropSelectedTableMenuItem.class);
        m4.setProfile(node.getProfile());
        m4.setDatabaseName(node.getDatabaseName());
        m4.setTableName(node.getTableName());
        m4.setEnabled(true);
    }
    
    private void processNode(ProfileTreeNode node) 
    {
        selectedProfile = node.getProfile();

        MainFrame frame = MainFrame.getInstance();
        EditSelectedProfileMenuItem m1 = 
            (EditSelectedProfileMenuItem) frame.getMenuItem(
            EditSelectedProfileMenuItem.class);
        m1.setProfile(node.getProfile());
        m1.setEnabled(true);
        
        CloneSelectedProfileMenuItem m2 =
            (CloneSelectedProfileMenuItem) frame.getMenuItem(
            CloneSelectedProfileMenuItem.class);
        m2.setProfile(node.getProfile());
        m2.setEnabled(true);
        
        DeleteSelectedProfileMenuItem m3 =
            (DeleteSelectedProfileMenuItem) frame.getMenuItem(
            DeleteSelectedProfileMenuItem.class);
        m3.setProfile(node.getProfile());
        m3.setEnabled(true);
    }
    
    private void clearMenuStates()
    {
        selectedProfile = null;
        selectedDatabase = null;
        selectedTable = null;

        MainFrame frame = MainFrame.getInstance();
        
        // Profile menu items
        frame.getMenuItem(EditSelectedProfileMenuItem.class).setEnabled(false);
        frame.getMenuItem(DeleteSelectedProfileMenuItem.class).setEnabled(false);
        frame.getMenuItem(CloneSelectedProfileMenuItem.class).setEnabled(false);
        
        // Database menu items
        frame.getMenuItem(PerformQueryInSelectedDatabaseMenuItem.class).setEnabled(false);
    }
    
    private void onTreeNodeSelection(TreeSelectionEvent e)
    {
        clearMenuStates();
        
        for (Object node: e.getPath().getPath())
        {
            if (node instanceof DatabaseNameTreeNode)
                processNode((DatabaseNameTreeNode) node);
            else
            if (node instanceof DatabaseTableTreeNode)
                processNode((DatabaseTableTreeNode) node);
            else
            if (node instanceof DatabasesTreeNode)
                processNode((DatabasesTreeNode) node);
            else
            if (node instanceof ProfileInfoTreeNode)
                processNode((ProfileInfoTreeNode) node);
            else
            if (node instanceof ProfileTreeNode)
                processNode((ProfileTreeNode) node);
        }
    }
    
    public static final synchronized TreePanel getInstance() throws SQLException
    {
        if (instance != null)
            return instance;
        instance = new TreePanel();
        return instance;
    }
    
    private class RefreshButton extends JButton
    {
        public RefreshButton()
        {
            super("Refresh");
            addActionListener(e -> refreshAllDatabaseInfo());
        }
    }
    
    private class CustomTreeCellRenderer extends DefaultTreeCellRenderer
    {
        public CustomTreeCellRenderer()
        {
            super();
        }
        
        public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean isLeaf, int row, 
            boolean focused)
        {
            Component label = super.getTreeCellRendererComponent(tree, value,
                selected, expanded, isLeaf, row, focused);
            
            Dimension d = label.getPreferredSize();
            d.width = 300;
            label.setPreferredSize(d);

            DefaultMutableTreeNode node = Utils.findNode(tree, row);
            if (node == null)
                return label;
            
            if (node instanceof ProfilesTreeNode)
                ((JLabel) label).setIcon(TreePanel.rootIcon);
            else
            if (node instanceof ProfileInfoTreeNode)
                ((JLabel) label).setIcon(TreePanel.notebookIcon);
            else
            if (node instanceof ProfileNameTreeNode)
                ((JLabel) label).setIcon(TreePanel.databaseServerIcon);
            else
            if (node instanceof DatabasesTreeNode)
                ((JLabel) label).setIcon(TreePanel.databasesIcon);
            else
            if (node instanceof DatabaseNameTreeNode)
                ((JLabel) label).setIcon(TreePanel.databaseIcon);
            else
            if (node instanceof DatabaseTableTreeNode)
                ((JLabel) label).setIcon(TreePanel.tableIcon);
            else
                ((JLabel) label).setIcon(null);
            
            return label;
        }
    }
    
    private class CustomMouseListener extends MouseAdapter
    {
        private void doPopupMenu(MouseEvent e)
        {
            int row = tree.getRowForLocation(e.getX(), e.getY());
            tree.setSelectionInterval(row, row);
            DefaultMutableTreeNode selectedNode = Utils.findNode(tree, row);
            if (selectedNode == null)
                return;
            
            if (selectedNode instanceof ProfilesTreeNode)
                popupMenu = new ProfilesPopupMenu(selectedNode);
            else
            if (selectedNode instanceof ProfileNameTreeNode)
            {
                ProfileNameTreeNode node = (ProfileNameTreeNode) selectedNode;
                popupMenu = new ProfileNamePopupMenu(selectedNode,
                    node.getProfile());
            }
            else
            if (selectedNode instanceof ProfileInfoTreeNode)
            {
                ProfileInfoTreeNode node = (ProfileInfoTreeNode) selectedNode;
                popupMenu = new ProfileInfoPopupMenu(node,
                    node.getProfile());
            }
            else
            if (selectedNode instanceof DatabasesTreeNode)
            {
                DatabasesTreeNode node = (DatabasesTreeNode) selectedNode;
                popupMenu = new DatabasesPopupMenu(node, 
                    node.getProfile());
            }
            else
            if (selectedNode instanceof DatabaseNameTreeNode)
            {
                DatabaseNameTreeNode node = (DatabaseNameTreeNode) selectedNode;
                popupMenu = new DatabaseNamePopupMenu(node, node.getProfile());
            }
            else
            if (selectedNode instanceof DatabaseTableTreeNode)
            {
                DatabaseTableTreeNode node = (DatabaseTableTreeNode) selectedNode;
                popupMenu = new DatabaseTablePopupMenu(node, node.getProfile());
            }
            else
                return;
            
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
        
        public void mousePressed(MouseEvent e)
        {
            if (e.isPopupTrigger() && SwingUtilities.isRightMouseButton(e))
                doPopupMenu(e);
        }
    }
}