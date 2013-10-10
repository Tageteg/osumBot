package com.osum.bot;

import com.osum.bot.interfaces.Canvas;
import com.osum.bot.scripting.Script;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.net.URL;
import java.net.URLClassLoader;

public class Bot extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final BotStub stub;
	private Canvas canvas;
	private Script script;
	private Applet game;

	public Bot(BotStub stub)
	{
		this.stub = stub;
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		setPreferredSize(Globals.CLIENT_DIMENSION);
	}

	public Script script()
	{
		return script;
	}

	public void run(Script script)
	{
		this.script = script;
		script.setParent(this);
		new Thread(script).start();
	}

	public void start(URL gamePackURL) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException
	{
		game = stub.load(new URLClassLoader(new URL[] { gamePackURL }));
		game.init();
		game.start();
		game.setPreferredSize(Globals.CLIENT_DIMENSION);
        game.setLayout(null);
        game.setBounds(0, 0, Globals.CLIENT_DIMENSION.width, Globals.CLIENT_DIMENSION.height);
		setMaximumSize(game.getPreferredSize());
        add(game);
		setBackground(Color.BLACK);

		while (getCanvas() == null)
		{
			Thread.sleep(1);
		}

		canvas = getCanvas();
	}

	public Canvas getCanvas()
	{
		if (canvas == null)
		{
			if (!(stub.getApplet().getComponentAt(100, 100) instanceof Canvas))
			{
				return null;
			}

			return (Canvas)stub.getApplet().getComponentAt(100, 100);
		}

		return canvas;
	}

	public BotStub getStub()
	{
		return stub;
	}
}
