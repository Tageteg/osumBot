package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Item;
import com.osum.bot.scripting.api.Widget;

import java.util.LinkedList;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 11:20 AM
 */
public class Inventory
{

    private ScriptContext context;

    public static final int INTERFACE_INVENTORY = 149;

    public static final int INTERFACE_INVENTORY_BANK = 763;

    public static final int INTERFACE_INVENTORY_SHOP = 621;

    public static final int CAPACITY = 28;

    public Inventory(ScriptContext context)
    {
        this.context = context;
    }

    public Widget getWidget()
    {
        if (context.widgets.get(INTERFACE_INVENTORY_BANK) != null)
        {
            Widget bankInv = context.widgets.get(INTERFACE_INVENTORY_BANK, 0);
            if (bankInv != null)
            {
                return bankInv;
            }
        }
        if (context.widgets.get(INTERFACE_INVENTORY_SHOP) != null)
        {
            Widget shopInv = context.widgets.get(INTERFACE_INVENTORY_SHOP, 0);
            if (shopInv != null)
            {
                return shopInv;
            }
        }

        /*
        if (methods.game.getCurrentTab() != Game.TAB_INVENTORY) {
            methods.game.openTab(Game.TAB_INVENTORY);
            sleep(random(400, 900));
        }
        */

        return context.widgets.get(INTERFACE_INVENTORY, 0);
    }

    public int getCount() {
        int count = 0;
        for(Item item : getItems()) {
            if(item == null)
            {
                continue;
            }
            if(item.getID() != -1) {
                count++;
            }
        }
        return count;
    }

    public Item[] getItems()
    {
        Widget inventory = getWidget();
        if (inventory != null)
        {
            int[] ids = inventory.getInventory();
            int[] stacks = inventory.getInventoryStackSizes();
            if (ids != null && stacks != null)
            {
                Item[] items = new Item[ids.length];
                for (int i = 0; i < ids.length; i++)
                {
                    if (ids[i] != 0)
                    {
                        Item item = new Item(context, i, inventory);
                        items[i] = item;
                    }
                }
                return items;
            }
        }
        return new Item[0];
    }

    public Item[] getItems(final int... ids)
    {
        LinkedList<Item> items = new LinkedList<Item>();
        for (Item item : getItems())
        {
            for (int i : ids)
            {
                if (item.getID() == i)
                {
                    items.add(item);
                    break;
                }
            }
        }
        return items.toArray(new Item[items.size()]);
    }

    public Item getItemAt(int slot)
    {
        Item[] items = getItems();
        if (items != null)
        {
            return items[slot];
        } else
        {
            return null;
        }
    }

    public Item getItem(final int... ids)
    {
        Item[] items = getItems();
        for (Item item : items)
        {
            for (int id : ids)
            {
                if (item.getID() == id)
                {
                    return item;
                }
            }
        }
        return null;
    }

    public boolean contains(final int id)
    {
        return getItem(id) != null;
    }

    public boolean containsAll(final int... itemID)
    {
        for (int i : itemID)
        {
            if (getItem(i) == null)
            {
                return false;
            }
        }
        return true;
    }

    public boolean containsOneOf(final int... itemID)
    {
        Item[] items = getItems();
        for (Item item : items)
        {
            for (int i : itemID)
            {
                if (item.getID() == i)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFull()
    {
        return getCount() == 28;
    }

}
