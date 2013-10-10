package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: Admin
 * Date: 17/03/13
 * Time: 00:28
 */
public class HashTableAnalyzer extends ClassAnalyzer
{

    public HashTableAnalyzer(final Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(final ClassNode node)
    {
        if (node.superName.equals("java/lang/Object"))
        {
            short nodeAField = 0;
            for (final FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).isNot(ACC_STATIC))
                {
                    if (field.desc.equals("[L" + getInstance().getAccessors().get("NodeAccessor").name + ";"))
                    {
                        nodeAField++;
                    }
                }
            }
            if (nodeAField == 1)
            {
                add("HashTableAccessor", node);
                return new ClassIdentity("HashTable", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(final ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC) && field.desc.equals("[L" + getInstance().getAccessors().get("NodeAccessor").name + ";"))
            {
                add(new FieldIdentity("getBuckets", field.desc, field.name, field.desc, identity.getClassName(), false));
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"NodeAccessor"};
    }
}
