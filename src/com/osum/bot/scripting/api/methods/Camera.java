package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Tile;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 3:08 AM
 */
public class Camera
{

    public static final int[] SIN_TABLE = new int[2048];
    public static final int[] COS_TABLE = new int[2048];

    static
    {
        for (int i = 0; i < SIN_TABLE.length; i++)
        {
            SIN_TABLE[i] = (int) (65536.0D * Math.sin((double) i * 0.0030679615D));
            COS_TABLE[i] = (int) (65536.0D * Math.cos((double) i * 0.0030679615D));
        }
    }

    private ScriptContext context;

    public Camera(ScriptContext context)
    {
        this.context = context;
    }

    public boolean pointOnScreen(Point check)
    {
        int x = check.x, y = check.y;
        return x > 4 && x < context.game.getWidth() - 253 && y > 4 && y < context.game.getHeight() - 169;
    }

    public final Point worldToScreen(int x, int y, int height)
    {

        int z = tileHeight(x, y) - height;
        x -= context.client.getCameraX();
        z -= context.client.getCameraZ();
        y -= context.client.getCameraY();

        int pitchSin = SIN_TABLE[context.client.getCameraPitch()];
        int pitchCos = COS_TABLE[context.client.getCameraPitch()];
        int yawSin = SIN_TABLE[context.client.getCameraYaw()];
        int yawCos = COS_TABLE[context.client.getCameraYaw()];

        int angle = y * yawSin + x * yawCos >> 16;

        y = y * yawCos - x * yawSin >> 16;
        x = angle;
        angle = z * pitchCos - y * pitchSin >> 16;
        y = z * pitchSin + y * pitchCos >> 16;
        z = angle;

        if (y >= 50)
        {
            int screenX = (x << 9) / y + 256;
            int screenY = (angle << 9) / y + 167;
            return new Point(screenX, screenY);
        }

        return new Point(-1, -1);
    }

    public Point tileToScreen(Tile tile, double dX, double dY, int height)
    {
        int x = (int) ((tile.getX() - context.client.getBaseX() + dX) * 128);
        int y = (int) ((tile.getY() - context.client.getBaseY() + dY) * 128);
        return worldToScreen(x, y, 0);
    }

    public Point tileToScreen(Tile tile)
    {
        return tileToScreen(tile, 0.5, 0.5, 0);
    }

    public Point tileToScreen(Tile tile, int randX, int randY)
    {
        Point p = tileToScreen(tile, 0.5, 0.5, 0);
        return new Point(p.x + context.random(-randX, randX), p.y + context.random(-randY, randY));
    }

    public int tileHeight(final int x, final int y)
    {
        int xx = x >> 7;
        int yy = y >> 7;
        if (xx < 0 || yy < 0 || xx > 103 || yy > 103)
        {
            return 0;
        }

        int plane = 0; //context.client.getPlane();

        int aa = context.client.getTileHeightMap()[plane][xx][yy] * (128 - (x & 0x7F))
                + context.client.getTileHeightMap()[plane][xx + 1][yy] * (x & 0x7F) >> 7;
        int ab = context.client.getTileHeightMap()[plane][xx][yy + 1]
                * (128 - (x & 0x7F))
                + context.client.getTileHeightMap()[plane][xx + 1][yy + 1]
                * (x & 0x7F) >> 7;
        return aa * (128 - (y & 0x7F)) + ab * (y & 0x7F) >> 7;
    }

    public Point tileToMinimap(Tile tile)
    {
        int x = tile.getX() - context.client.getBaseX();
        int y = tile.getY() - context.client.getBaseY();
        return worldToMinimap((x * 4 + 2) - context.players.getLocalPlayer().getRealX() / 32, (y * 4 + 2) - context.players.getLocalPlayer().getRealY() / 32);
    }

    public boolean tileOnMinimap(Tile tile){
        return context.players.getLocalPlayer().getPosition().distanceTo(tile) < 17;
    }

    public Point worldToMinimap(int regionX, int regionY)
    {
        int angle = context.client.getCompassAngle() + context.client.getMapScale() & 0x7FF;
        int j = regionX * regionX + regionY * regionY;

        if (j > 6400)
            return new Point(-1, -1);

        int sin = SIN_TABLE[angle] * 256 / (context.client.getMapOffset() + 256);
        int cos = COS_TABLE[angle] * 256 / (context.client.getMapOffset() + 256);

        int x = regionY * sin + regionX * cos >> 16;
        int y = regionY * cos - regionX * sin >> 16;

        //return new Point(644 + x, 80 - y);
        return new Point(643 + x, 83 - y);
    }

    public int getX()
    {
        return context.client.getCameraX();
    }

    public int getY()
    {
        return context.client.getCameraY();
    }

    public int getZ()
    {
        return context.client.getCameraZ();
    }

    /**
     * Returns the angle between the current camera angle and the given angle in
     * degrees.
     *
     * @param degrees The target angle.
     * @return The angle between the who angles in degrees.
     */
    public int getAngleTo(int degrees)
    {
        int ca = getAngle();
        if (ca < degrees)
        {
            ca += 360;
        }
        int da = ca - degrees;
        if (da > 180)
        {
            da -= 360;
        }
        return da;
    }

    /**
     * Returns the current compass orientation in degrees, with North at 0,
     * increasing counter-clockwise to 360.
     *
     * @return The current camera angle in degrees.
     */
    public int getAngle()
    {
        double angle = context.client.getCameraYaw();
        angle /= 2048;
        angle *= 360;
        return (int) angle;
    }

    /**
     * Returns the angle to a given tile in degrees anti-clockwise from the
     * positive x axis (where the x-axis is from west to east).
     *
     * @param t The target tile
     * @return The angle in degrees
     */
    public int angleToTile(Tile t)
    {
        Tile me = context.players.getLocalPlayer().getPosition();
        int angle = (int) Math.toDegrees(Math.atan2(t.getY() - me.getY(), t.getX() - me.getX()));
        return angle >= 0 ? angle : 360 + angle;
    }

    /**
     * Returns the camera angle at which the camera would be facing a certain
     * tile.
     *
     * @param t The target tile
     * @return The angle in degrees
     */
    public int getTileAngle(Tile t)
    {
        int a = (angleToTile(t) - 90) % 360;
        return a < 0 ? a + 360 : a;
    }

    /**
     * Returns the current percentage of the maximum pitch of the camera in an
     * open area.
     *
     * @return The current camera altitude percentage.
     */
    public int getPitch()
    {
        return (int) ((context.client.getCameraPitch() - 1024) / 20.48);
    }

    public Polygon getTileBounds(Tile tile)
    {
        Point bl = tileToScreen(tile, 0, 0, 0);
        Point br = tileToScreen(new Tile(tile.getX(), tile.getY() + 1), 0, 0, 0);
        Point tl = tileToScreen(new Tile(tile.getX() + 1, tile.getY()), 0, 0, 0);
        Point tr = tileToScreen(new Tile(tile.getX() + 1, tile.getY() + 1), 0, 0, 0);
        Polygon p = new Polygon();
        p.addPoint(bl.x, bl.y);
        p.addPoint(br.x, br.y);
        p.addPoint(tr.x, tr.y);
        p.addPoint(tl.x, tl.y);
        return p;
    }


}
