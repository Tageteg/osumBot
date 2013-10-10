package com.osum.bot.scripting.api;

import com.osum.bot.scripting.ScriptContext;

import java.util.Arrays;

public class Path
{

    private ScriptContext context;

    private final Tile[] tiles;

    private Path reversed;

    public Path(ScriptContext context, final Tile[] tiles)
    {
        this.context = context;
        this.tiles = tiles;
    }

    public Tile getStart()
    {
        return tiles[0];
    }

    public Tile getEnd()
    {
        return tiles[tiles.length - 1];
    }

    public void traverseCompletely()
    {
        traverseCompletely(true);
    }

    public void traverseCompletely(final boolean run)
    {
        Tile next;
        while ((next = next()) != null && !isAtEnd(next))
        {
            traverse(next, run);
        }
    }

    public void traverse()
    {
        traverse(true);
    }

    public void traverse(final boolean run)
    {
        final Tile next = next();
        traverse(next, run);
    }

    private void traverse(final Tile next, final boolean run)
    {
        try
        {
            if (context.game.getGameState() == 30
                    && next != null
                    && !isAtEnd(next)
                    && !context.players.getLocalPlayer().isMoving())
            {
                context.walking.walkMinimap(next, run);
            }
        } catch (Exception e)
        {

        }
    }

    private boolean isAtEnd(Tile next)
    {
        return context.players.getLocalPlayer().getPosition().distanceTo(next) < 3;
    }

    private Tile next()
    {
        try
        {
            for (int i = tiles.length - 1; i >= 0; --i)
            {
                if (context.camera.tileOnMinimap(tiles[i]))
                {
                    return tiles[i];
                }
            }
        } catch (Exception e)
        {
        }
        return null;
    }

    public Path reverse()
    {
        if (reversed == null)
        {
            Tile[] reversedTiles = new Tile[tiles.length];
            for (int i = tiles.length - 1; i >= 0; i--)
            {
                reversedTiles[tiles.length - 1 - i] = tiles[i];
            }
            reversed = new Path(context, reversedTiles);
        }
        return reversed;
    }

    @Override
    public String toString()
    {
        return Arrays.toString(tiles);
    }

}