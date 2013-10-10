package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Tile;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 3:45 AM
 */
public class Game
{

    private ScriptContext context;

    public Game(ScriptContext context) {
        this.context = context;
    }

    public boolean isLoggedIn() {
        return getGameState() == 30;
    }
    
    public boolean isLoaded()
    {
    	return getGameState() > 10;
    }
    
    public boolean isLoading()
    {
    	return getGameState() == 5;
    }

    public int getGameState() {
        return context.client.getGameState();
    }

    public Tile getDestination() {
        return new Tile((context.client.getWalkingDestX() >> 7) + context.client.getBaseX(), (context.client.getWalkingDestY() >> 7) + context.client.getBaseY(), context.client.getPlane());
    }

    /**
     * Gets the canvas height.
     *
     * @return The canvas' width.
     */
    public int getWidth() {
        return context.bot.getCanvas().getWidth();
    }

    /**
     * Gets the canvas height.
     *
     * @return The canvas' height.
     */
    public int getHeight() {
        return context.bot.getCanvas().getHeight();
    }

}
