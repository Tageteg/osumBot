package com.osum.updater.impl.tree2.item;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.abstracts.Condition;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.*;

import java.util.List;

public class ItemAnalyzer extends ClassAnalyzer
{

    public ItemAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("RenderableAccessor").name))
        {
            TypeQuerier querier = new TypeQuerier(node.access);
            if (querier.is(ACC_PUBLIC) && querier.is(ACC_FINAL))
            {
                int intCount = 0;
                for (FieldNode field : (List<FieldNode>) node.fields)
                {
                    querier = new TypeQuerier(field.access);
                    if (querier.isNot(ACC_STATIC) && querier.isNot(ACC_PUBLIC) && field.desc.equals("I"))
                    {
                        intCount++;
                    }
                }
                if (intCount == 2)
                {
                    add("ItemAccessor", node);
                    return new ClassIdentity("Item", node.name, node, 0);
                }
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final MethodNode method : identity.getMethods())
        {
            final TypeQuerier querier = new TypeQuerier(method.access);
            if (querier.isNot(ACC_STATIC) && querier.is(ACC_PROTECTED) && querier.is(ACC_FINAL))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final FieldInsnNode idField = examiner.next(FieldInsnNode.class, GETFIELD);
                final FieldInsnNode amountField = examiner.next(FieldInsnNode.class, new Condition()
                {
                    @Override
                    public boolean equals(AbstractInsnNode node)
                    {
                        return node.getOpcode() == GETFIELD && !((FieldInsnNode) node).name.equals(idField.name);
                    }
                });
                if (idField != null && amountField != null)
                {
                    final int amountMultiplier = Multipliers.get(identity.getClassName(), amountField.name);
                    add(new FieldIdentity("getAmount", amountField.desc, amountField.name, amountField.desc, identity.getClassName(), false, amountMultiplier));
                    final int idMultiplier = Multipliers.get(identity.getClassName(), idField.name);
                    add(new FieldIdentity("getId", idField.desc, idField.name, idField.desc, identity.getClassName(), false, idMultiplier));
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"RenderableAccessor"};
    }
}
