package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Tile;

import java.awt.event.KeyEvent;

/**
 * User: Marty
 * Date: 4/3/13
 * Time: 4:58 PM
 */
public class Walking
{

    private ScriptContext context;

    public Walking(ScriptContext context)
    {
        this.context = context;
    }

    public void walkScreen(Tile tile)
    {
        context.mouse.moveMouse(context.camera.tileToScreen(tile, 5, 5));
        context.mouse.leftClick();
    }

    public void walkMinimap(Tile tile, boolean run)
    {
        if (run)
        {
            KeyEvent e = new KeyEvent(context.bot.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_CONTROL, (char) KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT);
            context.client.getKeyboard()._keyPressed(e);
            context.client.getKeyboard()._keyTyped(e);
            context.sleep(250);
        }
        context.mouse.moveMouse(context.camera.tileToMinimap(tile));
        context.mouse.leftClick();
        if (run)
        {
            KeyEvent e = new KeyEvent(context.bot.getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_CONTROL, (char) KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT);
            context.client.getKeyboard()._keyReleased(e);
            context.sleep(250);
        }
    }

    public void walkMinimap(Tile tile, int randX, int randY)
    {
        int x = tile.getX() + context.random(-randX, randX);
        int y = tile.getY() + context.random(-randY, randY);
        context.mouse.moveMouse(context.camera.tileToMinimap(new Tile(x, y)));
        context.mouse.leftClick();
    }

}
