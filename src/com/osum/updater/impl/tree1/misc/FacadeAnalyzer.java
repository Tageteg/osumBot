package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class FacadeAnalyzer extends ClassAnalyzer
{

    public FacadeAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        short varpCount = 0;
        if (node.superName.equals("java/lang/Object"))
        {
            for (final FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).isNot(ACC_STATIC))
                {
                    if (field.desc.equals("L" + getInstance().getAccessors().get("VarpBitsAccessor").name + ";"))
                    {
                        varpCount++;
                    }
                }
            }
            if (varpCount == 1)
            {
                add("FacadeAccessor", node);
                return new ClassIdentity("Facade", node.name, node, 0);
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
                if (field.desc.equals("L" + getInstance().getAccessors().get("VarpBitsAccessor").name + ";"))
                {
                    add(new FieldIdentity("getVarpBits", field.desc, field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"VarpBitsAccessor"};
    }
}
