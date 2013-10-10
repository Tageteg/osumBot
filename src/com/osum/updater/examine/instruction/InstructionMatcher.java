package com.osum.updater.examine.instruction;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;

public class InstructionMatcher
{

    private final com.osum.updater.examine.instruction.InstructionExaminer examiner;

    public InstructionMatcher(final InsnList instructions)
    {
        this.examiner = new com.osum.updater.examine.instruction.InstructionExaminer(instructions);
    }

    public List<AbstractInsnNode[]> match(final int... pattern)
    {
        final ArrayList<AbstractInsnNode[]> nodes = new ArrayList<AbstractInsnNode[]>();
        while (examiner.hasNext())
        {
            final AbstractInsnNode[] temp = new AbstractInsnNode[pattern.length];
            for (int index = 0; index < pattern.length; index++)
            {
                final AbstractInsnNode node = examiner.next();
                if (node.getOpcode() == pattern[index])
                {
                    temp[index] = node;
                } else
                {
                    break;
                }
                if (index == (pattern.length - 1))
                {
                    nodes.add(temp);
                }
            }
        }
        return nodes;
    }

    public List<AbstractInsnNode[]> match(final String pattern)
    {
        final InsnList list = new InsnList();
        for (final AbstractInsnNode node : examiner.getInstructions())
        {
            list.add(node);
        }
        return new com.osum.updater.examine.instruction.RegexInstructionMatcher(list).search(pattern);
    }

    public boolean find(final int... pattern)
    {
        final ArrayList<AbstractInsnNode> nodes = new ArrayList<AbstractInsnNode>();
        while (examiner.hasNext())
        {
            for (final int type : pattern)
            {
                final AbstractInsnNode node = examiner.next();
                if (node != null)
                {
                    if (node.getOpcode() == type)
                    {
                        nodes.add(node);
                        if (nodes.size() == pattern.length)
                        {
                            return true;
                        }
                        continue;
                    }
                }
                nodes.clear();
                break;
            }
        }
        return false;
    }

    public com.osum.updater.examine.instruction.InstructionExaminer getExaminer()
    {
        return examiner;
    }

}
