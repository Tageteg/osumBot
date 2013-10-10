package com.osum.bot;

import com.osum.bot.debugger.DebugManager;
import com.osum.bot.test.TestScript;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class BotWindow extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static BotWindow instance = null;
    private static DebugManager debugManager = new DebugManager();

    private ArrayList<Bot> activeBots = new ArrayList<Bot>();
    private JTabbedPane tabPane = new JTabbedPane();
    private Hub homePane = new Hub();
    private int botCount = 0;
    
    public BotWindow() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        instance = this;
        newTitle(null);
        setBackground(Color.BLACK);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(Globals.CLIENT_DIMENSION);
        tabPane.add("Home", homePane);
        add(tabPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        toFront();
    }

    public static void addBot(Bot b)
    {
        instance.activeBots.add(b);
        instance.tabPane.add("Bot " + (instance.botCount++), b);
        instance.tabPane.setSelectedComponent(b);
    }

    public static void removeBot(Bot b)
    {
        instance.activeBots.remove(b);
        instance.tabPane.remove(b);
    }

    public static Bot getBot(Object o) {
        ClassLoader classLoader = o.getClass().getClassLoader();
        
        try
        {
            for(Bot bot : instance.activeBots) {
                if(classLoader == bot.getStub().getClient().getClass().getClassLoader()) {
                    return bot;
                }
            }
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
        
        return null;
    }

    public static void newTitle(String suffix)
    {
        if (suffix == null || suffix.length() == 0)
        {
            instance.setTitle("osumBot");
        }
        else
        {
            instance.setTitle("osumBot - " + suffix);
        }
    }

    public static void main(String[] args) throws Exception
    {
        try
        {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        if (args.length > 0)
        {
            int mode = Integer.parseInt(args[0]);

            if (mode == 1)
            {
                Globals.DEBUG_MODE = true;
            }
        }

        instance = new BotWindow();
        instance.homePane.load();
        Bot b = new Bot(new BotStub(14));
        addBot(b);
        b.start(Globals.GAMEPACK_LOCAL_URL);

        while (b.getStub().getClient().getGameState() != 5)
        {
        	b.run(new TestScript());
        	break;
        }
    }

    public static DebugManager getDebugManager()
    {
        return debugManager;
    }

}
