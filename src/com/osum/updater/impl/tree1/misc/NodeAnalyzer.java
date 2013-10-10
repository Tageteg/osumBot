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

/**
 * Created with IntelliJ IDEA.
 *
 * @author: Admin
 * Date: 16/03/13
 * Time: 17:08
 */
public class NodeAnalyzer extends ClassAnalyzer
{

    public NodeAnalyzer(final Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(final ClassNode node)
    {
        short selfCount = 0, longCount = 0;
        if (!node.superName.equals("java/lang/Object"))
        {
            return null;
        }
        for (final FieldNode field : (List<FieldNode>) node.fields)
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (field.desc.equals("L" + node.name + ";"))
                {
                    selfCount++;
                } else if (field.desc.equals("J"))
                {
                    longCount++;
                }
            }
        }
        if (selfCount == 2 && longCount == 1)
        {
            add("NodeAccessor", node);
            return new ClassIdentity("Node", node.name, node, 0);
        }
        return null;
    }

    @Override
    public void analyse(final ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            final TypeQuerier querier = new TypeQuerier(field.access);
            if (field.desc.equals("J") && querier.is(ACC_PUBLIC) && querier.isNot(ACC_FINAL))
            {
                add(new FieldIdentity("getId", field.desc, field.name, field.desc, identity.getClassName(), false));
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            if (new TypeQuerier(method.access).is(ACC_PUBLIC) && new WildCardMatcher(method, "()V").match())
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final FieldInsnNode previousField = examiner.next(FieldInsnNode.class, GETFIELD);
                final FieldInsnNode nextField = examiner.next(FieldInsnNode.class, new Condition()
                {
                    @Override
                    public boolean equals(AbstractInsnNode node)
                    {
                        return node.getOpcode() == GETFIELD && !((FieldInsnNode) node).name.equals(previousField.name);
                    }
                });
                if (previousField != null && nextField != null)
                {
                    add(new FieldIdentity("getNext", "Node", nextField.name, nextField.desc, identity.getClassName(), false));
                    add(new FieldIdentity("getPrevious", "Node", previousField.name, previousField.desc, identity.getClassName(), false));
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
