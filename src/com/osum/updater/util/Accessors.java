package com.osum.updater.util;

import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;

public class Accessors
{

    private final Map<String, ClassNode> accessors = new HashMap<>();

    public void add(final String name, final ClassNode node)
    {
        this.accessors.put(name, node);
    }

    public ClassNode get(final String name)
    {
        return this.accessors.get(name);
    }

    public boolean contains(final String... names)
    {
        for (final String string : names)
        {
            if (accessors.get(string) == null)
            {
                return false;
            }
        }
        return true;
    }

    public Map<String, ClassNode> getAll()
    {
        return accessors;
    }

}
