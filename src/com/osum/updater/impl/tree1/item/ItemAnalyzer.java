package com.osum.updater.impl.tree1.item;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.Type;
import com.osum.updater.util.abstracts.Condition;
import com.osum.updater.util.bytecode.ClassResolver;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Francis
 * Date: 27/03/13
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemAnalyzer extends ClassAnalyzer
{

    public ItemAnalyzer(Analyzer instance)
    {
        super(instance);
    }


    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals("java/lang/Object") && node.interfaces.size() == 1)
        {
            short stringArray = 0, hashTable = 0, string = 0;
            for (final FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).isNot(ACC_STATIC))
                {
                    if (field.desc.equals("[Ljava/lang/String;"))
                    {
                        stringArray++;
                    } else if (field.desc.equals("L" + getInstance().getAccessors().get("HashTableAccessor").name + ";"))
                    {
                        hashTable++;
                    } else if (field.desc.equals("Ljava/lang/String;"))
                    {
                        string++;
                    }
                }
            }
            if (hashTable == 1 && string == 1)
            {
                return new ClassIdentity("Item", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (field.desc.equals("Ljava/lang/String;"))
                {
                    add(new FieldIdentity("getMethodName", field.desc, field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            final TypeQuerier querier = new TypeQuerier(method.access);
            if (querier.isNot(ACC_STATIC))
            {
                if (method.desc.endsWith("L" + identity.getClassName() + ";"))
                {
                    final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                    final FieldInsnNode field = examiner.next(FieldInsnNode.class, new Condition()
                    {
                        @Override
                        public boolean equals(AbstractInsnNode node)
                        {
                            return node.getOpcode() == GETFIELD && !new Type(((FieldInsnNode) node).desc).isNormal();
                        }
                    });
                    if (field != null)
                    {
                        add(new FieldIdentity("getLoader", field.desc, field.name, field.desc, identity.getClassName(), false));
                        add("ItemLoaderAccessor", getInstance().getClasses().get(new ClassResolver(field.desc).resolve()));
                    }
                }
            }
        }
        if (getInstance().getAccessors().get("ItemLoaderAccessor") != null)
        {
            for (final MethodNode method : identity.getMethods())
            {
                final TypeQuerier querier = new TypeQuerier(method.access);
                if (querier.isNot(ACC_PUBLIC) && method.desc.equals("(L" + identity.getClassName() + ";L" + identity.getClassName() + ";)V"))
                {
                    final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                    final String[] names = new String[]{"getInventoryActions", "getGroundActions"};
                    for (int index = 0; index < 2; index++)
                    {
                        final FieldInsnNode field = examiner.next(FieldInsnNode.class, new Condition()
                        {
                            @Override
                            public boolean equals(AbstractInsnNode node)
                            {
                                return node.getOpcode() == GETFIELD && ((FieldInsnNode) node).desc.equals("[Ljava/lang/String;");
                            }
                        });
                        if (field != null)
                        {
                            add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"HashTableAccessor"};
    }
}
