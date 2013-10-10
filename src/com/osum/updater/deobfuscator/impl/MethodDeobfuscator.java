package com.osum.updater.deobfuscator.impl;

import com.osum.updater.deobfuscator.Deobfuscator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodDeobfuscator implements Deobfuscator
{

    private int removedMethods = 0, keptMethods = 0;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ClassNode> visit(Map<String, ClassNode> classes)
    {
        final Map<String, ClassNodeGraph> classGraphs = new HashMap<String, com.osum.updater.deobfuscator.impl.MethodDeobfuscator.ClassNodeGraph>();
        for (ClassNode cn : classes.values())
        {
            classGraphs.put(cn.name, new ClassNodeGraph(cn));
        }
        for (final ClassNodeGraph cg : classGraphs.values())
        {
            ClassNode cn = cg.cn;
            for (final MethodNode mn : (List<MethodNode>) cn.methods)
            {
                mn.accept(new MethodVisitor(ASM4)
                {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc)
                    {
                        if (!owner.startsWith("java") && !owner.startsWith("netscape"))
                        {
                            addMethodVisited(classGraphs, owner, name, desc);
                        }
                        super.visitMethodInsn(opcode, owner, name, desc);
                    }
                });
            }
        }
        final Map<String, ClassNode> newClassMap = new HashMap<>();
        for (final ClassNodeGraph cng : classGraphs.values())
        {
            final ClassNode cn = cng.cn;
            cn.accept(new ClassVisitor(ASM4)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
                {
                    if (cng.visited.get(desc + "||" + name) != null || name.equals("<init>") || name.equals("<clinit>") || desc.contains("java/awt"))
                    {
                        ++keptMethods;
                        return super.visitMethod(access, name, desc, signature, exceptions);
                    }
                    ++removedMethods;
                    return null;
                }
            });
            newClassMap.put(cn.name, cn);
        }
        System.out.println("Removed " + removedMethods + " Methods And Left " + keptMethods + " Methods");
        return newClassMap;
    }

    @SuppressWarnings("unchecked")
    private MethodNode getMethod(
            final ClassNode cn,
            final String desc,
            final String name)
    {

        for (MethodNode mn : (List<MethodNode>) cn.methods)
        {
            if (mn.desc.equals(desc) && mn.name.equals(name))
                return mn;
        }
        return null;
    }

    private void addMethodVisited(
            final Map<String, ClassNodeGraph> classes,
            final String className,
            final String methodName,
            final String methodDesc)
    {

        final ClassNodeGraph cng = classes.get(className);
        if (cng == null) return;
        final ClassNode cn = cng.cn;
        final MethodNode mn = getMethod(cn, methodDesc, methodName);

        if (mn != null)
        {
            cng.visited.put(methodDesc + "||" + methodName, mn);
        }
        if (cn.superName != null && !cn.superName.startsWith("java"))
        {
            addMethodVisited(classes, cn.superName, methodName, methodDesc);
        }
    }

    private final class ClassNodeGraph
    {
        ClassNode cn;
        Map<String, MethodNode> visited;

        public ClassNodeGraph(ClassNode cn)
        {
            this.cn = cn;
            this.visited = new HashMap<>();
        }
    }

}
