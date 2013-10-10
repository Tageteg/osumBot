package com.osum.updater.deobfuscator.impl;

import com.osum.updater.deobfuscator.Deobfuscator;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiplierDeobfuscator implements Deobfuscator
{

    private final String pattern = new String("");

    @Override
    public Map<String, ClassNode> visit(Map<String, ClassNode> node)
    {
        final Map<String, ClassNode> newMap = new HashMap<>();
        for (final ClassNode classNode : node.values())
        {
            for (final MethodNode methodNode : (List<MethodNode>) classNode.methods)
            {
                final TypeQuerier querier = new TypeQuerier(methodNode.access);
                if (querier.isNot(ACC_INTERFACE) && querier.isNot(ACC_ABSTRACT))
                {
                    methodNode.accept(new MethodVisitor(ASM4)
                    {
                        @Override
                        public void visitInsn(int opcode)
                        {
                            super.visitInsn(opcode);
                        }
                    });
                }
            }
        }
        return null;
    }

}
