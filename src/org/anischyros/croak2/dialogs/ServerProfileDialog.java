package org.anischyros.croak2.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import org.anischyros.croak2.components.*;
import org.anischyros.croak2.profile.*;
import org.anischyros.croak2.utils.*;

public class ServerProfileDialog extends JDialog implements DocumentListener
{
    private UndoableTextField profileNameField;
    private UndoableTextField addressField;
    private NumberField portField;
    private UndoableTextField userNameField;
    private UndoableTextField passwordField;
    private UndoableTextField parametersField;
    private JButton okButton;
    private JButton cancelButton;

    private boolean cancelled = true;
    private final String okButtonText;
    private final int okButtonMnemonic;

    private ServerProfileDialog(Window parent, String title, 
        String okButtonText, int okButtonMnemonic)
    {
        super(parent);
        this.okButtonText = okButtonText;
        this.okButtonMnemonic = okButtonMnemonic;
        
        setTitle(title);
        setModal(true);
        layoutComponents();
        pack();
        Utils.center(this);
        setResizable(false);
        setVisible(true);
    }
    
    private ServerProfileDialog(Window parent, String title, 
        String okButtonText, int okButtonMnemonic, Profile profile)
    {
        super(parent);
        this.okButtonText = okButtonText;
        this.okButtonMnemonic = okButtonMnemonic;
        
        setTitle(title);
        setModal(true);
        layoutComponents();
        pack();
        Utils.center(this);
        setResizable(false);
        
        profileNameField.setText(profile.getProfileName());
        profileNameField.setEditable(false);
        addressField.setText(profile.getAddress());
        if (profile.getPort() != 0)
            portField.setText(String.valueOf(profile.getPort()));
        userNameField.setText(profile.getUserName());
        passwordField.setText(profile.getPassword());
        parametersField.setText(profile.getParameters());
        
        setVisible(true);
    }

    private void layoutComponents()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));
        add(panel);

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(5, 20, 5, 20);
        
        JLabel label = new JLabel("Profile name");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, c);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        profileNameField = new UndoableTextField(16);
        profileNameField.getDocument().addDocumentListener(this);
        panel.add(profileNameField, c);
        
        c.gridwidth = 1;
        label = new JLabel("Address");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        addressField = new UndoableTextField(16);
        addressField.getDocument().addDocumentListener(this);
        panel.add(addressField, c);

        c.gridwidth = 1;
        label = new JLabel("Port");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        portField = new NumberField(16);
        panel.add(portField, c);

        c.gridwidth = 1;
        label = new JLabel("User name");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        userNameField = new UndoableTextField(16);
        userNameField.getDocument().addDocumentListener(this);
        panel.add(userNameField, c);

        c.gridwidth = 1;
        label = new JLabel("Password");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, c);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        passwordField = new UndoableTextField(16);
        passwordField.getDocument().addDocumentListener(this);
        panel.add(passwordField, c);

        c.gridwidth = 1;
        label = new JLabel("Parameters");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        parametersField = new UndoableTextField(16);
        panel.add(parametersField, c);

        c.gridheight = GridBagConstraints.REMAINDER;
        c.insets = new Insets(20, 5, 5, 5);
        okButton = new OkButton(okButtonText, okButtonMnemonic);
        cancelButton = new CancelButton();
        Box buttonsBox = Box.createHorizontalBox();
        buttonsBox.add(Box.createHorizontalGlue());
        buttonsBox.add(okButton);
        buttonsBox.add(Box.createHorizontalStrut(10));
        buttonsBox.add(cancelButton);
        buttonsBox.add(Box.createHorizontalGlue());
        panel.add(buttonsBox, c);
    }
    
    public void changedUpdate(DocumentEvent e) {};
    
    private boolean okayToEnableOkButton()
    {
        String profileName = profileNameField.getText().trim();
        String address = addressField.getText().trim();
        String userName = userNameField.getText().trim();
        String password = passwordField.getText().trim();
        return profileName.length() > 0 && address.length() > 0 &&
            userName.length() > 0 && password.length() > 0;
    }

    public void insertUpdate(DocumentEvent e)
    {
        okButton.setEnabled(okayToEnableOkButton());
    }

    public void removeUpdate(DocumentEvent e)
    {
        okButton.setEnabled(okayToEnableOkButton());
    }
        
    public static Profile display(Window parent, String title, 
        String okButtonText, int okButtonMnemonic)
    {
        ServerProfileDialog dialog = new ServerProfileDialog(parent, title,
            okButtonText, okButtonMnemonic);
        if (dialog.cancelled)
            return null;
        
        int port = 0;
        String portString = dialog.portField.getText().trim();
        if (portString.length() > 0)
            port = new Integer(portString);
        
        return new Profile(dialog.profileNameField.getText().trim(),
            dialog.addressField.getText().trim(), port, 
            dialog.userNameField.getText().trim(), 
            dialog.passwordField.getText().trim(),
            dialog.parametersField.getText().trim());
    }
    
    public static Profile display(Window parent, String title, 
        String okButtonText, int okButtonMnemonic, Profile profile)
    {
        ServerProfileDialog dialog = new ServerProfileDialog(parent, title,
            okButtonText, okButtonMnemonic, profile);
        if (dialog.cancelled)
            return null;
        
        int port = 0;
        String portString = dialog.portField.getText().trim();
        if (portString.length() > 0)
            port = new Integer(portString);
        
        return new Profile(dialog.profileNameField.getText().trim(),
            dialog.addressField.getText().trim(), port, 
            dialog.userNameField.getText().trim(), 
            dialog.passwordField.getText().trim(),
            dialog.parametersField.getText().trim());
    }
    
    private class OkButton extends JButton
    {
        public OkButton(String okButtonName, int okButtonMnemonic)
        {
            super(okButtonName);
            setMnemonic(okButtonMnemonic);
            setEnabled(false);
            addActionListener( e ->
            {
                cancelled = false;
                setVisible(false);
                dispose();
            });
        }
    }
    
    private class CancelButton extends JButton
    {
        public CancelButton()
        {
            super("Cancel");
            setMnemonic('C');
            addActionListener(e ->
            {
                cancelled = true;
                setVisible(false);
                dispose();
            });
       }
    }
}

