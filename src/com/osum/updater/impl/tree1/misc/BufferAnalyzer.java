package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class BufferAnalyzer extends ClassAnalyzer
{

    public BufferAnalyzer(final Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("NodeAccessor").name))
        {
            for (MethodNode method : (List<MethodNode>) node.methods)
            {
                if (new TypeQuerier(method.access).isNot(ACC_STATIC) && method.desc.contains("BigInteger"))
                {
                    add("BufferAccessor", node);
                    return new ClassIdentity("Buffer", node.name, node, 0);
                }
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        /*
        for (final FieldNode field : identity.getFields())
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (field.desc.equals("[B"))
                {
                    add(new FieldIdentity("getBuffer", field.desc, field.name, field.desc, identity.getClassName(), false));
                } else if (field.desc.equals("I"))
                {
                    final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                    add(new FieldIdentity("getOffset", field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                }
            }
        }
        */
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"NodeAccessor"};
    }
}
