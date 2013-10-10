package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:13 PM
 */
public interface GroundItem extends Renderable {

    int getId();

    int getPlane();

    int getX();

    int getY();

    boolean isNonStackable();

    int getDuration();

}
