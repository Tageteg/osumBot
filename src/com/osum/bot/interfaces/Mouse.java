package com.osum.bot.interfaces;

import java.awt.event.*;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:17 PM
 */
public abstract class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener
{
	private boolean inputBlocked = false;
    
    public void inputBlocked(boolean value)
    {
    	inputBlocked = value;
    }
    
    public boolean inputBlocked()
    {
    	return inputBlocked;
    }

    public abstract void _mouseClicked(MouseEvent e);

    public abstract void _mouseDragged(MouseEvent e);

    public abstract void _mouseEntered(MouseEvent e);

    public abstract void _mouseExited(MouseEvent e);

    public abstract void _mouseMoved(MouseEvent e);

    public abstract void _mousePressed(MouseEvent e);

    public abstract void _mouseReleased(MouseEvent e);

    public abstract void _mouseWheelMoved(MouseWheelEvent e);

    public abstract int getX();

    public abstract int getY();

    @Override
    public void mouseClicked(MouseEvent e)
    {
    	if (!inputBlocked)
    		_mouseClicked(e);
        e.consume();
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    	if (!inputBlocked)
    		_mousePressed(e);
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    	if (!inputBlocked)
    		_mouseReleased(e);
        e.consume();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    	if (!inputBlocked)
    		_mouseEntered(e);
        e.consume();
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    	if (!inputBlocked)
    		_mouseExited(e);
        e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
    	if (!inputBlocked)
    		_mouseDragged(e);
        e.consume();
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    	//System.out.println(e);
    	if (!inputBlocked)
    		_mouseMoved(e);
        e.consume();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
    	if (!inputBlocked)
    		_mouseWheelMoved(e);
        e.consume();
    }

    public void sendEvent(MouseEvent e)
    {
        try
        {
        	switch (e.getID())
        	{
        		case MouseEvent.MOUSE_CLICKED:
        			_mouseClicked(e);
        			break;
        		case MouseEvent.MOUSE_DRAGGED:
                    _mouseDragged(e);
        			break;
        		case MouseEvent.MOUSE_ENTERED:
                    _mouseEntered(e);
                    break;
        		case MouseEvent.MOUSE_EXITED:
                    _mouseExited(e);
                    break;
        		case MouseEvent.MOUSE_MOVED:
        			_mouseMoved(e);
        			break;
        		case MouseEvent.MOUSE_PRESSED:
        			_mousePressed(e);
        			break;
        		case MouseEvent.MOUSE_RELEASED:
        			_mouseReleased(e);
        			break;
        		case MouseEvent.MOUSE_WHEEL:
        			_mouseWheelMoved((MouseWheelEvent)e);
        			break;
        	}
        	
        	e.consume();
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
    }

}
