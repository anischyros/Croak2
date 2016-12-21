package org.anischyros.croak2.tabs;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.sql.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.utils.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.properties.Properties;

public class QueryDatabaseTab extends CustomTab
{
    private UndoableTextPane commandField;
    private JTextPane resultsField;
    private ExecuteButton executeButton;
    private LoadButton loadButton;
    private SaveButton saveButton;
    private SaveAsButton saveAsButton;
    private JLabel currentDatabaseLabel1;
    private JLabel currentDatabaseLabel2;
    private File scriptFile = null;
    
    public QueryDatabaseTab(Profile profile, String databaseName)
    {
        super(profile, databaseName);
        layoutComponents();
    }
    
    private Component layoutButtons()
    {
        executeButton = new ExecuteButton();
        loadButton = new LoadButton();
        saveButton = new SaveButton();
        saveAsButton = new SaveAsButton();

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(executeButton);
        box.add(Box.createHorizontalStrut(5));
        box.add(loadButton);
        box.add(Box.createHorizontalStrut(5));
        box.add(saveButton);
        box.add(Box.createHorizontalStrut(5));
        box.add(saveAsButton);
        box.add(Box.createHorizontalGlue());
        return box;
    }
    
    private Component layoutTopTextPane()
    {
        currentDatabaseLabel1 = new JLabel("Current database: ");
        currentDatabaseLabel2 = new JLabel(getDatabaseName());

        Box label = Box.createHorizontalBox();
        label.add(new JLabel("Enter SQL script below"));
        label.add(Box.createHorizontalGlue());
        label.add(currentDatabaseLabel1);
        label.add(currentDatabaseLabel2);
 
        commandField = new UndoableTextPane();
        commandField.setMinimumSize(
            new Dimension(commandField.getPreferredSize().width, 1000));
        commandField.setPreferredSize(commandField.getMinimumSize());
        
        Component buttons = layoutButtons();

        JScrollPane sp = new JScrollPane(commandField, 
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        box.add(label);
        box.add(Box.createVerticalStrut(10));
        box.add(sp);
        box.add(Box.createVerticalStrut(10));
        box.add(buttons);
        box.add(Box.createVerticalGlue());
        
        box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return box;
    }
    
    private Component layoutBottomTextPane()
    {
        Box label = Box.createHorizontalBox();
        label.add(new JLabel("Response"));
        label.add(Box.createHorizontalGlue());

        resultsField = new JTextPane();
        resultsField.setEditable(false);
        resultsField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultsField.setMinimumSize(
            new Dimension(resultsField.getPreferredSize().width, 1000));
        resultsField.setPreferredSize(resultsField.getMinimumSize());
        
        JScrollPane sp = new JScrollPane(resultsField, 
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        box.add(label);
        box.add(Box.createVerticalStrut(10));
        box.add(sp);
        box.add(Box.createVerticalGlue());
 
        box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return box;
    }
    
    private void layoutComponents()
    {
        Component topPane = layoutTopTextPane();
        Component bottomPane = layoutBottomTextPane();
        final JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPane, 
                bottomPane);
        sp.setResizeWeight(0.30);
        
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
        
        commandField.getDocument().addDocumentListener(
            new CustomDocumentListener());
    }
    
    public void disposeResources()
    {
        // This does nothing
    }
    
    private void enableButtons()
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            if (commandField.isEnabled())
            {
                executeButton.setEnabled(true);
                loadButton.setEnabled(true);
            }
            saveButton.setEnabled(true);
            saveAsButton.setEnabled(true);
        }
        else
        {
            SwingUtilities.invokeLater(() -> enableButtons());
        }
    }
    
    private void disableButtons()
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            executeButton.setEnabled(false);
            loadButton.setEnabled(false);
            saveButton.setEnabled(false);
            saveAsButton.setEnabled(false);
        }
        else
        {
            SwingUtilities.invokeLater(() -> disableButtons());
        }
    }
    
    private void updateResultsField(final String results)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            resultsField.setText(resultsField.getText() + results + "\n");
        }
        else
        {
            SwingUtilities.invokeLater(() -> updateResultsField(results));
        }
    }
    
    private void executeOneCommand(String command, Statement statement) 
        throws SQLException
    {
        // Execute the SQL request
        boolean hasResultSet = statement.execute(command);
        for (;;)
        {
            int updateCount = statement.getUpdateCount();
            if (!hasResultSet && updateCount == -1)
                break;
            
            final String result;
            if (hasResultSet)
            {
                result = RequestProcessor.formatResultSet(
                    statement.getResultSet());
            }
            else
            {
                result = RequestProcessor.formatUpdateResult(updateCount);
            }
          
            updateResultsField(result);
           
            hasResultSet = statement.getMoreResults();
        }
     }
    
    private List<String> commandFieldToStrings()
    {
        String commands = commandField.getText().trim();
        List<String> list = new ArrayList<>();
        
        boolean lastCharSemicolon = false;
        StringBuilder sb = new StringBuilder();
        for (char ch: commands.toCharArray())
        {
            sb.append(ch);
            if (ch == ';')
            {
                list.add(sb.toString().trim());
                sb = new StringBuilder();
                lastCharSemicolon = true;
            }
            else
            {
                lastCharSemicolon = false;
            }
        }
        if (!lastCharSemicolon)
        {
            sb.append(';');
            list.add(sb.toString().trim());
        }
        
        return list;
    }
    
    private void updateCurrentDatabaseLabel(Connection connection)
    {
        try (Statement s = connection.createStatement())
        {
            ResultSet rs = s.executeQuery("select database()");
            rs.next();
            String databaseName = rs.getString(1);
            if (databaseName == null || !databaseName.equals(getDatabaseName()))
            {
                setDatabaseName(databaseName);
                if (databaseName != null)
                {
                    currentDatabaseLabel2.setForeground(
                        currentDatabaseLabel1.getForeground());
                    currentDatabaseLabel2.setText(databaseName);
                }
                else
                {
                    currentDatabaseLabel2.setForeground(Color.red);
                    currentDatabaseLabel2.setText("-- none --");
                }
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, 
                "Unable to determine current database.", "Error", 
                JOptionPane.ERROR_MESSAGE);
            setDatabaseName("???");
        }
        
        if (getDatabaseName() == null)
        {
            commandField.setEnabled(false);
            executeButton.setEnabled(false);
            loadButton.setEnabled(false);
        }
    }
    
    // This should not be run on the event thread.
    private void executeCommandsThreadBody(List<String> commands)
    {
        try (Connection connection = 
            Utils.createDatabaseConnection(getProfile(), getDatabaseName()))
        {
            for (String command: commands)
            {
                try (Statement statement = connection.createStatement())
                {
                    executeOneCommand(command, statement);
                    updateCurrentDatabaseLabel(connection);
                }
            }
            TreePanel.getInstance().refreshAllDatabaseInfo();
        }
        catch (SQLException e)
        {
            updateResultsField(e.getMessage());
        }
    }
    
    private void executeCommands()
    {
        disableButtons();
        resultsField.setText("");
        
        final List<String> commands = commandFieldToStrings();
        new Thread(() ->
        {
            executeCommandsThreadBody(commands);
            enableButtons();
        }).start();
    }
    
    private void loadScript(File file)
    {
        StringBuilder sb = new StringBuilder();

        try (Reader in = new FileReader(file))
        {
            for (;;)
            {
                int ch = in.read();
                if (ch == -1)
                    break;
                sb.append((char) ch);
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        commandField.setText(sb.toString());
        
        scriptFile = file;
        
        renameTab(file.getName());
        
        setContentModified(false);
        updateTab(false);
        saveButton.setEnabled(false);
    }
    
    private void loadScript()
    {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "SQL scripts", "SQL", "sql");
        chooser.setFileFilter(filter);

        String directory = 
            Properties.getInstance().get("fileLoadDirectoryPath");
        if (directory != null)
            chooser.setCurrentDirectory(new File(directory));

        int ok = chooser.showOpenDialog(this);
        if (ok == JFileChooser.APPROVE_OPTION) 
        {
            loadScript(chooser.getSelectedFile());
            
            try
            {
                Properties.getInstance().set("fileLoadDirectoryPath",
                    scriptFile.getParentFile().getAbsolutePath()).update();
            }
            catch (IOException e)
            {
                // Let's not worry about this.  It's not that important.
            }
        }
    }

    private boolean saveCurrentScriptInNewFile()
    {
        // Look up previous folder where a file was last saved
        String fileSaveDirectoryPath = 
            Properties.getInstance().get("fileSaveDirectoryPath");
        
        // Configure file chooser
        JFileChooser chooser = new JFileChooser();
        if (fileSaveDirectoryPath != null)
            chooser.setCurrentDirectory(new File(fileSaveDirectoryPath));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "SQL scripts", "SQL", "sql");
        chooser.setFileFilter(filter);
        
        // Display file chooser
        int ok = chooser.showSaveDialog(this);
        
        // Save file to selected file if something was selected
        if (ok != JFileChooser.APPROVE_OPTION) 
            return false;
        
        scriptFile = chooser.getSelectedFile();
            
        // File exists?
        if (scriptFile.exists())
        {
            int result = JOptionPane.showConfirmDialog(this,
                "That file already exists.  Overwrite?", "File Exists",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.CANCEL_OPTION)
                return false;
        }
           
        // Make sure that the file name has the .sql extension
        String path = scriptFile.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".sql"))
        {
            path += ".sql";
            scriptFile = new File(path);
        }
            
        // Save file to disk
        if (!saveCurrentScript())
            return false;

        // Disable button
        saveButton.setEnabled(false);
            
        // Update tab label
        renameTab(scriptFile.getName());
            
        // Update properties
        try
        {
            Properties.getInstance().set("fileSaveDirectoryPath",
                scriptFile.getParentFile().getAbsolutePath()).update();
        }
        catch (IOException e)
        {
            // Let's not worry about this.  It's not that important.
        }

        return true;
    }
    
    private boolean saveCurrentScript()
    {
        if (scriptFile == null)
            return saveCurrentScriptInNewFile();
        
        try (Writer out = new FileWriter(scriptFile))
        {
            out.write(commandField.getText());
            setContentModified(false);
            updateTab(false);
            return true;
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(),"Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean okToClose()
    {
        if (!isContentModified())
            return true;
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Content has been modified.  Do you want to save it?", 
            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.NO_OPTION)
            return true;
                
        return saveCurrentScript();
    }
    
    private void updateTab(boolean contentsUpdated)
    {
        if (getTabComponent() == null)
            return;
        
        getTabComponent().contentsUpdated(contentsUpdated);
    }
    
    private class ExecuteButton extends JButton
    {
        public ExecuteButton()
        {
            super("Execute");
            setToolTipText("Execute this script");
            setEnabled(false);
            addActionListener(e ->
            {
                // This should not be necessary
                try 
                {
                    Class.forName("com.mysql.jdbc.Driver"); 
                    executeCommands();
                }
                catch (ClassNotFoundException f) 
                {
                }
           });
        }
    }
    
    private class LoadButton extends JButton
    {
        public LoadButton()
        {
            super("Load");
            setToolTipText("Load a script from a file");
            addActionListener(e -> doAction());
        }
        
        private void doAction()
        {
            if (isContentModified())
            {
                int results = JOptionPane.showConfirmDialog(getParent(),
                    "Contents have changed and not been saved.  Do you want " +
                    "to save them?", "Contents Changed", JOptionPane.YES_NO_OPTION);
                if (results == JOptionPane.YES_OPTION)
                    saveCurrentScript();
            }
            
            loadScript();
        }
    }
    
    private class SaveButton extends JButton
    {
        public SaveButton()
        {
            super("Save");
            setEnabled(false);
            setToolTipText("Save this script");
            addActionListener(e -> saveCurrentScript());
        }
    }
    
    private class SaveAsButton extends JButton
    {
        public SaveAsButton()
        {
            super("Save As");
            setEnabled(false);
            setToolTipText("Save this script to a new file");
            addActionListener(e -> saveCurrentScriptInNewFile());
        }
    }
    
    private class CustomDocumentListener implements DocumentListener
    {
        private void updateButtons()
        {
            boolean notEmpty = commandField.getDocument().getLength() > 0;
            if (isContentModified() != notEmpty)
            {
                updateTab(!isContentModified());
                setContentModified(notEmpty);
                updateTab(notEmpty);
            }
            executeButton.setEnabled(isContentModified());
            saveButton.setEnabled(isContentModified());
            saveAsButton.setEnabled(isContentModified());
            
        }
        
        public void changedUpdate(DocumentEvent e)
        {
            updateButtons();
        }
        
        public void insertUpdate(DocumentEvent e)
        {
            updateButtons();
        }
        
        public void removeUpdate(DocumentEvent e)
        {
            updateButtons();
        }
    }
}
