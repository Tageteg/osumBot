package com.osum.bot.debugger;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 9:36 AM
 */
public class DebugManager
{

    private HashMap<String, Debugger> debuggers = new HashMap<>();

    public void paint(Graphics g)
    {
        Color color = g.getColor();
        int y = 30;
        Iterator iterator = debuggers.entrySet().iterator();
        while (iterator.hasNext())
        {
            Debugger debugger = (Debugger) ((Map.Entry) iterator.next()).getValue();
            g.setColor(Color.GREEN);
            try
            {
                debugger.draw(g, new Point(10, y));
                y += 15;
            } catch (Exception e)
            {
            }
        }
        g.setColor(color);
    }

    public void addDebugger(Debugger debugger)
    {
        if (debuggers.get(debugger.getClass().getSimpleName()) == null)
        {
            debuggers.put(debugger.getClass().getSimpleName(), debugger);
        }
    }

    public void removeDebugger(Debugger debugger)
    {
        if (debuggers.get(debugger.getClass().getSimpleName()) != null)
        {
            debuggers.remove(debugger.getClass().getSimpleName());
        }
    }

    public HashMap<String, Debugger> getDebuggers()
    {
        return debuggers;
    }
}
