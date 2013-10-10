package com.osum.bot.scripting.api;

import com.osum.bot.scripting.ScriptContext;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 5:40 AM
 */
public class GroundItem
{

    private ScriptContext context;

    private int id;

    private int amount;

    private Tile tile;

    public GroundItem(ScriptContext context, int id, int amount, Tile tile) {
        this.context = context;
        this.id = id;
        this.amount = amount;
        this.tile = tile;
    }

    public int getID() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public Tile getPosition() {
        return tile;
    }


}
