package com.osum.bot.scripting;

import com.osum.bot.Bot;
import com.osum.bot.BotWindow;
import com.osum.bot.scripting.randoms.RandomEventManager;

import java.awt.*;

public abstract class Script extends ScriptContext implements Runnable
{
	public enum TerminationReason
	{
		EXIT,
		ERROR
	}
	
    private boolean running = false;

    private RandomEventManager randomEventManager = new RandomEventManager(this);

    public final void setParent(Bot parent)
    {
        this.bot = parent;
        setClient(parent.getStub().getClient());
    }
    
    public void paint(Graphics g)
    {
        BotWindow.getDebugManager().paint(g);
    }

    protected boolean init()
    {
        return true;
    }

    protected abstract int execute();

    protected final void destroy()
    {
        destroy(TerminationReason.EXIT);
    }
    
    protected void destroy(TerminationReason reason)
    {
    	// no default operation
    }

    public final Bot parent()
    {
        return bot;
    }

    public final void stop()
    {
        System.out.println("script exited");
        running = false;
    }

    @Override
    public final void run()
    {
    	try
    	{
            if(!init())
            {
                System.out.println("script failure");
                return;
            }
            randomEventManager.init();
            System.out.println("script started");
            running = true;
    
            while(running)
            {

                if(randomEventManager.run())
                {
                    continue;
                }

                int result = execute();
    
                if (result == -1)
                {
                    stop();
                    break;
                }
                
                sleep(1);
            }
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	
        destroy();
    }
}
