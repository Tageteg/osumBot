package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.GameObject;
import com.osum.bot.scripting.api.Tile;
import com.osum.bot.scripting.api.enums.ObjectType;
import com.osum.bot.scripting.api.util.Filter;

import java.util.ArrayList;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 6:06 AM
 */
public class Objects
{

    private ScriptContext context;

    public Objects(ScriptContext context)
    {
        this.context = context;
    }

    public GameObject getNearest(final int... ids)
    {
        return getNearest(new Filter<GameObject>() {
            public boolean accept(GameObject object) {
                for (int id : ids) {
                    if (object.getId() == id) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public GameObject getNearest(final Filter<GameObject> filter)
    {
        GameObject cur = null;
        double dist = -1;
        for (int x = 0; x < 104; x++)
        {
            for (int y = 0; y < 104; y++)
            {
                GameObject[] objects = getAllAtLocal(x, y);
                for (GameObject object : objects)
                {
                    if (filter.accept(object))
                    {
                        double distTmp = context.players.getLocalPlayer().getPosition().distanceTo(object.getPosition());
                        if (cur == null)
                        {
                            dist = distTmp;
                            cur = object;
                        } else if (distTmp < dist)
                        {
                            cur = object;
                            dist = distTmp;
                        }
                        break;
                    }
                }
            }
        }
        return cur;
    }

    public GameObject[] getAll()
    {
        return getAll(null);
    }

    public GameObject[] getAll(final Filter<GameObject> filter)
    {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        for (int x = 0; x < 104; x++)
        {
            for (int y = 0; y < 104; y++)
            {
                for (GameObject o : getAllAtLocal(x, y))
                {
                    if (filter == null || filter.accept(o))
                    {
                        gameObjects.add(o);
                    }
                }
            }
        }
        if (gameObjects.size() == 0)
        {
            return new GameObject[0];
        }
        return gameObjects.toArray(new GameObject[gameObjects.size()]);
    }

    public GameObject getAt(Tile tile)
    {
        return getAllAt(tile)[0];
    }

    public GameObject[] getAllAt(Tile tile)
    {
        return getAllAtLocal(tile.getX() - context.client.getBaseX(), tile.getY() - context.client.getBaseY());
    }

    public GameObject[] getAllAtLocal(int x, int y)
    {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        com.osum.bot.interfaces.Tile tile = context.client.getCurrentScene().getTiles()[0/*context.client.getPlane()*/][x][y];
        if (tile != null)
        {
            for (com.osum.bot.interfaces.InteractableObject object : tile.getObjects())
            {
                if (object == null)
                {
                    continue;
                }
                GameObject gameObject = new GameObject(context, object, ObjectType.INTERACTABLE, new Tile(x, y, 0/*context.client.getPlane()*/));
                if (gameObject.getId() > 0)
                {
                    gameObjects.add(gameObject);
                }
            }

            if (tile.getWall() != null)
            {
                GameObject gameObject = new GameObject(context, tile.getWall(), ObjectType.WALL, new Tile(x, y, 0/*context.client.getPlane()*/));
                if (gameObject.getId() > 0)
                {
                    gameObjects.add(gameObject);
                }
            }

            if (tile.getWallDecoration() != null)
            {
                GameObject gameObject = new GameObject(context, tile.getWallDecoration(), ObjectType.WALL_DECORATION, new Tile(x, y, 0/*context.client.getPlane()*/));
                if (gameObject.getId() > 0)
                {
                    gameObjects.add(gameObject);
                }
            }

            if (tile.getFloorDecoration() != null)
            {
                GameObject gameObject = new GameObject(context, tile.getFloorDecoration(), ObjectType.FLOOR_DECORATION, new Tile(x, y, 0/*context.client.getPlane()*/));
                if (gameObject.getId() > 0)
                {
                    gameObjects.add(gameObject);
                }
            }

        }
        return gameObjects.toArray(new GameObject[gameObjects.size()]);
    }

}
