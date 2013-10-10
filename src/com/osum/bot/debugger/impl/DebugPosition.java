package com.osum.bot.debugger.impl;

import com.osum.bot.debugger.Debugger;
import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Tile;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 9:36 AM
 */
public class DebugPosition extends Debugger
{

    public DebugPosition(ScriptContext context)
    {
        super(context);
    }

    @Override
    public void draw(Graphics g, Point p)
    {
        if (context.game.isLoggedIn())
        {
            Tile tile = context.players.getLocalPlayer().getPosition();
            g.drawString("Position: (" + tile.getX() + ", " + tile.getY() + ", " + tile.getZ() + ")", p.x, p.y);
            Point mmPoint = context.camera.tileToMinimap(tile);
            g.drawOval(mmPoint.x - 2, mmPoint.y - 2, 4, 4);
            Polygon poly = context.camera.getTileBounds(tile);
            g.drawPolygon(poly);
            g.setColor(new Color(0, 255, 0, 50));
            g.fillPolygon(poly);
        }
    }

}
