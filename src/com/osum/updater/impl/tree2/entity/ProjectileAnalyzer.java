package com.osum.updater.impl.tree2.entity;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.field.FieldExaminer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import org.objectweb.asm.tree.ClassNode;


public class ProjectileAnalyzer extends ClassAnalyzer
{

    public ProjectileAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("RenderableAccessor").name))
        {
            final FieldExaminer examiner = new FieldExaminer(node);
            if (examiner.getMap().get("Double") > 5)
            {
                return new ClassIdentity("Projectile", node.name, node, 0);
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
        return new String[]{"RenderableAccessor"};
    }
}
