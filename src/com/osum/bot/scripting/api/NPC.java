package com.osum.bot.scripting.api;

import com.osum.bot.interfaces.NPCDefinition;
import com.osum.bot.scripting.ScriptContext;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 1:45 AM
 */
public class NPC extends Actor
{

    private ScriptContext context;

    public NPC(ScriptContext context, com.osum.bot.interfaces.NPC npc) {
        super(context, npc);
        this.context = context;
        this.npc = npc;
    }

    private final com.osum.bot.interfaces.NPC npc;

    public int getId() {
        return (getNPCDefinition() != null) ? getNPCDefinition().getId() : 0;
    }

    public String getName() {
        return (getNPCDefinition() != null) ? getNPCDefinition().getName() : "";
    }

    public String getActions()[] {
        return (getNPCDefinition() != null) ? getNPCDefinition().getActions() : new String[0];
    }

    public int getHeadIcon() {
        return (getNPCDefinition() != null) ? getNPCDefinition().getHeadIcon() : 0;
    }

    public boolean isClickable() {
        return (getNPCDefinition() != null) ? getNPCDefinition().isClickable() : true;
    }

    private NPCDefinition getNPCDefinition() {
        return npc.getNPCDefinition();
    }

    public void click()
    {
        context.mouse.moveMouse(getPoint());
    }

    public void interact(String action)
    {
        context.mouse.moveMouse(getPoint());
        context.mouse.rightClick();
        context.sleep(context.random(100, 250));
        if(context.menu.isMenuOpen())
        {
            context.menu.click(action);
        }
    }


}
