package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.Widget;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 11:13 PM
 */
public class Widgets
{

    private ScriptContext context;

    public Widgets(ScriptContext context) {
        this.context = context;
    }

    public Widget[] get(int group) {
        if (context.client.getWidgets() == null || group >= context.client.getWidgets().length
                || context.client.getWidgets()[group] == null) return new Widget[0];
        com.osum.bot.interfaces.Widget[] widgets = context.client.getWidgets()[group];
        Widget[] valid = new Widget[widgets.length];

        for (int j = 0; j < widgets.length; j++) {
            if (widgets[j] == null) continue;

            Widget w = new Widget(context, context.client.getWidgets()[group][j]);
            valid[j] = w;
        }

        return valid;
    }

    public Widget get(int group, int child) {
        Widget[] widgets = get(group);
        if (widgets != null && child < widgets.length && widgets[child] != null) {
            return widgets[child];
        }
        return null;
    }

}
