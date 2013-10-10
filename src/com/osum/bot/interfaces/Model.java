package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:12 PM
 */
public interface Model extends Renderable {

    int[] getXVertices();

    int[] getYVertices();

    int[] getZVertices();

    int[] getXTriangles();

    int[] getYTriangles();

    int[] getZTriangles();

}
