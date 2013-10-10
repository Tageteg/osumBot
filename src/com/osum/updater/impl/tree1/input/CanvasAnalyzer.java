package com.osum.updater.impl.tree1.input;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class CanvasAnalyzer extends ClassAnalyzer
{

    public CanvasAnalyzer(final Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(final ClassNode node)
    {
        if (node.superName.equals("java/awt/Canvas"))
        {
            add("CanvasAccessor", node);
            return new ClassIdentity("Canvas", node.name, node, 0);
        }
        return null;
    }

    @Override
    public void analyse(final ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (field.desc.equals("Ljava/awt/Component;"))
                {
                    //add(new FieldIdentity("getComponent", field.desc, field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return null;
    }
}
