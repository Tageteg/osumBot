package com.osum.updater.impl.tree1.misc;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.abstracts.Condition;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.*;

import java.util.List;

public class VarpBitsAnalyzer extends ClassAnalyzer
{

    public VarpBitsAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals("java/lang/Object") && node.interfaces != null && node.interfaces.size() == 1)
        {
            short intArrayCount = 0, totalCount = 0;
            for (final FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).isNot(ACC_STATIC))
                {
                    if (field.desc.equals("[I"))
                    {
                        intArrayCount++;
                    }
                    totalCount++;
                }
            }
            if (intArrayCount == 2 && totalCount <= 4)
            {
                add("VarpBitsAccessor", node);
                return new ClassIdentity("VarpBits", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final MethodNode method : identity.getMethods())
        {
            if (method.name.equals("<init>"))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final FieldInsnNode field = examiner.next(FieldInsnNode.class, new Condition()
                {
                    @Override
                    public boolean equals(AbstractInsnNode node)
                    {
                        return node.getOpcode() == PUTFIELD && ((FieldInsnNode) node).desc.equals("[I");
                    }
                });
                if (field != null)
                {
                    add(new FieldIdentity("getBits", field.desc, field.name, field.desc, identity.getClassName(), false));
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
