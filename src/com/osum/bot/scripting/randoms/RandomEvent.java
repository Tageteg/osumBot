package com.osum.bot.scripting.randoms;

import com.osum.bot.scripting.ScriptContext;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 2:57 AM
 */
public abstract class RandomEvent
{

    public ScriptContext context;

    public boolean run(ScriptContext context)
    {
        this.context = context;
        if (isActive())
        {
            execute();
            return true;
        }
        return false;
    }

    protected abstract boolean isActive();

    protected abstract void execute();

}