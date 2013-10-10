package com.osum.bot.scripting.api.methods.keys;

public class CharacterKey extends Key
{
	private boolean secondCharacterShift = true;
	private char secondCharacter;
	private char firstCharacter;
	
	public CharacterKey(char firstCharacter, char secondCharacter, int code, int modifier, int location, boolean secondCharacterShift)
	{
		super(code, modifier, location);
		this.firstCharacter = firstCharacter;
		this.secondCharacter = secondCharacter;
		this.secondCharacterShift = secondCharacterShift;
	}
	
	public CharacterKey(char character, int code, int modifier, int location)
	{
		this(character, '\0', code, modifier, location, true);
	}
	
	public char firstCharacter()
	{
		return firstCharacter;
	}
	
	public char secondCharacter()
	{
		return secondCharacter;
	}
	
	public boolean secondCharacterShift()
	{
		return secondCharacterShift;
	}
}