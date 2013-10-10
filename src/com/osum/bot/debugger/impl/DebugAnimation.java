package com.osum.bot.debugger.impl;

import com.osum.bot.debugger.Debugger;
import com.osum.bot.scripting.ScriptContext;

import java.awt.*;

public class DebugAnimation extends Debugger
{

    public DebugAnimation(ScriptContext context)
    {
        super(context);
    }

    @Override
    public void draw(Graphics g, Point p)
    {
        if (context.game.isLoggedIn())
        {
            g.drawString("Animation: " + context.players.getLocalPlayer().getAnimation(), p.x, p.y);
        }
    }

}
