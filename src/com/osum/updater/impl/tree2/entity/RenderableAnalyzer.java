package com.osum.updater.impl.tree2.entity;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class RenderableAnalyzer extends ClassAnalyzer
{

    public RenderableAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("CacheableNodeAccessor").name))
        {
            for (final MethodNode method : (List<MethodNode>) node.methods)
            {
                if (method.desc.equals("(IIIIIIIII)V"))
                {
                    add("RenderableAccessor", node);
                    return new ClassIdentity("Renderable", node.name, node, 0);
                }
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            final TypeQuerier querier = new TypeQuerier(field.access);
            if (field.desc.equals("I") && querier.is(ACC_PUBLIC) && querier.isNot(ACC_FINAL))
            {
                add(new FieldIdentity("getHeight", field.desc, field.name, field.desc, identity.getClassName(), false, Multipliers.get(identity.getClassNode().name, field.name)));
            }
        }
        /*
        for (final MethodNode method : identity.getMethods())
        {
            if (method.name.equals("<init>"))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final List<FieldInsnNode> nodes = examiner.findAll(FieldInsnNode.class, new Condition()
                {
                    @Override
                    public boolean equals(AbstractInsnNode node)
                    {
                        return node.getOpcode() == PUTFIELD && ((FieldInsnNode) node).desc.equals("S");
                    }
                });
                if (nodes.size() == 4)
                {
                    final FieldInsnNode minX = nodes.get(0);
                    add(new FieldIdentity("getMinimumX", minX.name, minX.desc, identity.getClassName(), false));
                    final FieldInsnNode maxX = nodes.get(1);
                    add(new FieldIdentity("getMaximumX", maxX.name, maxX.desc, identity.getClassName(), false));
                    final FieldInsnNode minY = nodes.get(2);
                    add(new FieldIdentity("getMinimumY", minY.name, minY.desc, identity.getClassName(), false));
                    final FieldInsnNode maxY = nodes.get(3);
                    add(new FieldIdentity("getMaximumY", maxY.name, maxY.desc, identity.getClassName(), false));
                }
            }
        }
        */
    }

    @Override
    public String[] accessors()
    {
        return null;
    }
}
