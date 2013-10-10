package com.osum.bot.debugger.impl;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Tile;
import com.osum.bot.debugger.Debugger;

import java.awt.*;

public class DebugTiles extends Debugger
{

    public DebugTiles(ScriptContext context)
    {
        super(context);
    }

    @Override
    public void draw(Graphics g, Point p)
    {
        if (context.game.isLoggedIn())
        {
            final int baseX = context.client.getBaseX();
            final int baseY = context.client.getBaseY();
            final Point[][] minimapPoints = new Point[105][105];
            final Point[][] screenPoints = new Point[105][105];
            for (int i = 0; i < screenPoints.length; i++)
            {
                for (int j = 0; j < screenPoints[i].length; j++)
                {
                    final int x = i + baseX - 1;
                    final int y = j + baseY - 1;
                    Point mini = context.camera.worldToMinimap(x, y);
                    if ((mini.x == -1) || (mini.y == -1))
                    {
                        mini = null;
                    }
                    minimapPoints[i][j] = mini;
                    Point screen = context.camera.tileToScreen(new Tile(x, y), 0, 0, 0);
                    if ((screen.x == -1) || (screen.y == -1))
                    {
                        screen = null;
                    }
                    screenPoints[i][j] = screen;
                }
            }

            for (int i = 1; i < 104; i++)
            {
                for (int j = 1; j < 104; j++)
                {
                    final Point miniBL = minimapPoints[i][j];
                    final Point miniBR = minimapPoints[i][j + 1];
                    final Point miniTL = minimapPoints[i + 1][j];
                    final Point miniTR = minimapPoints[i + 1][j + 1];
                    final Point bl = screenPoints[i][j];
                    final Point br = screenPoints[i][j + 1];
                    final Point tl = screenPoints[i + 1][j];
                    final Point tr = screenPoints[i + 1][j + 1];

                    g.setColor(new Color(0, 0, 255, 50));
                    if ((tl != null) && (br != null) && (tr != null) && (bl != null))
                    {

                        g.fillPolygon(new int[]{bl.x, br.x, tr.x, tl.x}, new int[]{bl.y, br.y, tr.y, tl.y}, 4);
                    }
                    if ((miniBL != null) && (miniBR != null) && (miniTR != null) && (miniTL != null))
                    {
                        g.fillPolygon(new int[]{miniBL.x, miniBR.x, miniTR.x, miniTL.x},
                                new int[]{miniBL.y, miniBR.y, miniTR.y, miniTL.y}, 4);
                    }


                    g.setColor(Color.BLUE);
                    if ((tl != null) && (bl != null))
                    {
                        g.drawLine(bl.x, bl.y, tl.x, tl.y);
                    }
                    if ((miniBL != null) && (miniTL != null))
                    {
                        g.drawLine(miniBL.x, miniBL.y, miniTL.x, miniTL.y);
                    }


                    g.setColor(Color.BLUE);
                    if ((br != null) && (bl != null))
                    {
                        g.drawLine(bl.x, bl.y, br.x, br.y);
                    }
                    if ((miniBR != null) && (miniBL != null))
                    {
                        g.drawLine(miniBL.x, miniBL.y, miniBR.x, miniBR.y);
                    }
                }
            }
        }

        final Point mini = context.camera.tileToMinimap(context.players.getLocalPlayer().getPosition());
        g.setColor(Color.BLUE);
        g.fillRect((int) mini.getX() - 1, (int) mini.getY() - 1, 2, 2);
    }

}