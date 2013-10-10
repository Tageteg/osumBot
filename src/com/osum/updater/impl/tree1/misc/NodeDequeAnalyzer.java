package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: Admin
 * Date: 16/03/13
 * Time: 17:24
 */
public class NodeDequeAnalyzer extends ClassAnalyzer
{

    public NodeDequeAnalyzer(final Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(final ClassNode node)
    {
        if (!node.name.equals(getInstance().getAccessors().get("NodeAccessor").name) && node.superName.equals("java/lang/Object"))
        {
            int nodeType = 0, publicNodeType = 0;
            for (final FieldNode field : (ArrayList<FieldNode>) node.fields)
            {
                final TypeQuerier querier = new TypeQuerier(field.access);
                if (querier.isNot(ACC_PUBLIC) && field.desc.equals("L" + getInstance().getAccessors().get("NodeAccessor").name + ";"))
                {
                    nodeType++;
                } else if (querier.is(ACC_PUBLIC) && field.desc.equals("L" + getInstance().getAccessors().get("NodeAccessor").name + ";"))
                {
                    publicNodeType++;
                }
            }
            if (publicNodeType == 1 && nodeType == 1)
            {
                add("NodeDequeAccessor", node);
                return new ClassIdentity("NodeDeque", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(final ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            final TypeQuerier querier = new TypeQuerier(field.access);
            if (querier.isNot(ACC_STATIC))
            {
                if (querier.isNot(ACC_PUBLIC) && field.desc.equals("L" + getInstance().getAccessors().get("NodeAccessor").name + ";"))
                {
                    add(new FieldIdentity("getCurrent", "Node", field.name, field.desc, identity.getClassName(), false));
                } else if (querier.is(ACC_PUBLIC) && field.desc.equals("L" + getInstance().getAccessors().get("NodeAccessor").name + ";"))
                {
                    add(new FieldIdentity("getTail", "Node", field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"NodeAccessor"};
    }
}
