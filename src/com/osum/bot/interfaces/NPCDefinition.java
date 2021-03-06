package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:16 PM
 */
public interface NPCDefinition extends CacheableNode {

    String getName();

    String[] getActions();

    int getHeadIcon();

    int getId();

    boolean isClickable();

}
