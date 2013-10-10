package com.osum.bot.debugger.impl;

import com.osum.bot.debugger.Debugger;
import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.GameObject;
import com.osum.bot.scripting.api.Model;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/6/13
 * Time: 8:19 AM
 */
public class DebugObjects extends Debugger
{

    public DebugObjects(ScriptContext context)
    {
        super(context);
    }

    @Override
    public void draw(Graphics g, Point p)
    {
        if (context.game.isLoggedIn())
        {
            for (GameObject object : context.objects.getAll())
            {
                try
                {
                    Model model = object.getModel();
                    if (model != null)
                    {
                        for (Polygon tri : model.getTriangles())
                        {
                            g.drawPolygon(tri);
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
