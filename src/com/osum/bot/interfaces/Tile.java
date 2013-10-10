package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:15 PM
 */
public interface Tile extends Node {

    int getHeight();

    int getX();

    int getY();

    InteractableObject[] getObjects();

    Wall getWall();

    WallDecoration getWallDecoration();

    FloorDecoration getFloorDecoration();

}
