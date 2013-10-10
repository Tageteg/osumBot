package com.osum.bot.scripting.api;

import com.osum.bot.scripting.ScriptContext;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 6:00 AM
 */
public class Item
{

    private ScriptContext context;

    private int index;

    private Widget widget;

    public Item(ScriptContext context, int index, Widget widget) {
        this.context = context;
        this.index = index;
        this.widget = widget;
    }

    public int getIndex()
    {
        return index;
    }

    public int getID()
    {
        return widget.getInventory()[index] - 1;
    }

    public int getStackSize()
    {
        return widget.getInventoryStackSizes()[index];
    }

    public Point getPoint() {
        int col = (index % 4);
        int row = (index / 4);
        int x = 580 + (col * 42);
        int y = 228 + (row * 36);
        return new Point(x, y);
    }

    public Point getRandomPoint() {
        Point loc = getPoint();
        loc.setLocation(loc.x + (-12 + context.random(0, 24)), loc.y + (-12 + context.random(0, 24)));
        return loc;
    }

}
