package com.osum.bot.interfaces;

import com.osum.bot.Bot;
import com.osum.bot.BotWindow;

import java.awt.*;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:11 PM
 */
public abstract class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = 1L;

	public Canvas() {

    }
	
	public abstract Component getComponent();

    @Override
    public final Graphics getGraphics() {
        Bot bot = BotWindow.getBot(this);
        Graphics spr = super.getGraphics();
        //System.out.println("omgcall");
        if(bot != null) {
            Graphics g = bot.getStub().getBuffer().getGraphics();
            
            if (bot.script() != null)
            {
            	bot.script().paint(g);
            }
            
            spr.drawImage(bot.getStub().getBuffer(), 0, 0, null);
            return g;
            //return bot.getStub().getBuffer().getGraphics();
        }
        return spr;
    }
}
