package com.osum.bot.scripting;

import com.osum.bot.Bot;
import com.osum.bot.interfaces.Client;
import com.osum.bot.scripting.api.methods.*;

import java.util.Random;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 1:16 AM
 */
public class ScriptContext
{
    private final Random random = new Random();
    
    public Game game = new Game(this);
    public Players players = new Players(this);
    public NPCs npcs = new NPCs(this);
    public Widgets widgets = new Widgets(this);
    public GroundItems groundItems = new GroundItems(this);
    public Objects objects = new Objects(this);
    public Inventory inventory = new Inventory(this);
    public Bank bank = new Bank(this);
    public Camera camera = new Camera(this);
    public Walking walking = new Walking(this);
    public Mouse mouse = new Mouse(this);
    public Keyboard keyboard = new Keyboard(this);
    public Menu menu = new Menu(this);
    public Client client;
    public Bot bot;
    
    protected void setClient(Client client)
    {
        this.client = client;
        mouse.setup(client);
        keyboard.setup(client);
    }
    
    public final double random()
    {
    	return random.nextDouble();
    }

    public final int random(int min, int max)
    {
        int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
    }

    public final double random(double min, double max)
    {
        return Math.min(min, max) + random.nextDouble() * Math.abs(max - min);
    }

    public final Random getRandom()
    {
        return random;
    }

    public void sleep(long l)
    {
        try
        {
            Thread.sleep(l);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
