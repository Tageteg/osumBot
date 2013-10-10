package com.osum.bot.scripting.api;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.util.Filter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 2:26 AM
 */
public class Widget
{

    private ScriptContext context;
    private com.osum.bot.interfaces.Widget widget;

    public Widget(final ScriptContext context, com.osum.bot.interfaces.Widget widget)
    {
        this.context = context;
        this.widget = widget;
    }

    public int getModelId()
    {
        return widget.getModelId();
    }

    public String getSelectedAction()
    {
        return widget.getSelectedAction();
    }

    public String getText()
    {
        return widget.getText();
    }

    public String getSpellName()
    {
        return widget.getSpellName();
    }

    public Widget getChild(final int id)
    {
        if (widget.getComponents() == null)
        {
            return null;
        }

        return new Widget(context, widget.getComponents()[id]);
    }

    public List<Widget> getChildren()
    {
        return getChildren(null);
    }

    public List<Widget> getChildren(Filter<Widget> filter)
    {
        List<Widget> children = new ArrayList<Widget>();

        if (widget.getComponents() == null || widget.getComponents().length == 0)
        {
            return children;
        }

        for (int i = 0; i < widget.getComponents().length; i++)
        {
            if (widget.getComponents()[i] != null)
            {
                Widget w = new Widget(context, widget.getComponents()[i]);
                if (filter == null || filter.accept(w))
                {
                    children.add(w);
                }
            }
        }
        return children;
    }

    public int getChildCount()
    {
        if (widget.getComponents() == null)
        {
            return 0;
        } else
        {
            return widget.getComponents().length;
        }
    }

    public int getId()
    {
        return widget.getId() & 0xFFFF;
    }

    public int getParentId()
    {
        return widget.getId() >> 16;
    }

    public int getParentIndex()
    {
        return widget.getId() << 16;
    }

    public int getIndex()
    {
        return widget.getId() & 0xFFFF;
    }

    public int getX()
    {
        if (widget == null)
        {
            return -1;
        }

        Widget parent = getParent();
        int x = 0;
        if (parent != null)
        {
            x = parent.getX();
        } else
        {
            int[] posx = context.client.getWidgetBoundsXArray();
            if (widget.getBoundsArrayIndex() != -1 && posx[widget.getBoundsArrayIndex()] > 0)
            {
                return (posx[widget.getBoundsArrayIndex()] + widget.getX());
            }
        }
        return (widget.getX() + x);
    }

    public int getY()
    {
        if (widget == null)
        {
            return -1;
        }

        Widget parent = getParent();
        int y = 0;
        if (parent != null)
        {
            y = parent.getY();
        } else
        {
            int[] posy = context.client.getWidgetBoundsYArray();
            if (widget.getBoundsArrayIndex() != -1 && posy[widget.getBoundsArrayIndex()] > 0)
            {
                return (posy[widget.getBoundsArrayIndex()] + widget.getY());
            }
        }
        return (widget.getY() + y);
    }

    public int getRelativeX()
    {
        return widget.getX();
    }

    public int getRelativeY()
    {
        return widget.getY();
    }

    public int getWidth()
    {
        return widget.getWidth();
    }

    public int getHeight()
    {
        return widget.getHeight();
    }

    public Rectangle getBounds()
    {
        return new Rectangle(getX(), getY(), widget.getWidth(), widget.getHeight());
    }

    public boolean hasParent()
    {
        return getParentId() >= 0 && getParentId() < context.client.getWidgets().length;
    }

    public Widget getParent()
    {
        if (widget == null)
        {
            return null;
        }
        int uid = getParentId();
        if (uid == -1)
        {
            // WidgetNode stuff
        }
        if (uid == -1)
        {
            return null;
        }
        int parent = uid >> 16;
        int child = uid & 0xffff;
        return null;
    }

    public Widget getRoot()
    {
        return new Widget(context, widget.getRoot());
    }

    public String getTooltip()
    {
        return widget.getTooltip();
    }

    public int[] getInventory()
    {
        return widget.getInventory();
    }

    public int[] getInventoryStackSizes()
    {
        return widget.getInventoryStackSizes();
    }

    public boolean isValid()
    {
        return widget != null;
    }

}