package com.osum.updater.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler
{

    public HashMap<String, String> parameterMap;
    private String gamePack = null;

    public WebCrawler()
    {
        long time = System.currentTimeMillis();
        System.out.println("\nLocating Game Pack...");
        parameterMap = new HashMap<>();
        try
        {
            final String pageSource = getPageSource(getWorldUrl());
            final Pattern regexPattern = Pattern.compile("<param name=\"?([^\\s]+)\"?\\s+value=\"?([^>]*)\"?>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            final Matcher regexMatcher = regexPattern.matcher(pageSource);
            while (regexMatcher.find())
            {
                if (!parameterMap.containsKey(regexMatcher.group(1)))
                {
                    parameterMap.put(regexMatcher.group(1).replaceAll("\"", ""), regexMatcher.group(2).replaceAll("\"", ""));
                }
            }
            gamePack = pageSource.split("'archive=")[1].split(" ")[0];
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getPageSource(final URL url) throws IOException
    {
        try
        {
            URLConnection conn = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null)
            {
                sb.append(line);
            }
            rd.close();
            return sb.toString();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public URL getWorldUrl() throws MalformedURLException
    {
        return new URL("http://world1.runescape.com");
    }

    public URL getGamePackUrl() throws MalformedURLException
    {
        if (gamePack == null)
        {
            new WebCrawler();
        }
        return new URL(getWorldUrl() + "/" + gamePack);
    }

}
