package com.osum.updater.impl.tree2.animation;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class AnimationAnalyzer extends ClassAnalyzer
{

    public AnimationAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        short intArrayCount = 0, ownArrayCount = 0;
        for (final FieldNode field : (List<FieldNode>) node.fields)
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (field.desc.equals("[[I"))
                {
                    intArrayCount++;
                }
            } else
            {
                if (field.desc.equals("[L" + node.name + ";"))
                {
                    ownArrayCount++;
                }
            }
        }
        if (intArrayCount > 1 && ownArrayCount == 1)
        {
            add("AnimationAccessor", node);
            return new ClassIdentity("Animation", node.name, node, 0);
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
