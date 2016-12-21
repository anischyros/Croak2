package org.anischyros.croak2.profile;

import java.util.*;
import java.io.*;
import org.jdom2.*;
import org.jdom2.input.*;

public class ProfileManager 
{
    private static ProfileManager instance = null;
    
    private final String PROFILES_FILE_NAME = "Croak2Profile.xml";
    
    private Map<String, Profile> profileMap = new HashMap<>();
    
    private ProfileManager()
    {
        try
        {
            loadProfiles();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    public void updateProfile(Profile profile) throws IOException
    {
        profileMap.put(profile.getProfileName(), profile);

        try (PrintWriter out = new PrintWriter(new File(PROFILES_FILE_NAME)))
        {
            out.println("<profiles>");
            profileMap.keySet().forEach(key ->
            {
                Profile thisProfile = profileMap.get(key);
                out.printf("  <profile name=\"%s\">\n", 
                    thisProfile.getProfileName());
                out.printf("    <address>%s</address>\n", 
                    thisProfile.getAddress());
                out.printf("    <port>%d</port>\n", 
                    thisProfile.getPort());
                out.printf("    <userName>%s</userName>\n", 
                    thisProfile.getUserName());
                out.printf("    <password>%s</password>\n", 
                    thisProfile.getPassword());
                out.printf("    <parameters>%s</parameters>\n", 
                    thisProfile.getParameters());
                out.println("  </profile>");
            });
            out.println("</profiles>");
        }
    }
    
    public void removeProfile(String profileName) throws IOException
    {
        profileMap.remove(profileName);
        saveAllProfiles();
    }
    
    public Profile[] getAllProfiles()
    {
        Set<String> keys = profileMap.keySet();
        Profile[] profiles = new Profile[keys.size()];
        int i = 0;
        for (String key: keys)
            profiles[i++] = profileMap.get(key);
        Arrays.sort(profiles, 
            (a, b) -> a.getProfileName().compareTo(b.getProfileName()));
        return profiles;
    }
    
    public boolean isEmpty()
    {
        return (profileMap.isEmpty());
    }
    
    public Profile getProfile(String name)
    {
        return profileMap.get(name);
    }
    
    private void loadProfiles() throws IOException, JDOMException
    {
        try (FileReader in = new FileReader(PROFILES_FILE_NAME))
        {
            Element root = new SAXBuilder().build(in).getRootElement();
            root.getChildren().forEach(child ->
            {
                Profile profile = new Profile(child);
                profileMap.put(profile.getProfileName(), profile);
            });
        }
        catch (FileNotFoundException e)
        {
        }
    }
    
    private void saveAllProfiles() throws IOException
    {
        Profile[] profiles = getAllProfiles();
        for (Profile profile: profiles)
            updateProfile(profile);
    }
    
    public synchronized static final ProfileManager getInstance()
    {
        if (instance != null)
            return instance;
        instance = new ProfileManager();
        return instance;
    }
    
    
    public static String createURLString(Profile profile, String databaseName)
    {
        String url;
        if (profile.getPort() > 0)
            url = String.format("jdbc:mysql://%s:%d/%s", profile.getAddress(),
               profile.getPort(), databaseName);
        else
            url = String.format("jdbc:mysql://%s/%s", profile.getAddress(),
                databaseName);
        url = url + String.format("?user=%s&password=%s", profile.getUserName(),
            profile.getPassword());
        if (profile.getParameters().length() > 0)
            url = url + "&" + profile.getParameters();
        return url;
    }
}
