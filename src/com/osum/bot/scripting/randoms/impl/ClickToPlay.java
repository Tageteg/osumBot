package com.osum.bot.scripting.randoms.impl;

import com.osum.bot.scripting.ScriptManifest;
import com.osum.bot.scripting.randoms.RandomEvent;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 3:00 AM
 */
@ScriptManifest(name = "Click To Play", description = "Clicks the login screen widget.", version = 1.0)
public class ClickToPlay extends RandomEvent
{

    @Override
    protected boolean isActive()
    {
        if(context.widgets.get(378, 17) != null) {
            return true;
        }
        return false;
    }

    @Override
    protected void execute()
    {
        Point p = new Point(context.random(292, 488), context.random(306, 373));
        context.mouse.moveMouse(p);
        context.mouse.leftClick();
        context.sleep(context.random(2000, 2500));
    }

}
