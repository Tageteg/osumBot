package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:16 PM
 */
public interface Player extends Actor {

    PlayerDefinition getDefinition();

    Model getModel();

    String getName();

    boolean isSpotAnimating();

    int getCombatLevel();

    int getTotalLevel();

    int getHeadIcon();

    int getSkullIcon();

}
