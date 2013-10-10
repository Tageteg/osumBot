package com.osum.bot.test;

import com.osum.bot.BotWindow;
import com.osum.bot.debugger.impl.DebugGameState;
import com.osum.bot.debugger.impl.DebugMouseCursor;
import com.osum.bot.debugger.impl.DebugObjects;
import com.osum.bot.debugger.impl.DebugPosition;
import com.osum.bot.scripting.Script;
import com.osum.bot.scripting.ScriptManifest;
import com.osum.bot.scripting.api.*;
import com.osum.bot.scripting.api.enums.Orientation;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/4/13
 * Time: 4:25 AM
 */
@ScriptManifest(name = "Sorceress Garden - Summer", description = "", version = 1.0)

public class SorcGardenSummer extends Script
{

    private final int CLOSED_DOOR_ID = 1530;
    private final int APPRENTICE_ID = 5532;
    private final int FOUNTAIN_ID = 21764;
    private final int GATE_ID = 21687;
    private final int TREE_ID = 21767;
    private final int SQIRK_ID = 10845;
    private final int BANK_ID = 2693;
    private final int HERB_ID = 21669;

    private final int[] ELEMENTAL_IDS = new int[]{5547, 5548, 5549, 5550, 5551, 5552};

    private final Path bankToApprentice = new Path(this,
            new Tile[]{
                    new Tile(3308, 3120), new Tile(3303, 3130), new Tile(3309, 3140),
                    new Tile(3321, 3142)
            }
    );

    private long startTime;
    private int herbCount = 0;
    private int inventoryCount = 0;
    private int mazeStep = 0;
    private int runs = 0;

    private enum Status
    {
        BANKING, WALKING_APPRENTICE, OPENING_DOOR_IN, TELEPORTING_APPRENTICE,
        OPENING_GATE, COMPLETING_MAZE, LEAVING_GARDEN, OPENING_DOOR_OUT,
        WALKING_BANK
    }

    @Override
    public boolean init()
    {
        BotWindow.getDebugManager().addDebugger(new DebugGameState(this));
        BotWindow.getDebugManager().addDebugger(new DebugPosition(this));
        BotWindow.getDebugManager().addDebugger(new DebugMouseCursor(this));
        BotWindow.getDebugManager().addDebugger(new DebugObjects(this));
        mouse.setInputBlocking(false);
        keyboard.setInputBlocking(false);
        inventoryCount = inventory.getCount();
        startTime = System.currentTimeMillis();
        return true;
    }

    private Status status = Status.OPENING_GATE;

