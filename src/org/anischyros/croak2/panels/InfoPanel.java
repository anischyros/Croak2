package org.anischyros.croak2.panels;

import java.awt.*;
import javax.swing.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.tabs.*;

public class InfoPanel extends JTabbedPane
{
    private static InfoPanel instance = null;
    
    private InfoPanel()
    {
        super();
        setPreferredSize(new Dimension(600, 550));
    }
    
    public void addTab(CustomTab tab)
    {
        super.addTab(tab.getName(), tab);
        int index = getTabCount() - 1;
        tab.setTabComponent(new ButtonTabComponent(this, tab));
        setTabComponentAt(index, tab.getTabComponent());
        setSelectedIndex(index);
    }
    
    public void addTab(String tabName, CustomTab tab)
    {
        tab.setName(tabName);
        this.addTab(tab);
    }
    
    public boolean tabExists(String name)
    {
        for (int i = 0; i < getTabCount(); i++)
        {
            if (getTitleAt(i).equals(name))
                return true;
        }
        return false;
    }
    
    public boolean requestFocusInWindow()
    {
        Component tab = getSelectedComponent();
        if (tab == null)
            return false;
        return tab.requestFocusInWindow();
    }
    
    public static final synchronized InfoPanel getInstance()
    {
        if (instance != null)
            return instance;
        instance = new InfoPanel();
        return instance;
    }
}
