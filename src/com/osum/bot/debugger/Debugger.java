package com.osum.bot.debugger;

import com.osum.bot.scripting.ScriptContext;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 9:35 AM
 */
public abstract class Debugger
{
    public ScriptContext context;

    public Debugger(ScriptContext context) {
        this.context = context;
    }

    public abstract void draw(Graphics g, Point point);
}
