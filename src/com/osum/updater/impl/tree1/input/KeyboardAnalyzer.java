package com.osum.updater.impl.tree1.input;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public class KeyboardAnalyzer extends ClassAnalyzer
{

    public KeyboardAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        for (final String iface : (List<String>) node.interfaces)
        {
            if (iface.equals("java/awt/event/KeyListener"))
            {
                add("KeyboardAccessor", node);
                return new ClassIdentity("Keyboard", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {

    }

    @Override
    public String[] accessors()
    {
        return null;
    }
}
