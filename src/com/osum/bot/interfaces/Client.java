package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:12 PM
 */
public interface Client {

    NPC[] getNPCArray();

    Player[] getPlayerArray();

    Player getLocalPlayer();

    Scene getCurrentScene();

    int getGameState();

    int getCameraX();

    int getCameraZ();

    int getCameraY();

    int getCameraPitch();

    int getCameraYaw();

    int getCompassAngle();

    int getMapOffset();

    int getMapScale();

    int getBaseX();

    int getBaseY();

    int getPlane();

    int getWalkingDestX();

    int getWalkingDestY();

    int[] getWidgetBoundsXArray();

    int[] getWidgetBoundsYArray();

    int[][][] getTileHeightMap();

    byte[][][] getSceneFlags();

    Widget[][] getWidgets();

    NodeDeque[][][] getGroundItems();

    Mouse getMouse();

    Keyboard getKeyboard();

    int[] getSkillLevels();

    int[] getSkillMaxLevels();

    int[] getSkillExperiences();

    String[] getChatNames();

    String[] getChatMessages();

    String[] getMenuActions();

    String[] getMenuTargets();

    boolean isMenuOpen();

    int getMenuX();

    int getMenuY();

    int getMenuWidth();

    int getMenuHeight();

    int getMenuSize();

}
