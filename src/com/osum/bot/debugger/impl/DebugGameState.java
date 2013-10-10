package com.osum.bot.debugger.impl;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.debugger.Debugger;

import java.awt.*;

public class DebugGameState extends Debugger
{

    public DebugGameState(ScriptContext context)
    {
        super(context);
    }

    @Override
    public void draw(Graphics g, Point p) {
        g.drawString("Game State: " + context.client.getGameState(), p.x, p.y);
    }

}
