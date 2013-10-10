package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.abstracts.Condition;
import com.osum.updater.util.bytecode.TypeQuerier;
import com.osum.updater.util.bytecode.WildCardMatcher;
import org.objectweb.asm.tree.*;

import java.util.List;

public class LinkedListAnalyzer extends ClassAnalyzer
{

    public LinkedListAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        short totalCount = 0, listNodeCount = 0;
        for (final FieldNode field : (List<FieldNode>) node.fields)
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (!field.desc.equals("L" + node.name + ";") && field.desc.equals("L" + getInstance().getAccessors().get("LinkedListNodeAccessor").name + ";"))
                {
                    listNodeCount++;
                }
                totalCount++;
            }
        }
        if (totalCount == 2 && listNodeCount == 2)
        {
            add("LinkedListAccessor", node);
            return new ClassIdentity("LinkedList", node.name, node, 0);
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final MethodNode method : identity.getMethods())
        {
            if (new TypeQuerier(method.access).isNot(ACC_STATIC) && new WildCardMatcher(method, "(?)V").match())
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final FieldInsnNode previousField = examiner.next(FieldInsnNode.class, GETFIELD);
                final FieldInsnNode nextField = examiner.next(FieldInsnNode.class, new Condition()
                {
                    @Override
                    public boolean equals(AbstractInsnNode node)
                    {
                        return node.getOpcode() == PUTFIELD && !((FieldInsnNode) node).name.equals(previousField.name);
                    }
                });
                if (previousField != null && nextField != null)
                {
                    add(new FieldIdentity("getNext", nextField.desc, nextField.name, nextField.desc, identity.getClassName(), false));
                    add(new FieldIdentity("getHead", previousField.desc, previousField.name, previousField.desc, identity.getClassName(), false));
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"LinkedListNodeAccessor"};
    }
}
