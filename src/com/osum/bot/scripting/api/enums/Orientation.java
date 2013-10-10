package com.osum.bot.scripting.api.enums;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 2:54 AM
 */
public enum Orientation
{

    SOUTH(0), SOUTHWEST(1), WEST(2), NORTHWEST(3), NORTH(4), NORTHEAST(5), EAST(6), SOUTHEAST(7);

    public final int num;

    Orientation(int num)
    {
        this.num = num;
    }

}
