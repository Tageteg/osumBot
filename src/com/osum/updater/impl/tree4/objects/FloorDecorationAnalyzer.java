package com.osum.updater.impl.tree4.objects;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 8:00 AM
 */
public class FloorDecorationAnalyzer extends ClassAnalyzer
{
    public FloorDecorationAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.name.equals(getInstance().getAccessors().get("FloorDecorationAccessor").name))
        {
            return new ClassIdentity("FloorDecoration", node.name, node, 0);
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            if (field.desc.equals("L" + getInstance().getAccessors().get("RenderableAccessor").name + ";"))
            {
                add(new FieldIdentity("getRenderable", "Renderable", field.name, field.desc, identity.getClassName(), false));
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[] {"FloorDecorationAccessor"};
    }
}
