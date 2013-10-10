package com.osum.bot.debugger.impl;

import java.awt.Graphics;
import java.awt.Point;

import com.osum.bot.debugger.Debugger;
import com.osum.bot.scripting.ScriptContext;

public class DebugMouseCursor extends Debugger
{
	public DebugMouseCursor(ScriptContext context)
	{
		super(context);
	}

	@Override
	public void draw(Graphics g, Point point)
	{
		Point p = context.mouse.getPosition();
		g.drawRect(p.x, p.y, 4, 4);
		g.drawString("Mouse: " + context.mouse.getPosition(), point.x, point.y);
	}
}
