package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.field.FieldExaminer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class ViewPortAnalyzer extends ClassAnalyzer
{

    public ViewPortAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals("java/lang/Object"))
        {
            final Map<String, Integer> map = new FieldExaminer(node).getMap();
            if (map.get("FloatArray") == 1 && map.get("Total") == 1)
            {
                add("ViewPortAccessor", node);
                return new ClassIdentity("ViewPort", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (field.desc.equals("[F"))
                {
                    add(new FieldIdentity("getFloats", field.desc, field.name, field.desc, identity.getClassName(), false));
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
