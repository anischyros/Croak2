package org.anischyros.croak2.profile;

import org.jdom2.*;

public class Profile 
{
    private String profileName;
    private String address;
    private int port;
    private String userName;
    private String password;
    private String parameters;
    
    public Profile(String profileName, String address, int port,
        String userName, String password, String parameters)
    {
        setProfileName(profileName);
        setAddress(address);
        setPort(port);
        setUserName(userName);
        setPassword(password);
        setParameters(parameters);
    }
    
    public Profile(Element profileElement)
    {
        setProfileName(profileElement.getAttributeValue("name"));
        setAddress(profileElement.getChildText("address"));
        setPort(new Integer(profileElement.getChildText("port")));
        setUserName(profileElement.getChildText("userName"));
        setPassword(profileElement.getChildText("password"));
        setParameters(profileElement.getChildText("parameters"));
    }
    
    public final void setProfileName(String s) { profileName = s; }
    public final String getProfileName() { return profileName; }
    public final void setAddress(String s) { address = s; }
    public final String getAddress() { return address; }
    public final void setPort(int n) { port = n; }
    public final int getPort() { return port; }
    public final void setUserName(String s) { userName = s; }
    public final String getUserName() { return userName; }
    public final void setPassword(String s) { password = s; }
    public final String getPassword() { return password; }
    public final void setParameters(String s) { parameters = s; }
    public final String getParameters() { return parameters; }
    
    public String toString()
    {
        return String.format("profileName: %s, address: %s, port: %d, " +
            "userName: %s, password: %s, parameters: %s\n", getProfileName(), 
            getAddress(), getPort(), getUserName(), getPassword(), 
            getParameters());
    }
}
