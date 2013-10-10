package com.osum.bot.scripting.api.methods;

import com.osum.bot.interfaces.Client;
import com.osum.bot.scripting.ScriptContext;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Mouse
{
	private com.osum.bot.interfaces.Mouse botMouse;
	private Point position = new Point(0, 0);
	private final ScriptContext context;
	
	public Mouse(ScriptContext context)
	{
		this.context = context;
	}
	
	public Point getActualMousePosition()
	{
		return new Point(botMouse.getX(), botMouse.getY());
	}
	
	public void setup(Client client)
	{
		this.botMouse = client.getMouse();
	}
	
	public void leftClick()
	{
		leftMousePressed(position);
		leftMouseReleased(position);
		leftMouseClicked(position);
	}

    public void rightClick()
    {
        rightMousePressed(position);
        rightMouseReleased(position);
        rightMouseClicked(position);
    }

	public void dragMouse(Point destination)
	{
		leftMousePressed(position);
		moveMouse(destination, true);
		leftMouseReleased(destination);
	}
	
	public void linearMouseMoveOld(Point destination)
	{
		linearMouseMoveOld(destination, false);
	}
	
	public void linearMouseMoveOld(Point destination, boolean drag)
	{
		int deltaX = destination.x - position.x;
		int deltaY = destination.y - position.y;
		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		
		for (int i = 0; i < max; i++) {
			if (deltaX < 0) {
				deltaX++;
			} else if (deltaX > 0) {
				deltaX--;
			}
			if (deltaY < 0) {
				deltaY++;
			} else if (deltaY > 0) {
				deltaY--;
			}
			
			mouseMove(new Point(destination.x - deltaX, destination.y - deltaY));
			
			if (drag)
			{
				mouseDrag(new Point(destination.x - deltaX, destination.y - deltaY), MouseEvent.BUTTON1_DOWN_MASK, 1);
			}
			
			try
			{
				Thread.sleep(context.random(2, 7));
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized Point moveMouse(Point point)
	{
		return moveMouse(point, false);
	}
	
	public synchronized Point moveMouse(Point point, boolean drag)
	{
		return moveMouse(point.x, point.y, 0.50, drag);
	}
	
	public synchronized Point moveMouse(int x, int y, final double speedFactor, boolean drag)
	{
		double speed = (context.random() * 15D + 15D) / 10D;
		speed *= speedFactor;
		return windMouseImpl(position.x, position.y, x, y, 9D, 3D, 5D / speed, 10D / speed, 10D * speed, 8D * speed, drag);
	}
	
	private synchronized Point windMouseImpl(double xs, double ys, double xe, double ye, double gravity, double wind, double minWait, double maxWait, double maxStep, double targetArea, boolean drag)
	{
		// System.out.println(targetArea);
		final double sqrt3 = Math.sqrt(3);
		final double sqrt5 = Math.sqrt(5);
		double dist, veloX = 0, veloY = 0, windX = 0, windY = 0;
		while ((dist = Math.hypot(xs - xe, ys - ye)) >= 1) {
			wind = Math.min(wind, dist);
			if (dist >= targetArea) {
				windX = windX / sqrt3
						+ (Math.random() * (wind * 2D + 1D) - wind) / sqrt5;
				windY = windY / sqrt3
						+ (Math.random() * (wind * 2D + 1D) - wind) / sqrt5;
			} else {
				windX /= sqrt3;
				windY /= sqrt3;
				if (maxStep < 3) {
					maxStep = Math.random() * 3 + 3D;
				} else {
					maxStep /= sqrt5;
				}
				// System.out.println(maxStep + ":" + windX + ";" + windY);
			}
			veloX += windX + gravity * (xe - xs) / dist;
			veloY += windY + gravity * (ye - ys) / dist;
			double veloMag = Math.hypot(veloX, veloY);
			if (veloMag > maxStep) {
				double randomDist = maxStep / 2D + Math.random() * maxStep / 2D;
				veloX = (veloX / veloMag) * randomDist;
				veloY = (veloY / veloMag) * randomDist;
			}
			xs += veloX;
			ys += veloY;
			int mx = (int) Math.round(xs);
			int my = (int) Math.round(ys);
			if (position.x != mx || position.y != my) {
				mouseMove(new Point(mx, my));
				
				if (drag)
				{
					mouseDrag(new Point(mx, my), MouseEvent.BUTTON1_DOWN_MASK, 1);
				}
			}
			double step = Math.hypot(xs - position.x, ys - position.y);
			context.sleep(Math.round((maxWait - minWait) * (step / maxStep) + minWait));
		}
		return new Point(position.x, position.y);
	}
	
	public void dispatchEvent(int id, Point position, int modifier, int clickCount)
	{
		this.position = position;
		botMouse.sendEvent(new MouseEvent(context.bot.getCanvas(), id, System.currentTimeMillis(), modifier, position.x, position.y, clickCount, false));
	}

    public void rightMousePressed(Point position)
    {
        mousePress(position, MouseEvent.BUTTON3_MASK, 1);
    }
	
	public void leftMousePressed(Point position)
	{
		mousePress(position, MouseEvent.BUTTON1_MASK, 1);
	}

    public void rightMouseReleased(Point position)
    {
        mouseRelease(position, MouseEvent.BUTTON3_MASK, 1);
    }
	
	public void leftMouseReleased(Point position)
	{
		mouseRelease(position, MouseEvent.BUTTON1_MASK, 1);
	}

    public void rightMouseClicked(Point position)
    {
        mouseClick(position, MouseEvent.BUTTON3_MASK, 1);
    }
	
	public void leftMouseClicked(Point position)
	{
		mouseClick(position, MouseEvent.BUTTON1_MASK, 1);
	}
	
	public void mouseMove(Point position)
	{
		mouseMove(position, 0, 0);
	}
	
	public void mouseMove(Point position, int modifier, int clickCount)
	{
		dispatchEvent(MouseEvent.MOUSE_MOVED, position, modifier, clickCount);
	}
	
	public void mouseDrag(Point position, int modifier, int clickCount)
	{
		dispatchEvent(MouseEvent.MOUSE_DRAGGED, position, modifier, clickCount);
	}

	public void mousePress(Point position, int modifier, int clickCount)
	{
		dispatchEvent(MouseEvent.MOUSE_PRESSED, position, modifier, clickCount);
	}
	
	public void mouseRelease(Point position, int modifier, int clickCount)
	{
		dispatchEvent(MouseEvent.MOUSE_RELEASED, position, modifier, clickCount);
	}
	
	public void mouseClick(Point position, int modifier, int clickCount)
	{
		dispatchEvent(MouseEvent.MOUSE_CLICKED, position, modifier, clickCount);
	}
	
	public Point getPosition()
	{
		return position;
	}
	
	public void setInputBlocking(boolean blockingInput)
	{
		botMouse.inputBlocked(blockingInput);
	}
	
	public boolean isBlockingInput()
	{
		return botMouse.inputBlocked();
	}
}
