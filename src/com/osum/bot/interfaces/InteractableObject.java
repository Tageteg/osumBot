package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:13 PM
 */
public interface InteractableObject {

    Renderable getRenderable();

    int getId();

    int getFlags();

    int getX();

    int getY();

}
