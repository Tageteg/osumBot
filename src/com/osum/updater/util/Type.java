package com.osum.updater.util;

public class Type
{

    private final String type;

    public Type(final String type)
    {
        this.type = type;
    }

    public boolean isNormal()
    {
        final String[] normals = new String[]{"I", "Z", "B", "J", "S", "Ljava/lang/String;"};
        for (final String string : normals)
        {
            if (type.equals(string) || type.equals("[" + string) || type.equals("[[" + string))
            {
                return true;
            }
        }
        return false;
    }
}
