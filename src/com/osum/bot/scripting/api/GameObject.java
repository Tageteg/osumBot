package com.osum.bot.scripting.api;

import com.osum.bot.interfaces.InteractableObject;
import com.osum.bot.interfaces.Renderable;
import com.osum.bot.interfaces.Wall;
import com.osum.bot.interfaces.WallDecoration;
import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.enums.ObjectType;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 6:06 AM
 */
public class GameObject
{

    private ScriptContext context;

    private Object object;

    private ObjectType type;

    private Tile tile;

    private int id;

    public GameObject(ScriptContext context, Object object, ObjectType type, Tile tile)
    {
        this.context = context;
        this.object = object;
        this.type = type;
        this.tile = new Tile(tile.getX() + context.client.getBaseX(), tile.getY() + context.client.getBaseY());
        if (type == ObjectType.INTERACTABLE)
        {
            this.id = (((InteractableObject) object).getId() >> 14) & 0x7FFF;
        }
        if (type == ObjectType.WALL_DECORATION)
        {
            this.id = (((WallDecoration) object).getId() >> 14) & 0x7FFF;
        }
        if (type == ObjectType.WALL)
        {
            this.id = (((Wall) object).getId() >> 14) & 0x7FFF;
        }
    }

    public void click()
    {
        Point p = getModel().getRandomPoint();
        context.mouse.moveMouse(p);
        if (context.mouse.getPosition().equals(p))
        {
            context.mouse.leftClick();
        }
    }

    public void interact(String action)
    {
        context.mouse.moveMouse(getModel().getRandomPoint());
        context.mouse.rightClick();
        context.sleep(context.random(100, 250));
        if(context.menu.isMenuOpen())
        {
            context.menu.click(action);
        }
    }

    public Tile getPosition()
    {
        return tile;
    }

    public Model getModel()
    {
        if (type == ObjectType.INTERACTABLE)
        {
            InteractableObject interactableObject = (InteractableObject) object;
            Renderable renderable = interactableObject.getRenderable();
            if (renderable instanceof com.osum.bot.interfaces.Model)
            {
                return new Model(context, (com.osum.bot.interfaces.Model) renderable, interactableObject.getX(), interactableObject.getY());
            }
        }
        if (type == ObjectType.WALL)
        {
            Wall wall = (Wall) object;
            Renderable renderable = wall.getRenderable();
            if (renderable instanceof com.osum.bot.interfaces.Model)
            {
                return new Model(context, (com.osum.bot.interfaces.Model) renderable, wall.getX(), wall.getY());
            }
        }
        if (type == ObjectType.WALL_DECORATION)
        {
            WallDecoration wallDecoration = (WallDecoration) object;
            Renderable renderable = wallDecoration.getRenderable();
            if (renderable instanceof com.osum.bot.interfaces.Model)
            {
                return new Model(context, (com.osum.bot.interfaces.Model) renderable, wallDecoration.getX(), wallDecoration.getY());
            }
        }
        if (type == ObjectType.FLOOR_DECORATION)
        {
            // not supported
        }
        return null;
    }

    public ObjectType getType()
    {
        return type;
    }

    public int getId()
    {
        return id;
    }

}
