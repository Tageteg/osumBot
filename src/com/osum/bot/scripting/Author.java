package com.osum.bot.scripting;

import java.net.URL;

/**
 * Working on something, ignore for now.
 * @author Supah Fly
 */
public class Author
{
	private final String name;
	private final URL website;
	
	public Author(String name, URL website)
	{
		this.name = name;
		this.website = website;
	}
	
	public String name()
	{
		return name;
	}
	
	public URL website()
	{
		return website;
	}
}
