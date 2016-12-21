package org.anischyros.croak2.menuitems;

import javax.swing.*;
import org.anischyros.croak2.panels.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.tabs.*;

public class PerformQueryInSelectedDatabaseMenuItem extends JMenuItem
{
    private Profile profile = null;
    private String databaseName = null;

    public PerformQueryInSelectedDatabaseMenuItem()
    {
        super("Perform query in selected database");
        setEnabled(false);
        addActionListener(e -> onAction());
    }
    
    private void onAction()
    {
        if (profile == null || databaseName == null)
            return;
        
        QueryDatabaseTab tab = new QueryDatabaseTab(profile, databaseName);
        InfoPanel.getInstance().addTab("Unnamed.sql", tab);
    }
    
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }
    
    public Profile getProfile()
    {
        return profile;
    }
    
    public void setDatabaseName(String databaseName)
    {
        this.databaseName = databaseName;
    }
    
    public String getDatabaseName()
    {
        return databaseName;
    }
    
    public void setEnabled(boolean enabled)
    {
        if (enabled)
        {
            setText(String.format("<html>Perform query in " +
                "<font color=\"green\">%s</font> database</html>", 
                databaseName));
        }
        else
            setText("Perform query in selected database");
        super.setEnabled(enabled);
    }
}
    
