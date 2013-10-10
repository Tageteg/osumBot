package com.osum.bot.scripting.api.methods;

import com.osum.bot.interfaces.Node;
import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.GroundItem;
import com.osum.bot.scripting.api.NodeDeque;
import com.osum.bot.scripting.api.Tile;

import java.util.ArrayList;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 5:44 AM
 */
public class GroundItems
{

    private ScriptContext context;

    public GroundItems(ScriptContext context) {
        this.context = context;
    }

    public GroundItem[] getAll() {
        ArrayList<GroundItem> items = new ArrayList<>();
        com.osum.bot.interfaces.NodeDeque[][] deques = context.client.getGroundItems()[context.client.getPlane()];
        for(int x = 0; x < deques.length; x++) {
            for(int y = 0; y < deques[x].length; y++) {
                com.osum.bot.interfaces.NodeDeque deq = deques[x][y];
                if(deq == null) {
                    continue;
                }
                NodeDeque<Node> deque = new NodeDeque<Node>(deq);
                for (Node node = deque.front(); node != null; node = deque.next()) {
                    com.osum.bot.interfaces.Item i = (com.osum.bot.interfaces.Item) node.getPrevious();
                    GroundItem groundItem = new GroundItem(context, i.getId(), i.getAmount(), new Tile(context.client.getBaseX() + x, context.client.getBaseY() + y, context.client.getPlane()));
                    items.add(groundItem);
                }
            }
        }
        return items.toArray(new GroundItem[items.size()]);
    }

}
