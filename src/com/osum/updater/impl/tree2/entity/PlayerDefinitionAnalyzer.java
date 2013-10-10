package com.osum.updater.impl.tree2.entity;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;


public class PlayerDefinitionAnalyzer extends ClassAnalyzer
{

    public PlayerDefinitionAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        return node.name.equals(getInstance().getAccessors().get("PlayerDefinitionAccessor").name) ? new ClassIdentity("PlayerDefinition", node.name, node, 0) : null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            final TypeQuerier querier = new TypeQuerier(field.access);
            if (querier.isNot(ACC_STATIC))
            {
                if (field.desc.equals("[I") && querier.isNot(ACC_PUBLIC))
                {
                    add(new FieldIdentity("getAppearance", field.desc, field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            if (method.name.equals("<init>") && method.instructions.size() < 25)
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final FieldInsnNode field = examiner.next(FieldInsnNode.class, PUTFIELD);
                if (field != null)
                {
                    final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                    add(new FieldIdentity("getId", field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"PlayerDefinitionAccessor"};
    }
}
