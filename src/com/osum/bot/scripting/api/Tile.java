package com.osum.bot.scripting.api;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 4:56 AM
 */
public class Tile
{
    private int x;
    private int y;
    private int z;

    public Tile(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public Tile(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public String toString(){
        return "[Tile: x: "+x+" y:"+y+"]";
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object instanceof Tile)
        {
            Tile tile = (Tile) object;
            return (tile.x == x) && (tile.y == y) && (tile.z == z);
        }
        return false;
    }

    public double distanceTo(Tile tile)
    {
        return Math.sqrt((x - tile.getX()) * (x - tile.getX()) + (y - tile.getY()) * (y - tile.getY()));
    }

}
