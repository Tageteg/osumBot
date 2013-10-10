package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.NPC;
import com.osum.bot.scripting.api.Tile;
import com.osum.bot.scripting.api.util.Filter;

import java.util.ArrayList;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 1:22 AM
 */
public class NPCs
{

    private ScriptContext context;

    public NPCs(ScriptContext context)
    {
        this.context = context;
    }

    public NPC getNearest(final int... ids)
    {
        return getNearest(new Filter<NPC>()
        {
            public boolean accept(NPC npc)
            {
                for (int id : ids)
                {
                    if (npc.getId() == id)
                    {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public NPC getNearest(final Filter<NPC> filter)
    {
        Tile tile = context.players.getLocalPlayer().getPosition();
        NPC closest = null;
        com.osum.bot.interfaces.NPC[] npcs = context.client.getNPCArray();
        for (com.osum.bot.interfaces.NPC _npc : npcs)
        {
            if (_npc != null)
            {
                NPC npc = new NPC(context, _npc);
                if (closest == null || tile.distanceTo(npc.getPosition()) < tile.distanceTo(closest.getPosition()))
                {
                    if (filter == null || filter.accept(npc))
                    {
                        closest = npc;
                    }
                }
            }
        }
        return closest;
    }

    public NPC[] getAll()
    {
        return getAll(null);
    }

    public NPC[] getAll(final Filter<NPC> filter)
    {
        com.osum.bot.interfaces.NPC[] npcArray = context.client.getNPCArray();
        ArrayList<NPC> npcs = new ArrayList<>();
        for (com.osum.bot.interfaces.NPC _npc : npcArray)
        {
            if (_npc != null)
            {
                NPC npc = new NPC(context, _npc);
                if (filter == null || filter.accept(npc))
                {
                    npcs.add(new NPC(context, _npc));
                }
            }
        }
        return npcs.toArray(new NPC[npcs.size()]);
    }

}
