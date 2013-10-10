package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:15 PM
 */
public interface Widget extends Node {

    Widget[] getComponents();

    int[] getInventory();

    int[] getInventoryStackSizes();

    Widget getRoot();

    int getId();

    int getModelId();

    int getParentId();

    int getActionType();

    int getContentType();

    int getX();

    int getY();

    int getRelativeX();

    int getRelativeY();

    int getWidth();

    int getHeight();

    int getBoundsArrayIndex();

    String getText();

    String getTooltip();

    String getSpellName();

    String getSelectedAction();

}
