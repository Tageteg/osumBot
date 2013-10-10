package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Player;
import com.osum.bot.scripting.api.util.Filter;

import java.util.ArrayList;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 1:22 AM
 */
public class Players
{

    private ScriptContext context;

    public Players(ScriptContext context)
    {
        this.context = context;
    }

    public Player getLocalPlayer()
    {
        return new Player(context, context.client.getLocalPlayer());
    }

    public Player[] getAll()
    {
        return getAll(null);
    }

    public Player[] getAll(Filter<Player> filter)
    {
        com.osum.bot.interfaces.Player[] playerArray = context.client.getPlayerArray();
        ArrayList<Player> players = new ArrayList<>();
        for (com.osum.bot.interfaces.Player _player : playerArray)
        {
            if (_player != null)
            {
                Player player = new Player(context, _player);
                if (filter == null || filter.accept(player))
                {
                    players.add(new Player(context, _player));
                }
            }
        }
        return players.toArray(new Player[players.size()]);
    }

}
