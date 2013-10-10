package com.osum.bot.scripting.api.methods;

import com.osum.bot.scripting.ScriptContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Marty
 * Date: 4/4/13
 * Time: 3:39 AM
 */
public class Menu
{

    private ScriptContext context;

    public Menu(ScriptContext context)
    {
        this.context = context;
    }

    public int getIndex(String choice) {
        choice = choice.toLowerCase();
        List<String> choices = getChoices();
        for (String s : choices) {
            if (s.toLowerCase().contains(choice.toLowerCase())) {
                return choices.indexOf(s);
            }
        }
        return -1;
    }

    public String getChoice(int index) {
        List<String> choices = getChoices();

        if (index >= choices.size() || index < 0) {
            return null;
        }
        return choices.get(index);
    }

    public String getTarget(int index) {
        List<String> targets = getTargets();

        if (index >= targets.size() || index < 0) {
            return null;
        }
        return targets.get(index);
    }

    public List<String> getChoices() {
        List<String> choices = new ArrayList<>();

        String[] menuChoices = context.client.getMenuActions();
        for (int i = menuChoices.length - 1; i >= 0; i--) {
            if (menuChoices[i] != null) {
                choices.add(String.valueOf(menuChoices[i]));
            }
        }
        return choices.subList(choices.size() - context.client.getMenuSize(), choices.size());
    }

    public List<String> getTargets() {
        List<String> choices = new ArrayList<>();

        String[] targets = context.client.getMenuTargets();
        for (int i = targets.length - 1; i >= 0; i--) {
            if (targets[i] != null) {
                choices.add(String.valueOf(targets[i]));
            }
        }
        return choices.subList(choices.size() - context.client.getMenuSize(), choices.size());
    }

    public boolean isMenuOpen() {
        return context.client.isMenuOpen();
    }

    public Rectangle getBounds() {
        return new Rectangle(context.client.getMenuX(), context.client.getMenuY(), context.client.getMenuWidth(), context.client.getMenuHeight());
    }

    public Point getClickPoint(int index) {
        Rectangle bounds = getBounds();
        Point menu = new Point(bounds.x + 4, bounds.y + 4);
        return new Point(menu.x + context.random(4, bounds.width - 4), menu.y + context.random(23, 30) + 15 * index);
    }

    public boolean click(int index) {
        Point p = getClickPoint(index);
        context.mouse.moveMouse(p);
        context.mouse.leftClick();
        return true;
    }

    public boolean click(String action) {
        int index = getIndex(action);
        if (index >= 0) {
            Point p = getClickPoint(index);
            context.mouse.moveMouse(p);
            context.mouse.leftClick();
            return true;
        }
        return false;
    }

}
