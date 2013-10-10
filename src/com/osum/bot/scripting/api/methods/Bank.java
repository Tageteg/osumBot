package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Item;
import com.osum.bot.scripting.api.Widget;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 12:00 PM
 */
public class Bank
{

    private ScriptContext context;


    public static final int BANK_EXIT = 103;

    public static final int WIDGET_ID = 12;

    public static final int ITEM_PANE_ID = 89;

    public static final int CAPACITY = 400;

    public Bank(ScriptContext context)
    {
        this.context = context;
    }

    public boolean isOpen()
    {
        if (context.widgets.get(WIDGET_ID) != null && context.widgets.get(WIDGET_ID, ITEM_PANE_ID) != null
                && context.widgets.get(WIDGET_ID, ITEM_PANE_ID).isValid())
        {
            Widget bankPane = context.widgets.get(WIDGET_ID, ITEM_PANE_ID);
            int[] ids = bankPane.getInventory();
            int[] stacks = bankPane.getInventoryStackSizes();
            if (ids != null && stacks != null)
            {
                for (int i = 0; i < ids.length; i++)
                {
                    if(ids[i] == 0 && stacks[i] > 0) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public int getCount() {
        int count = 0;
        for(Item item : getItems()) {
            if(item.getID() != -1) {
                count++;
            }
        }
        return count;
    }

    public Item[] getItems()
    {
        if (isOpen())
        {
            Widget bankPane = context.widgets.get(WIDGET_ID, ITEM_PANE_ID);
            if (bankPane != null)
            {
                int[] ids = bankPane.getInventory();
                int[] stacks = bankPane.getInventoryStackSizes();
                if (ids != null && stacks != null)
                {
                    Item[] items = new Item[ids.length];
                    for (int i = 0; i < ids.length; i++)
                    {
                        Item item = new Item(context, i, bankPane);
                        items[i] = item;
                    }
                    return items;
                }
            }
        }
        return new Item[0];
    }

    public boolean depositAll() {
        for(Item item : context.inventory.getItems())
        {
            if(item == null)
            {
                continue;
            }
            Point p = item.getRandomPoint();
            context.mouse.moveMouse(p);
            context.mouse.leftClick();
            context.sleep(context.random(400, 700));
        }
        return false;
    }

}