    @Override
    protected int execute()
    {
        try
        {
            if (!game.isLoggedIn())
            {
                return 0;
            }
            int[] test = client.getSkillMaxLevels();
            switch (status)
            {
                case OPENING_GATE:
                    if (runs > 4)
                    {
                        sleep(60000);
                        runs = 0;
                    }
                    if (!inCenter())
                    {
                        status = Status.COMPLETING_MAZE;
                        mazeStep = 0;
                        sleep(500);
                        return 0;
                    }
                    GameObject gate = objects.getNearest(GATE_ID);
                    if (gate != null)
                    {
                        if (distanceTo(gate.getPosition()) > 5)
                        {
                            if (!players.getLocalPlayer().isMoving())
                            {
                                walking.walkMinimap(new Tile(2911, 5477), false);
                            }
                        } else
                        {
                            gate.click();
                            sleep(1500);
                            return 0;
                        }
                    } else
                    {
                        return -1;
                    }
                    break;
                case COMPLETING_MAZE:
                    if (inCenter())
                    {
                        int currentInventoryCount = inventory.getCount();
                        if (currentInventoryCount != inventoryCount)
                        {
                            herbCount += currentInventoryCount - inventoryCount;
                            inventoryCount = currentInventoryCount;
                        }
                        if (inventory.isFull())
                        {
                            runs /= 2;
                            status = Status.LEAVING_GARDEN;
                            return 0;
                        }
                        status = Status.OPENING_GATE;
                        sleep(1000);
                        return 0;
                    }
                    if (!players.getLocalPlayer().isMoving() && players.getLocalPlayer().getAnimation() == -1)
                    {
                        switch (mazeStep)
                        {
                            case 0:
                                Tile tile = new Tile(2908, 5482);
                                if (isOn(tile))
                                {
                                    mazeStep++;
                                    break;
                                }
                                walking.walkMinimap(tile, false);
                                sleep(2000);
                                break;
                            case 1:
                                tile = new Tile(2906, 5486);
                                if (isOn(tile))
                                {
                                    mazeStep++;
                                    break;
                                }
                                NPC elemental0 = npcs.getNearest(ELEMENTAL_IDS[0]);
                                if (elemental0.getPosition().getY() == 5483)
                                {
                                    walking.walkMinimap(tile, false);
                                    sleep(2000);
                                }
                                break;
                            case 2:
                                tile = new Tile(2906, 5492);
                                if (isOn(tile))
                                {
                                    mazeStep++;
                                    break;
                                }
                                elemental0 = npcs.getNearest(ELEMENTAL_IDS[0]);
                                if (elemental0.getOrientation() == Orientation.SOUTH && elemental0.getPosition().getY() == 5486)
                                {
                                    walking.walkMinimap(tile, false);
                                    sleep(2000);
                                }
                                break;
                            case 3:
                                tile = new Tile(2909, 5490);
                                if (isOn(tile))
                                {
                                    mazeStep++;
                                    runs++;
                                    break;
                                }
                                NPC elemental1 = npcs.getNearest(ELEMENTAL_IDS[1]);
                                if (elemental1.getOrientation() == Orientation.SOUTH && elemental1.getPosition().getY() == 5492)
                                {
                                    walking.walkMinimap(tile, true);
                                    sleep(2000);
                                }
                                break;
                            case 4:
                                tile = new Tile(2911, 5485);
                                if (distanceTo(tile) <= 2)
                                {
                                    mazeStep++;
                                    break;
                                }
                                NPC elemental2 = npcs.getNearest(ELEMENTAL_IDS[2]);
                                if (elemental2.getOrientation() == Orientation.NORTH && elemental2.getPosition().getY() >= 5490 && elemental2.getPosition().getY() <= 5491)
                                {
                                    walking.walkMinimap(tile, true);
                                    sleep(2000);
                                }
                                break;
                            case 5:
                                tile = new Tile(2921, 5485);
                                if (distanceTo(tile) <= 2)
                                {
                                    mazeStep++;
                                    break;
                                }
                                NPC elemental3 = npcs.getNearest(ELEMENTAL_IDS[3]);
                                if (elemental3.getOrientation() == Orientation.SOUTH || elemental3.getPosition().getX() == 2914)
                                {
                                    walking.walkMinimap(tile, true);
                                    sleep(2000);
                                }
                                break;
                            case 6:
                                tile = new Tile(2923, 5484);
                                if (distanceTo(tile) <= 2)
                                {
                                    inventoryCount = inventory.getCount();
                                    mazeStep++;
                                    break;
                                }
                                NPC elemental4 = npcs.getNearest(ELEMENTAL_IDS[4]);
                                if (elemental4.getOrientation() == Orientation.EAST && elemental4.getPosition().getX() <= 2922)
                                {
                                    walking.walkMinimap(tile, false);
                                    sleep(2000);
                                }
                                break;
                            case 7:
                                GameObject herb = objects.getNearest(HERB_ID);
                                herb.click();
                                sleep(6000);
                                return 0;
                        }
                        break;
                    }
                case LEAVING_GARDEN:
                    GameObject fountain = objects.getNearest(FOUNTAIN_ID);
                    if (fountain != null) {
                        if (distanceTo(fountain.getPosition()) > 5) {
                            if(!players.getLocalPlayer().isMoving()) {
                                walking.walkMinimap(new Tile(2912, 5474), false);
                            }
                        } else {
                            //fountain.click();
                            mouse.moveMouse(camera.tileToScreen(fountain.getPosition()));
                            mouse.leftClick();
                            sleep(6500);
                            return 0;
                        }
                    } else {
                        NPC apprentice = npcs.getNearest(APPRENTICE_ID);
                        if(apprentice != null) {
                            status = Status.OPENING_DOOR_OUT;
                            sleep(500);
                            return 0;
                        }
                        return -1;
                    }
                    break;
                case OPENING_DOOR_OUT:
                    GameObject door = objects.getNearest(CLOSED_DOOR_ID);
                    if(door != null) {
                        if(door.getPosition().getY() <= 3143 && door.getPosition().getY() >= 3139) {
                            door.click();
                            sleep(2000);
                            return 0;
                        } else {
                            status = Status.WALKING_BANK;
                        }
                    } else {
                        status = Status.WALKING_BANK;
                    }
                    break;
                case WALKING_BANK:
                    door = objects.getNearest(CLOSED_DOOR_ID);
                    if(door != null) {
                        if(players.getLocalPlayer().getPosition().getY() <= 3141 && players.getLocalPlayer().getPosition().getY() >= 3137
                                && players.getLocalPlayer().getPosition().getX() >= 3318) {
                            status = Status.OPENING_DOOR_IN;
                            sleep(500);
                            return 0;
                        }
                    }
                    Path apprenticeToBank = bankToApprentice.reverse();
                    if(distanceTo(apprenticeToBank.getEnd()) > 4) {
                        apprenticeToBank.traverse(false);
                    } else {
                        status = Status.BANKING;
                    }
                    break;
                case BANKING:
                    if(inventory.getCount() > 0) {
                        if(!bank.isOpen()) {
                            GameObject bank = objects.getNearest(BANK_ID);
                            if(bank != null) {
                                if(widgets.get(211, 2) != null) {
                                    //widgets.get(211, 2).click();
                                    Point p = new Point(random(190, 335), random(443, 453));
                                    mouse.moveMouse(p);
                                    mouse.leftClick();
                                    sleep(1000);
                                    return 0;
                                }
                                bank.click();
                                sleep(1500);
                                return 0;
                            }
                        }
                        bank.depositAll();
                        sleep(1000);
                        return 0;
                    }
                    if(bank.isOpen()) {
                        //bank.close();
                        Point p = new Point(random(190, 335), random(443, 453));
                        mouse.moveMouse(p);
                        mouse.leftClick();
                        sleep(500);
                        return 0;
                    } else {
                        status = Status.WALKING_APPRENTICE;
                    }
                    break;
                case WALKING_APPRENTICE:
                    if(distanceTo(bankToApprentice.getEnd()) > 4) {
                        bankToApprentice.traverse(false);
                    } else {
                        status = Status.OPENING_DOOR_IN;
                    }
                    break;
                case OPENING_DOOR_IN:
                    door = objects.getNearest(CLOSED_DOOR_ID);
                    if(door != null) {
                        if(door.getPosition().getY() <= 3143 && door.getPosition().getY() >= 3139) {
                            door.click();
                            sleep(2000);
                            return 0;
                        } else {
                            status = Status.TELEPORTING_APPRENTICE;
                        }
                    } else {
                        status = Status.TELEPORTING_APPRENTICE;
                    }
                    break;
                case TELEPORTING_APPRENTICE:
                    door = objects.getNearest(CLOSED_DOOR_ID);
                    if(door != null) {
                        if(door.getPosition().getY() <= 3143 && door.getPosition().getY() >= 3139) {
                            status = Status.OPENING_DOOR_IN;
                            sleep(500);
                            return 0;
                        }
                    }
                    NPC apprentice = npcs.getNearest(APPRENTICE_ID);
                    if(apprentice != null) {
                        if(players.getLocalPlayer().getPosition().getY() >= 3142) {
                            walking.walkMinimap(new Tile(3321, 3139), false);
                            sleep(500);
                            return 2500;
                        }
                        apprentice.interact("Teleport");
                        sleep(3000);
                        return 0;
                    } else {
                        if(inCenter()) {
                            status = Status.OPENING_GATE;
                            sleep(500);
                            return 0;
                        }
                        return -1;
                    }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public double distanceTo(Tile tile)
    {
        return players.getLocalPlayer().getPosition().distanceTo(tile);
    }

    public boolean isOn(Tile tile)
    {
        return players.getLocalPlayer().getPosition().getX() == tile.getX() && players.getLocalPlayer().getPosition().getY() == tile.getY();
    }

    public boolean inCenter()
    {
        int x = players.getLocalPlayer().getPosition().getX();
        int y = players.getLocalPlayer().getPosition().getY();
        if (x >= 2903 && y >= 5463 && x <= 2920 && y <= 5480)
        {
            return true;
        }
        return false;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        /*
        if (game.isLoggedIn())
        {
            g.setColor(new Color(50, 50, 50, 200));
            g.fillRect(7, 345, 250, 85);
            g.fillRect(320, 345, 195, 125);
            g.setColor(Color.WHITE);
            double timeDiff = (double) (System.currentTimeMillis() - startTime);
            timeDiff /= 1000;
            timeDiff /= 60;
            timeDiff /= 60;
            final double herbsHr = herbCount / timeDiff;
            final DecimalFormat df = new DecimalFormat("#.###");
            g.drawString("Marty's SummerGarden Pwner 2000", 13, 360);
            g.drawString(df.format(timeDiff) + " hrs", 13, 375);
            g.drawString("[" + herbCount + "] " + df.format(herbsHr) + " herbs/hr", 13, 390);
            g.drawString("Status: " + status, 13, 405);
            g.drawString("Step: " + mazeStep, 13, 420);
            final ArrayList<Polygon> polygons = new ArrayList<>();
            for (int i = 0; i < ELEMENTAL_IDS.length; i++)
            {
                NPC elemental = npcs.getNearest(ELEMENTAL_IDS[i]);
                if (elemental != null)
                {
                    g.setColor(Color.WHITE);
                    g.drawString(i + ": " + elemental.getPosition() + " - " + elemental.getOrientation(), 326, 360 + (i * 15));
                    if (camera.pointOnScreen(elemental.getPoint()))
                    {
                        g.setColor(Color.BLUE);
                        g.drawString("[" + i + "]" + elemental.getOrientation(), (int) elemental.getPoint().getX() - 10, (int) elemental.getPoint().getY() - 10);
                        g.drawString(elemental.getPosition().getX() + ", " + elemental.getPosition().getY(), (int) elemental.getPoint().getX() - 30, (int) elemental.getPoint().getY() - 30);
                        polygons.add(camera.getTileBounds(elemental.getPosition()));
                    }
                    final Point point = camera.tileToMinimap(elemental.getPosition());
                    point.x -= 5;
                    final Polygon polygon = new Polygon();
                    polygon.addPoint((int) point.getX(), (int) point.getY());
                    polygon.addPoint((int) point.getX() + 5, (int) point.getY());
                    polygon.addPoint((int) point.getX() + 5, (int) point.getY() + 5);
                    polygon.addPoint((int) point.getX(), (int) point.getY() + 5);
                    polygons.add(polygon);
                }
            }
            for (final Polygon p : polygons)
            {
                g.setColor(Color.BLUE);
                g.drawPolygon(p);
                g.setColor(new Color(0, 0, 255, 50));
                g.fillPolygon(p);
            }
        }
        */
    }

}
