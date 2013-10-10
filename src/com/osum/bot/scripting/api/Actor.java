package com.osum.bot.scripting.api;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.enums.Orientation;

import java.awt.*;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 1:17 AM
 */
public class Actor
{

    private ScriptContext context;

    public Actor(ScriptContext context, com.osum.bot.interfaces.Actor actor)
    {
        this.context = context;
        this.actor = actor;
    }

    private final com.osum.bot.interfaces.Actor actor;

    public Tile getPosition() {
        if(actor == null) {
            return new Tile(-1, -1);
        }
        int x = context.client.getBaseX() + (actor.getX() >> 7);
        int y = context.client.getBaseY() + (actor.getY() >> 7);
        int z = 0; //context.client.getPlane();
        return new Tile(x, y, z);
    }

    public Point getPoint() {
        return context.camera.worldToScreen(actor.getX(), actor.getY(), (actor.getHeight() / 2));
    }

    public int getRealX() {
        return actor.getX();
    }

    public int getRealY() {
        return actor.getY();
    }

    public boolean isMoving() {
        return (actor.getWalkingQueueXPos() != 0) || (actor.getWalkingQueueYPos() != 0);
    }

    public int[] getWalkingQueueX() {
        return actor.getWalkingQueueX();
    }

    public int[] getWalkingQueueY() {
        return actor.getWalkingQueueY();
    }

    public int getAnimation() {
        return actor.getAnimation();
    }

    public String getTextSpoken() {
        return actor.getTextSpoken();
    }

    public Actor getInteracting()
    {
        int index = actor.getInteracting();
        if (index == -1)
        {
            return null;
        }
        if (index < 32768)
        {
            return new NPC(context, context.client.getNPCArray()[index]);
        }
        return new Player(context, context.client.getPlayerArray()[index - 32768]);
    }

    public Orientation getOrientation()
    {
        int num = actor.getOrientation() / 256;
        for (Orientation o : Orientation.values())
        {
            if (o.num == num)
            {
                return o;
            }
        }
        return Orientation.SOUTH;
    }

}
