package org.anischyros.croak2.tabs;

import javax.swing.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.profile.*;

public abstract class CustomTab extends JPanel
{
    private final Profile profile;
    
    private String databaseName;
    private boolean contentModified;
    private ButtonTabComponent tabComponent = null;
    
    public CustomTab(Profile profile, String databaseName)
    {
        super();
        this.profile = profile;
        this.databaseName = databaseName;
        this.contentModified = false;
    }
    
    public Profile getProfile()
    {
        return profile;
    }
    
    public String getDatabaseName()
    {
        return databaseName;
    }
    
    public void setDatabaseName(String databaseName)
    {
        this.databaseName = databaseName;
    }
    
    public boolean isContentModified()
    {
        return contentModified;
    }
    
    public void setContentModified(boolean contentModified)
    {
        this.contentModified = contentModified;
    }
    
    public void setTabComponent(ButtonTabComponent tabComponent)
    {
        this.tabComponent = tabComponent;
    }
    
    public ButtonTabComponent getTabComponent()
    {
        return tabComponent;
    }
    
    public boolean okToClose()
    {
        return true;
    }
    
    private int findPositionInPane(String name)
    {
        JTabbedPane tabbedPane = (JTabbedPane) getParent();

        // There appears to be an off-by-one bug in JTabbedPane created because
        // of the way InfoPanel uses it.  Subtracting one from from the count is
        // the workaround.
        int count = tabbedPane.getComponentCount() - 1;
        
        for (int i = 0; i < count; i++)
        {
            if (tabbedPane.getComponentAt(i) == this)
                return i;
        }
        
        return -1;
    }
    
    public void renameTab(String name)
    {
        setName(name);
        int pos = findPositionInPane(name);
        if (pos < 0)
            return;
        
        JTabbedPane tabbedPane = (JTabbedPane) getParent();
        tabbedPane.setTitleAt(pos, name);
    }
    
    public abstract void disposeResources();

    // Called when tag is entered
    public void enter()
    {
    }
    
    // Called when tag is exited
    public void exit()
    {
    }
}
