package org.anischyros.croak2.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.tabs.*;

public class InfoPanel extends JTabbedPane implements ChangeListener
{
    private static InfoPanel instance = null;

    private int lastSelectedIndex = -1;
    
    private InfoPanel()
    {
        super();
        setPreferredSize(new Dimension(600, 550));
        addChangeListener(this);
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
    
    public void stateChanged(ChangeEvent e)
    {
        if (lastSelectedIndex >= 0)
            ((CustomTab) getComponentAt(lastSelectedIndex)).exit();
        ((CustomTab) getComponentAt(getSelectedIndex())).enter();
        lastSelectedIndex = getSelectedIndex();
    }
}
