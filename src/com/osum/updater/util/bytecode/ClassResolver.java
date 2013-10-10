package com.osum.updater.util.bytecode;

public class ClassResolver
{

    private final String desc;

    public ClassResolver(final String desc)
    {
        this.desc = desc;
    }

    public String resolve()
    {
        final StringBuilder builder = new StringBuilder();
        final String[] split = desc.split("");
        int lChance = 0;
        for (String string : split)
        {
            if (string.equals("L") || string.equals(";") || string.equals("["))
            {
                if (lChance == 0 && string.equals("L"))
                {
                    lChance++;
                    continue;
                } else if (string.equals("L"))
                {
                    builder.append("L");
                    continue;
                }
                continue;
            }
            builder.append(string);
        }
        return builder.toString();
    }


}
