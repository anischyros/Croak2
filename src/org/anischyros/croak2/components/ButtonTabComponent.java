package org.anischyros.croak2.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import org.anischyros.croak2.tabs.*;

public class ButtonTabComponent extends JPanel 
{
    private final JTabbedPane pane;
    
    private TabLabel tabLabel;
    private ButtonMouseListener buttonMouseListener;
 
    public ButtonTabComponent(JTabbedPane pane, CustomTab tab) 
    {
        // Unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) 
            throw new NullPointerException("TabbedPane is null");
        this.pane = pane;
        setOpaque(false);
         
        // Make JLabel read titles from JTabbedPane
        tabLabel = new TabLabel(tab);
        add(tabLabel);
        
        // Tab button
        add(new TabButton());
        
        // Add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
    
    public void contentsUpdated(boolean contentsUpdated)
    {
        tabLabel.contentsUpdated(contentsUpdated);
    }
        
    private class TabLabel extends JLabel
    {
        final private CustomTab tab;
        
        private Color savedForeground = null;
            
        public TabLabel(CustomTab tab)
        {
            super();
               
            this.tab = tab;
                
            // Add more space between the label and the button
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        }
        
        public String getText() 
        {
            if (tab == null)
                return "";
            
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1)
                return pane.getTitleAt(i);
                
            return "";
        }
        
        public void contentsUpdated(boolean contentsUpdated)
        {
            if (contentsUpdated)
            {
                if (savedForeground == null)
                    savedForeground = getForeground();
                setForeground(Color.RED);
            }
            else
            if (savedForeground != null)
                setForeground(savedForeground);
        }
    }
 
    private class TabButton extends JButton
    {
        public TabButton() 
        {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close this tab");
               
            // Make the button looks the same for all LAF's
            setUI(new BasicButtonUI());
             
            // Make it transparent
            setContentAreaFilled(false);
               
            // No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
                
            // Making nice rollover effect we use the same listener for all
            // buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
                
            // Close the proper tab by clicking the button
            addActionListener(e -> onAction());
        }
 
        public void onAction()
        {
            int index = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (index < 0)
                return;
                
            CustomTab tab = (CustomTab) pane.getComponentAt(index);
            if (!tab.okToClose())
                return;
                
            pane.remove(index);
            tab.disposeResources();
        }
 
        // We don't want to update UI for this button
        public void updateUI() 
        {
        }
 
        // Paint the cross
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
                
            // Shift the image for pressed buttons
            if (getModel().isPressed()) 
                g2.translate(1, 1);
              
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) 
                g2.setColor(Color.RED);
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, 
                getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, 
                getHeight() - delta - 1);
            g2.dispose();
        }
    }
    
    private class ButtonMouseListener extends MouseAdapter
    {
        public void mouseEntered(MouseEvent e) 
        {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) 
            {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }
 
        public void mouseExited(MouseEvent e) 
        {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) 
            {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}