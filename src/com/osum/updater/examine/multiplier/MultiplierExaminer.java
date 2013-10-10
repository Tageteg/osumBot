package com.osum.updater.examine.multiplier;

import com.osum.updater.examine.instruction.RegexInstructionMatcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MultiplierExaminer implements Opcodes
{

    private static final String[] PATTERNS = {
            "(ldc|getstatic|aload) (aload|getfield|getstatic|ldc|invokevirtual) (imul|putstatic|getfield|ldc) (imul)?",
            "(ldc|getstatic|aload) (aload|getfield|getstatic|ldc|invokevirtual) (getfield|ldc) imul putstatic"
    };

    @SuppressWarnings("unchecked")
    public static void findMultipliers(Map<String, ClassNode> classMap)
    {
        final Iterator<ClassNode> classes = classMap.values().iterator();
        while (classes.hasNext())
        {
            ClassNode cn = classes.next();
            List<MethodNode> methods = (List<MethodNode>) cn.methods;
            for (MethodNode mn : methods)
            {
                RegexInstructionMatcher searcher = new RegexInstructionMatcher(mn.instructions);
                for (String pattern : PATTERNS)
                {
                    List<AbstractInsnNode[]> matches = searcher.search(pattern);
                    for (AbstractInsnNode[] match : matches)
                    {
                        Integer value = null, refHash = null;
                        for (AbstractInsnNode insn : match)
                        {
                            if (insn.getOpcode() == LDC)
                            {
                                try
                                {
                                    value = (Integer) ((LdcInsnNode) insn).cst;
                                } catch (ClassCastException cce)
                                {
                                    break;
                                }
                            }
                            if (insn.getOpcode() == GETFIELD || insn.getOpcode() == GETSTATIC)
                            {
                                FieldInsnNode fieldInsn = ((FieldInsnNode) insn);
                                refHash = Multipliers.getHash(fieldInsn.owner, fieldInsn.name);
                            }
                            if (insn.getOpcode() == PUTSTATIC)
                            {
                                FieldInsnNode fieldInsn = ((FieldInsnNode) insn);
                                refHash = Multipliers.getHash(fieldInsn.owner, fieldInsn.name);
                            }
                        }

                        if (refHash != null && value != null)
                        {
                            Multipliers.put(refHash, value);
                        }
                    }
                }
            }
        }
    }

}
