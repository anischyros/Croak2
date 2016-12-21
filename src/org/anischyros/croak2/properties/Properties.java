package org.anischyros.croak2.properties;

import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.*;

public class Properties 
{
    private static final String PROPERTIES_FILE_NAME = "Croak2Properties.xml";
    
    private static Properties instance = null;
    
    private Map<String, String> properties = new HashMap<>();
    
    private Properties()
    {
        try
        {
            load();
        }
        catch (IOException | JDOMException e)
        {
        }
    }
    
    public Properties set(String name, String value)
    {
        properties.put(name, value);
        return this;
    }
    
    public Properties set(String name, int value)
    {
        set(name, Integer.toString(value));
        return this;
    }
    
    public String get(String name)
    {
        return properties.get(name);
    }
    
    public Properties load() throws IOException, JDOMException
    {
        try (FileReader in = new FileReader(PROPERTIES_FILE_NAME))
        {
            Element root = new SAXBuilder().build(in).getRootElement();
            for (Element child: root.getChildren())
                properties.put(child.getName(), child.getTextTrim());
        }
        catch (FileNotFoundException e)
        {
        }
        
        return this;
    }
    
    public Properties update() throws IOException
    {
        try (PrintWriter out = new PrintWriter(new File(PROPERTIES_FILE_NAME)))
        {
            out.println("<Croak2Properties>");
            for (String name: properties.keySet())
            {
                out.println(String.format("    <%s>%s</%s>", name, 
                    properties.get(name), name));
            }
            out.println("</Croak2Properties>");
        }
        catch (FileNotFoundException e)
        {
        }
        
        return this;
    }
    
    public static synchronized Properties getInstance()
    {
        if (instance == null)
            instance = new Properties();
        return instance;
    }
}
