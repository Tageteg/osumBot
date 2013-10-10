package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:08 PM
 */
public interface Actor extends Renderable {

    String getTextSpoken();

    int getX();

    int getY();

    int[] getWalkingQueueX();

    int[] getWalkingQueueY();

    int getWalkingQueueXPos();

    int getWalkingQueueYPos();

    int getOrientation();

    int getInteracting();

    int getAnimation();

    int getWalkAnimation();

    int getStandAnimation();

}
