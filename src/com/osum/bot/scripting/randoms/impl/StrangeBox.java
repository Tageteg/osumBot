package com.osum.bot.scripting.randoms.impl;

import com.osum.bot.scripting.ScriptManifest;
import com.osum.bot.scripting.randoms.RandomEvent;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 2:59 AM
 */
@ScriptManifest(name = "Strange Box", description = "Completes the strange box random.", version = 1.0)
public class StrangeBox extends RandomEvent
{

    @Override
    protected boolean isActive()
    {
        return false;
    }

    @Override
    protected void execute()
    {

    }

}
