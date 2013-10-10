package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 8:22 AM
 */
public interface Wall
{

    Renderable getRenderable();

    int getId();

    int getFlags();

    int getX();

    int getY();

}
