package org.anischyros.croak2.components;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.*;

// This is essentially a JTextPane that supports undo/redo, both via the
// CTRL-Z, SHIFT-CTRL-Z, and CTRL-Y keys and through the application's Edit
// menu's Undo and Redo options.  Nothing special needs to be done to enable
// these features.  When the field receives the focus, the links to the menu
// will be made.  When it loses it, the links are severed.

public class UndoableTextPane extends JTextPane
{
    private UndoManager undoManager;
    
    public UndoableTextPane()
    {
        super();
        configureUndoSupport();
    }
    
    public UndoableTextPane(StyledDocument doc)
    {
        super(doc);
        configureUndoSupport();
    }
    
    private void configureUndoSupport()
    {
        undoManager = new UndoManager();
        getDocument().addUndoableEditListener(undoManager);
        this.addKeyListener(new CustomKeyListener());
    }
    
    private class CustomKeyListener implements KeyListener
    {
        public void keyPressed(KeyEvent e)
        {
            int code = e.getKeyCode();
            int modifiers = e.getModifiersEx();
            if (code == 90 && (modifiers & KeyEvent.CTRL_DOWN_MASK) != 0)
            {
               if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0)
               {
                   if (undoManager.canRedo())
                       undoManager.redo();
               }
               else
               {
                   if (undoManager.canUndo())
                       undoManager.undo();
               }
               e.consume();
            }
            else
            if (code == 89 && (modifiers & KeyEvent.CTRL_DOWN_MASK) != 0)
            {
                if (undoManager.canRedo())
                    undoManager.redo();
                e.consume();
            }
        }
        
        public void keyReleased(KeyEvent e)
        {
        }
        
        public void keyTyped(KeyEvent e)
        {
        }
    }
}
