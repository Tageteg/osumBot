package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class CacheAnalyzer extends ClassAnalyzer
{

    public CacheAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals("java/lang/Object"))
        {
            short hashtableType = 0, intType = 0, totalCount = 0;
            for (final FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).isNot(ACC_STATIC))
                {
                    if (field.desc.equals("I"))
                    {
                        intType++;
                    } else if (field.desc.equals("L" + getInstance().getAccessors().get("HashTableAccessor").name + ";"))
                    {
                        hashtableType++;
                    }
                    totalCount++;
                }
            }
            if (hashtableType == 1 && intType == 2 && totalCount == 4)
            {
                add("CacheAccessor", node);
                return new ClassIdentity("Cache", node.name, node, 0);
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
        return new String[]{"HashTableAccessor"};
    }
}
