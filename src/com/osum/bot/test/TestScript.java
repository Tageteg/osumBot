package com.osum.bot.test;

import com.osum.bot.BotWindow;
import com.osum.bot.debugger.impl.DebugGameState;
import com.osum.bot.debugger.impl.DebugMouseCursor;
import com.osum.bot.debugger.impl.DebugPosition;
import com.osum.bot.scripting.Script;
import com.osum.bot.scripting.ScriptManifest;
import com.osum.bot.scripting.api.GameObject;
import com.osum.bot.scripting.api.NPC;
import com.osum.bot.scripting.api.Widget;

import java.awt.*;

@ScriptManifest(name = "TestScript", description = "A test script", version = 1.0)
public class TestScript extends Script
{
    @Override
    public boolean init()
    {
        BotWindow.getDebugManager().addDebugger(new DebugGameState(this));
        BotWindow.getDebugManager().addDebugger(new DebugPosition(this));
        BotWindow.getDebugManager().addDebugger(new DebugMouseCursor(this));
        mouse.setInputBlocking(false);
        keyboard.setInputBlocking(false);
        return true;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if (game.isLoggedIn())
        {
            NPC apprentice = npcs.getNearest(5532);
            if(apprentice != null) {
                Point p = apprentice.getPoint();
                g.fillOval(p.x, p.y, 3, 3);
            }
            if (widgets.get(64, 3) != null)
            {
                Widget widget = widgets.get(64, 3);
                g.fillOval((int) widget.getBounds().getCenterX(), (int) widget.getBounds().getCenterY(), 3, 3);
            }
            Point a = camera.tileToScreen(players.getLocalPlayer().getPosition(), 0, 0, 0);
            Point ab = camera.tileToScreen(players.getLocalPlayer().getPosition());
            Point b = camera.tileToScreen(players.getLocalPlayer().getPosition(), 1, 1, 0);
            Point c = camera.tileToMinimap(players.getLocalPlayer().getPosition());
            g.setColor(Color.RED);
            g.fillOval(ab.x, ab.y, 3, 3);
            g.fillOval(c.x, c.y, 3, 3);
            Polygon p = new Polygon();
            p.addPoint(a.x, a.y);
            p.addPoint(a.x, b.y);
            p.addPoint(b.x, b.y);
            p.addPoint(b.x, a.y);
            g.setColor(Color.BLUE);
            g.drawPolygon(p);
            g.setColor(new Color(0, 0, 255, 50));
            g.fillPolygon(p);
        }
    }

    boolean once = false;

    @Override
    public int execute()
    {
        try
        {
            if (!once && client.getGameState() == 10)
            {
                mouse.moveMouse(new Point(459, 305));
                mouse.leftClick();
                //keyboard.sendKey2('c');
                keyboard.sendKeys("marty-@live.com", true);
                //keyboard.sendKeys("niggaplz123", true);
                once = true;
            }
            if (game.isLoggedIn())
            {
                GameObject door = objects.getNearest(1530);
                if (door != null)
                {
                    Point p = door.getModel().getRandomPoint();
                    mouse.moveMouse(p);
                    if (mouse.getPosition().equals(p))
                    {
                        mouse.leftClick();
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        sleep(2500);
        return 0;
    }

}
