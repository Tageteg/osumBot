package com.osum.bot.scripting.api.util;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 1:10 PM
 */
public interface Filter<T>
{

    public boolean accept(T t);

}
