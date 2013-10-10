package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 8:21 AM
 */
public interface WallDecoration
{

    Renderable getRenderable();

    int getId();

    int getFlags();

    int getX();

    int getY();

}
