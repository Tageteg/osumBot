package com.osum.bot.scripting.api;

import com.osum.bot.scripting.ScriptContext;

import java.awt.*;
import java.util.LinkedList;

/**
 * User: Marty
 * Date: 4/3/13
 * Time: 9:09 PM
 */
public class Model
{

    private ScriptContext context;

    private com.osum.bot.interfaces.Model model;

    private int x;

    private int y;

    private int[] xTriangles;

    private int[] yTriangles;

    private int[] zTriangles;

    private int[] xVertices;

    private int[] yVertices;

    private int[] zVertices;

    public Model(ScriptContext context, com.osum.bot.interfaces.Model model, int x, int y)
    {
        this.context = context;
        this.model = model;
        this.x = x;
        this.y = y;
        this.xTriangles = model.getXTriangles();
        this.yTriangles = model.getYTriangles();
        this.zTriangles = model.getZTriangles();
        this.xVertices = model.getXVertices();
        this.yVertices = model.getYVertices();
        this.zVertices = model.getZVertices();
    }

    private boolean contains(Point p)
    {
        if (this == null)
        {
            return false;
        }

        Polygon[] triangles = this.getTriangles();
        for (Polygon poly : triangles)
        {
            if (poly.contains(p))
            {
                return true;
            }
        }

        return false;
    }

    public Point getRandomPoint(){
        Polygon[] triangles = getTriangles();
        for(int i = 0; i < 100; i++){
            Polygon p = triangles[context.random(0, triangles.length)];
            Point point = new Point(p.xpoints[context.random(0, p.xpoints.length)], p.ypoints[context.random(0, p.ypoints.length)]);
            if (context.camera.pointOnScreen(point))
                return point;
        }
        return new Point(-1, -1);
    }

    public Polygon[] getTriangles()
    {
        LinkedList<Polygon> polygons = new LinkedList<>();

        for (int i = 0; i < xTriangles.length; i++)
        {
            Point p1 = context.camera.worldToScreen(x + xVertices[xTriangles[i]], y + zVertices[xTriangles[i]], -yVertices[xTriangles[i]]);
            Point p2 = context.camera.worldToScreen(x + xVertices[yTriangles[i]], y + zVertices[yTriangles[i]], -yVertices[yTriangles[i]]);
            Point p3 = context.camera.worldToScreen(x + xVertices[zTriangles[i]], y + zVertices[zTriangles[i]], -yVertices[zTriangles[i]]);

            if (p1.x >= 0 && p2.x >= 0 && p2.x >= 0)
            {
                polygons.add(new Polygon(new int[]{p1.x, p2.x, p3.x}, new int[]{p1.y, p2.y, p3.y}, 3));
            }
        }
        return polygons.toArray(new Polygon[polygons.size()]);
    }

}
