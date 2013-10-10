package com.osum.bot.interfaces;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:17 PM
 */
public abstract class Keyboard implements KeyListener {

	private boolean inputBlocked = false;

    public void inputBlocked(boolean value)
    {
    	inputBlocked = value;
    }

    public boolean inputBlocked()
    {
    	return inputBlocked;
    }

    public abstract void _keyPressed(KeyEvent e);

    public abstract void _keyReleased(KeyEvent e);

    public abstract void _keyTyped(KeyEvent e);

    public void sendEvent(KeyEvent e)
    {
    	//System.out.println(e);
    	
    	try
    	{
    		switch (e.getID())
    		{
    			case KeyEvent.KEY_PRESSED:
    				_keyPressed(e);
    				break;
    			case KeyEvent.KEY_RELEASED:
    				_keyReleased(e);
    				break;
    			case KeyEvent.KEY_TYPED:
    				_keyTyped(e);
    				break;
    		}
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }

    @Override
    public void keyTyped(KeyEvent e) {
    	//System.out.println(e + " | " + e.getModifiers() + "/" + e.getModifiersEx());
    	if (!inputBlocked)
    		_keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	//System.out.println(e + " | " + e.getModifiers() + "/" + e.getModifiersEx());
    	if (!inputBlocked)
    		_keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	//System.out.println(e + " | " + e.getModifiers() + "/" + e.getModifiersEx());
    	if (!inputBlocked)
    		_keyReleased(e);
    }
}
