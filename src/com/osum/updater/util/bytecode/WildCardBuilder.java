package com.osum.updater.util.bytecode;

/**
 * Created with IntelliJ IDEA.
 * User: frazb_000
 * Date: 12/02/13
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class WildCardBuilder
{

    private final String desc;

    public WildCardBuilder(final String desc)
    {
        this.desc = desc;
    }

    public String resolve()
    {
        final StringBuilder builder = new StringBuilder();
        final StringBuilder builder2 = new StringBuilder();
        for (final String string : desc.split(""))
        {
            builder2.append(string);
            builder.append("?");
        }
        System.out.println(builder2.toString());
        return builder.toString();
    }
}
