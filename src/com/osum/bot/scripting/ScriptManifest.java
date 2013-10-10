package com.osum.bot.scripting;

public @interface ScriptManifest
{
	String name();
	String description();
	double version() default 1.0;
}
