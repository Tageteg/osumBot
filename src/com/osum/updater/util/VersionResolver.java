package com.osum.updater.util;

import com.osum.updater.examine.instruction.InstructionMatcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;

public class VersionResolver
{

    private ClassNode classNode;

    public VersionResolver(final ClassNode classNode)
    {
        this.classNode = classNode;
    }

    public int resolve()
    {
        for (final MethodNode methodNode : (ArrayList<MethodNode>) classNode.methods)
        {
            if (methodNode.name.equals("init"))
            {
                final InstructionMatcher matcher = new InstructionMatcher(methodNode.instructions);
                matcher.match(new int[]{Opcodes.SIPUSH, Opcodes.ICONST_1});
                final IntInsnNode field = matcher.getExaminer().previous(IntInsnNode.class, Opcodes.SIPUSH);
                if (field != null)
                {
                    return field.operand;
                }
            }
        }
        return -1;
    }

}
