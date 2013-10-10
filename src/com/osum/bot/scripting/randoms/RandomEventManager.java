package com.osum.bot.scripting.randoms;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.randoms.impl.ClickToPlay;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 2:58 AM
 */
public class RandomEventManager
{

    private static ArrayList<RandomEvent> randoms = new ArrayList<RandomEvent>();

    private ScriptContext context;

    public RandomEventManager(ScriptContext context) {
        this.context = context;
    }

    public void init() {
        randoms.add(new ClickToPlay());
    }

    public boolean run() {
        for(RandomEvent random : randoms) {
            if(random.run(context)) {
                Logger.getAnonymousLogger().log(Level.INFO, "Random event detected: " + random.getClass().getSimpleName());
                return true;
            }
        }
        return false;
    }

}