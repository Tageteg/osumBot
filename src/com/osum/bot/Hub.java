package com.osum.bot;

import com.osum.common.FileUtils;
import com.osum.common.WebUtils;
import org.objectweb.asm.tree.ClassNode;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.jar.JarInputStream;

public class Hub extends JPanel
{
    public class Home extends JPanel
    {
        private static final long serialVersionUID = 1L;

        public Home()
        {
            setLayout(new BorderLayout());
            setBackground(Color.BLACK);
            setForeground(Color.WHITE);
            setPreferredSize(Globals.CLIENT_DIMENSION);
            JLabel welcome = new JLabel("Welcome to osumBot");
            welcome.setForeground(Color.WHITE);
            welcome.setHorizontalAlignment(JLabel.CENTER);
            add(welcome, BorderLayout.CENTER);
            add(WorldList.CURRENT, BorderLayout.PAGE_END);
        }
    }

    public class Loader extends JPanel
    {
        private static final long serialVersionUID = 1L;

        private final Font font = new Font("Helvetica", Font.BOLD, 13);
        private final FontMetrics fontMetrics = getFontMetrics(font);
        private String loadingText = "";
        private boolean loaded = false;
        private int totalPercent = 0;

        public Loader()
        {
            setBackground(Color.BLACK);
            setForeground(new Color(140, 17, 17));
            drawLoadingText(0, "Initializing bot");
            setPreferredSize(Globals.CLIENT_DIMENSION);
        }

        @Override
        public final void paint(Graphics g)
        {
            g.setColor(getBackground()); // set background color
            g.fillRect(0, 0, getWidth(), getHeight());
            int j = getHeight() / 2 - 18;
            int h = totalPercent * 3; // because the width of the loading bar is 300px
            g.setColor(getForeground()); // set foreground color
            g.drawRect(getWidth() / 2 - 152, j, 303, 33); // draw the outer box (1px spacing)
            g.fillRect(getWidth() / 2 - 150, j + 2, h, 30); // draw the inner box (30 high)
            g.setColor(getBackground()); // set background color
            g.fillRect((getWidth() / 2 - 150) + h, j + 2, 300 - h, 30); // fill the rest
            g.setFont(font); // now draw our text in the middle
            g.setColor(Color.WHITE);
            g.drawString(loadingText, (getWidth() - fontMetrics.stringWidth(loadingText)) / 2, j + 22);
        }

        public final void drawLoadingText(int percent, String text)
        {
            totalPercent = percent;
            loadingText = text;
            repaint();
        }

        private byte[] loadGamePack(String address) throws IOException
        {
            URLConnection connection = WebUtils.createConnection(address, true);
            int totalAmount = connection.getContentLength();
            InputStream stream = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2046];

            if (totalAmount == -1)
            {
                totalAmount = stream.available();
            }

            boolean knownSize = true;
            int read = -1;

            while ((read = stream.read(buffer, 0, buffer.length)) != -1)
            {
                out.write(buffer, 0, read);

                if (totalAmount < out.size())
                {
                    totalAmount = out.size();
                    knownSize = false;
                }

                if (knownSize)
                {
                    int newPercent = (out.size() * 100) / totalAmount; // get percentage completed

                    if (newPercent != totalPercent)
                    {
                        drawLoadingText(newPercent, "Loading game code - " + newPercent + "% (" + out.size() + " bytes)");
                        totalPercent = newPercent; // prevent redrawing every read unless it actually needs to
                    }
                } else
                {
                    drawLoadingText(0, "Loading game code (" + out.size() + " bytes)");
                }
            }

            stream.close();
            out.flush();
            return out.toByteArray();
        }

        public void load() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException, URISyntaxException
        {
            loaded = false;

            if (!loaded)
            {
                drawLoadingText(5, "Initializing Environment");
                System.out.println("Initializing environment");
            } else
            {
                drawLoadingText(5, "Re-initializing Environment");
                System.out.println("Re-initializing environment");
                Globals.GAMEPACK_PARAMETERS = null;
                Globals.GAMEPACK_LOCAL_URL = null;
            }

            Globals.INITIAL_LOAD_URL = new URL("http://oldschool" + Globals.INITIAL_LOAD_WORLD + ".runescape.com/");
            String pageSource = WebUtils.readPageSource(Globals.INITIAL_LOAD_URL);
            String gamePackName = WebUtils.findGamepackName(pageSource);
            String gamePackAddress = Globals.INITIAL_LOAD_URL.toString() + gamePackName;
            byte[] gamePack = loadGamePack(gamePackAddress);
            Globals.GAMEPACK_PARAMETERS = WebUtils.parseParameters(pageSource);
            
            for (String key : Globals.GAMEPACK_PARAMETERS.keySet())
            {
            	if (Globals.GAMEPACK_PARAMETERS.get(key).contains("slr.ws"))
            	{
                    WorldList.CURRENT.setAddress(Globals.GAMEPACK_PARAMETERS.get(key));
            	}
            }
            
            drawLoadingText(50, "Loading game code");

            if (Globals.DEBUG_MODE)
            {
                System.out.println("Downloaded gamepack: " + gamePackAddress);
                System.out.println("Game parameters:");

                for (String k : Globals.GAMEPACK_PARAMETERS.keySet())
                {
                    System.out.println("\t" + k + " : " + Globals.GAMEPACK_PARAMETERS.get(k));
                }
            }
            
            drawLoadingText(75, "Propogating hooks");
            inject(gamePack);
            drawLoadingText(100, "Environment loaded");
            loaded = true;
        }

        /*private void save(byte[] gamePack) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
        {
            File temp = File.createTempFile("osgp", ".jar");
            FileOutputStream out = new FileOutputStream(temp);
            out.write(gamePack);
            out.close();
            Globals.GAMEPACK_LOCAL_URL = temp.toURI().toURL();
        }*/

        private void inject(byte[] gamePack) throws IOException, URISyntaxException
        {
            File temp = File.createTempFile("osgp", ".jar");
            File[] files = new File(temp.getAbsolutePath().substring(0, temp.getAbsolutePath().lastIndexOf(File.separator))).listFiles();
            
            for (File file : files)
            {
                long lastUsed = temp.lastModified() - file.lastModified();
                if (!file.getName().equals(temp.getName()) && lastUsed > 300000 && file.getName().startsWith("osgp") && file.getName().endsWith(".jar"))
                {
                    try
                    {
                        file.delete();
                    } catch (Exception e)
                    {
                        // probably in use
                    }
                }
            }
            
            temp.deleteOnExit();

            Globals.GAMEPACK_LOCAL_URL = temp.toURI().toURL();
            HashMap<String, ClassNode> classes = FileUtils.parseJAR(new JarInputStream(new ByteArrayInputStream(gamePack)));
            FileUtils.injectHooks(classes);
            FileUtils.dumpJar(new File(Globals.GAMEPACK_LOCAL_URL.toURI()), classes.values().toArray(new ClassNode[classes.size()]));
        }
    }

    private static final long serialVersionUID = 1L;
    private Loader loader = new Loader();
    private Home home = new Home();

    public Hub()
    {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setForeground(new Color(140, 17, 17));
        setPreferredSize(Globals.CLIENT_DIMENSION);
        add(loader);
    }

    public void load() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException, URISyntaxException
    {
        removeAll();
        add(loader);
        loader.load();
        removeAll();
        add(home);
        WorldList.CURRENT.update();
    	validate();
        repaint();
    }
}
