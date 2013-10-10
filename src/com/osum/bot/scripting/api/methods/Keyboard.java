package com.osum.bot.scripting.api.methods;

import com.osum.bot.interfaces.Client;
import com.osum.bot.scripting.ScriptContext;
import com.osum.bot.scripting.api.methods.keys.Key;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Keyboard
{
	public static char getOtherCharacter(char code)
	{	
		if ((code >= 'a') && (code <= 'z'))
		{
			return (char)(code - 32);
		}
		else if ((code >= 'A') && (code <= 'Z'))
		{
			return (char)(code + 32);
		}
		else
		{
			switch (code)
			{
				case KeyEvent.VK_0:
					return ')';
				case KeyEvent.VK_1:
					return '!';
				case KeyEvent.VK_2:
					return '@';
				case KeyEvent.VK_3:
					return '#';
				case KeyEvent.VK_4:
					return '$';
				case KeyEvent.VK_5:
					return '%';
				case KeyEvent.VK_6:
					return '^';
				case KeyEvent.VK_7:
					return '&';
				case KeyEvent.VK_8:
					return '*';
				case KeyEvent.VK_9:
					return '(';
				case 189: // -
					return '_';
				case KeyEvent.VK_COMMA:
					return '<';
				case KeyEvent.VK_PERIOD:
					return '>';
				case KeyEvent.VK_EQUALS:
					return '+';
				case KeyEvent.VK_SLASH:
					return '?';
				case KeyEvent.VK_SEMICOLON:
					return ':';
				case KeyEvent.VK_OPEN_BRACKET:
					return '{';
				case KeyEvent.VK_CLOSE_BRACKET:
					return '}';
				case KeyEvent.VK_BACK_SLASH:
					return '|';
				case '\'':
					return '"';
			}
		}
		
		return '\0';
	}
	
	private com.osum.bot.interfaces.Keyboard botBoard;
	private final ScriptContext context;
	
	private HashMap<Integer, Key> virtualBoard = new HashMap<Integer, Key>();
	
	public Keyboard(ScriptContext context)
	{
		this.context = context;
		
		
	}
	
	public void dispatchEvent(int id, int code, char c, int modifier, int location)
	{
		botBoard.sendEvent(new KeyEvent(context.bot.getCanvas(), id, System.currentTimeMillis(), modifier, code, c, location));
	}
	
	public void keyPressed(int c, int modifier)
	{
		dispatchEvent(KeyEvent.KEY_PRESSED, c, (char)KeyEvent.VK_UNDEFINED, modifier, KeyEvent.KEY_LOCATION_STANDARD);
	}
	
	public void keyReleased(int c, int modifier)
	{
		dispatchEvent(KeyEvent.KEY_RELEASED, c, (char)KeyEvent.VK_UNDEFINED, modifier, KeyEvent.KEY_LOCATION_STANDARD);
	}
	
	public void keyTyped(int c, int modifier)
	{
		dispatchEvent(KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, (char)c, modifier, KeyEvent.KEY_LOCATION_UNKNOWN);
	}
	
	public void sendKey(char c)
	{
		sendKey(c, 0);
	}
	
	public void sendKey(char ch, int modifier)
	{
		boolean shift = false;
    	int code = ch;
    	
    	if((code >= 'a') && (code <= 'z'))
    	{
    		//code = KeyEvent.getExtendedKeyCodeForChar(code);
    		//code -= 32;
    	}
    	else if((code >= 'A') && (code <= 'Z') || code == KeyEvent.VK_EXCLAMATION_MARK)
    	{
    		shift = true;
        	modifier |= KeyEvent.SHIFT_DOWN_MASK;
    	}

		//System.out.println(shift + " shift " + ch + " (" + ((int)ch) + ")/" + code + " (" + ((char)code) + ")");
		
    	if (shift)
    	{
    		keyPressed(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK);
    		keyPressed(code, modifier);
    		context.sleep(context.random(5, 75));
			keyTyped(code, 0);
			keyReleased(code, modifier);
    		context.sleep(context.random(5, 50));
			keyTyped(KeyEvent.VK_SHIFT, 0);
			keyReleased(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK);
    	}
    	else
    	{
    		keyPressed(code, modifier);
    		context.sleep(context.random(50, 150));
			keyTyped(code, 0);
			keyReleased(code, modifier);
    	}
	}
	
	public void sendKeys(String str, boolean enter)
	{
		for (char c : str.toCharArray())
		{
			sendKey(c, 0);
			context.sleep(context.random(15, 150));
		}
		
		if (enter)
		{
			sendKey((char)KeyEvent.VK_ENTER, 0);
		}
	}
	
	public void setup(Client client)
	{
		this.botBoard = client.getKeyboard();
	}
	
	public void setInputBlocking(boolean blockingInput)
	{
		botBoard.inputBlocked(blockingInput);
	}
	
	public boolean isBlockingInput()
	{
		return botBoard.inputBlocked();
	}
}
