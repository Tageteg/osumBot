package com.osum.bot;

import com.osum.bot.interfaces.Client;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

public class BotStub implements AppletStub
{
    private final HashMap<String, String> parameters;
    private final URL pageAddress;
    private Applet applet;
    private Client client;
    private Image buffer;

    public BotStub(int world) throws MalformedURLException
    {
        this(Globals.GAMEPACK_PARAMETERS, new URL("http://oldschool" + world + ".runescape.com/"));
        
        for (String key : parameters.keySet())
        {
        	try
        	{
        		int i = Integer.parseInt(parameters.get(key));
        		
        		if (i > 300)
        		{
        			parameters.put(key, (300 + world) + "");
        		}
        	}
        	catch (Exception ex)
        	{
        		// yolo
        	}
        }
    }

    @SuppressWarnings("unchecked")
	public BotStub(HashMap<String, String> parameters, URL pageAddress)
    {
        this.parameters = (HashMap<String, String>)parameters.clone();
        this.pageAddress = pageAddress;
    }

    public Applet load(URLClassLoader loader) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class<?> clientClass = loader.loadClass("client");
        applet = (Applet)clientClass.newInstance();
        applet.setStub(this);
        client = (Client)applet;
        buffer = new BufferedImage(Globals.CLIENT_DIMENSION.width, Globals.CLIENT_DIMENSION.height, BufferedImage.TYPE_INT_RGB);
        return applet;
    }
    
    public Applet getApplet()
    {
    	return applet;
    }

    public Image getBuffer()
    {
        return buffer;
    }

    public Client getClient()
    {
        return client;
    }

    public boolean isActive()
    {
        return true;
    }

    public URL getDocumentBase()
    {
        return pageAddress;
    }

    public URL getCodeBase()
    {
        return pageAddress;
    }

    public String getParameter(String name)
    {
        return parameters.get(name);
    }

    public AppletContext getAppletContext()
    {
        return null;
    }

    public void appletResize(int width, int height)
    {
        if (Globals.DEBUG_MODE)
        {
            System.out.println("dbg aresize " + width + "," + height);
        }
    }
}
