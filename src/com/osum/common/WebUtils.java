package com.osum.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Scanner;

/**
 * User: Marty
 * Date: 3/5/13
 * Time: 2:07 AM
 */
public class WebUtils {

    /**
     * Connects to the provided web address and reads source of the page to
     * which the address points.
     *
     * @param address
     *            the address of the page
     * @return the page source
     * @throws IOException
     *             if a connection or reading errors occurs
     */
    public static String readPageSource(URL address) throws IOException {
        Scanner scanner = new Scanner(address.openStream());
        String source = "";
        while (scanner.hasNext()) {
            source += scanner.nextLine() + '\n';
        }
        scanner.close();
        return source;
    }

    /**
     * Downloads a file at the specified address and saves it at the destined
     * location. The connection to the remote file can be established using a
     * fake user client.
     *
     * @param fileAddress
     *            the address of the remote file
     * @param destination
     *            the location to save the file
     * @param fakeUser
     *            whether or not the user client will be faked
     * @return the downloaded file
     * @throws IOException
     *             if a connection or file I/O error occurs
     */
    public static byte[] downloadFile(String fileAddress, boolean fakeUser) throws IOException {
        URLConnection connection = createConnection(fileAddress, fakeUser);
        InputStream input = connection.getInputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        for (int amountRead; (amountRead = input.read(data, 0, 1024)) != -1;) {
            output.write(data, 0, amountRead);
        }
        input.close();
        return output.toByteArray();
    }
    
    public static URLConnection createConnection(String address, boolean fake) throws MalformedURLException, IOException
    {
    	URLConnection connection = new URL(address).openConnection();
    	
        if (fake)
        {
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.7) Gecko/20060909 Firefox/1.5.0.7");
            connection.setRequestProperty("Connection", "Keep-alive");
            connection.setRequestProperty("Keep-Alive", "300");
            connection.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
            connection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        }
        
        return connection;
    }
    
    /**
     * Parses the parameters and their corresponding values from a page and
     * stores them in a {@code HashMap}.
     *
     * @param pageSource
     *            the page source from which the parameters will be parsed
     * @return the mappings of the values to the parameters
     */
    public static HashMap<String, String> parseParameters(String pageSource) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        String[] lines = pageSource.split("\n");
        String paramNameBeginning = "param name=";
        String valueBeginning = "value=";
        for (String line : lines) {
            if (!line.contains(paramNameBeginning)) {
                continue;
            }
            int start = line.indexOf(paramNameBeginning)
                    + paramNameBeginning.length() + 1;
            int end = line.indexOf('"', start);
            String name = line.substring(start, end);
            start = line.indexOf(valueBeginning) + valueBeginning.length() + 1;
            end = line.indexOf('"', start);
            String value = line.substring(start, end);
            parameters.put(name, value);
        }
        return parameters;
    }

    /**
     *
     * @param pageSource
     * @return
     */
    public static String findGamepackName(String pageSource) {
        String gamepackFileNameStart = "archive=";
        int gamepackFileNameStartIndex = pageSource
                .indexOf(gamepackFileNameStart);
        return pageSource.substring(gamepackFileNameStartIndex
                + gamepackFileNameStart.length(),
                pageSource.indexOf('\'', gamepackFileNameStartIndex));
    }
}
