package com.osum.bot.scripting.api;

import com.osum.bot.scripting.ScriptContext;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 1:11 AM
 */
public class Player extends Actor
{

    private ScriptContext context;

    public Player(ScriptContext context, com.osum.bot.interfaces.Player player) {
        super(context, player);
        this.context = context;
        this.player = player;
    }

    private final com.osum.bot.interfaces.Player player;

    public String getName() {
        return player.getName();
    }

    public int getCombatLevel() {
        return player.getCombatLevel();
    }

    public int getTotalLevel() {
        return player.getTotalLevel();
    }

    public int getHeadIcon() {
        return player.getHeadIcon();
    }

    public int getSkullIcon() {
        return player.getSkullIcon();
    }

    public Model getModel() {
        return new Model(context, player.getModel(), getRealX(), getRealY());
    }

}
