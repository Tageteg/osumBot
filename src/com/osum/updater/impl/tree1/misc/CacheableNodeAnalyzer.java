package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;

public class CacheableNodeAnalyzer extends ClassAnalyzer
{

    public CacheableNodeAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("NodeAccessor").name))
        {
            short selfType = 0;
            for (final FieldNode field : (ArrayList<FieldNode>) node.fields)
            {
                final TypeQuerier querier = new TypeQuerier(field.access);
                if (querier.isNot(ACC_STATIC))
                {
                    if (field.desc.equals("L" + node.name + ";") && querier.isNot(ACC_FINAL))
                    {
                        selfType++;
                    }
                }
            }
            if (selfType == 2)
            {
                add("CacheableNodeAccessor", node);
                return new ClassIdentity("CacheableNode", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        /*
        for (final FieldNode field : identity.getFields()) {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC)) {
                if (field.desc.equals("J")) {
                    add(new FieldIdentity("getId", field.desc, field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
        for (final MethodNode method : identity.getMethods()) {
            if (new TypeQuerier(method.access).isNot(ACC_STATIC) && new WildCardMatcher(method, "(?)V").match()) {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final FieldInsnNode previousField = examiner.next(FieldInsnNode.class, GETFIELD);
                final FieldInsnNode nextField = examiner.next(FieldInsnNode.class, new Condition() {
                    @Override
                    public boolean equals(AbstractInsnNode node) {
                        return node.getOpcode() == GETFIELD && !((FieldInsnNode) node).name.equals(previousField.name);
                    }
                });
                if (previousField != null && nextField != null) {
                    add(new FieldIdentity("getNext", nextField.name, nextField.desc, identity.getClassName(), false));
                    add(new FieldIdentity("getPrevious", previousField.name, previousField.desc, identity.getClassName(), false));
                }
            }
        }
       */
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"NodeAccessor"};
    }
}
