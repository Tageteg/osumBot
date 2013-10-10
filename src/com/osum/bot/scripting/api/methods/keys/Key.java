package com.osum.bot.scripting.api.methods.keys;

public abstract class Key
{
	private int modifier;
	private int location;
	private int code;
	
	public Key(int code, int modifier, int location)
	{
		this.code = code;
		this.modifier = modifier;
		this.location = location;
	}
	
	public int modifier()
	{
		return modifier;
	}
	
	public int location()
	{
		return location;
	}
	
	public int code()
	{
		return code;
	}
}