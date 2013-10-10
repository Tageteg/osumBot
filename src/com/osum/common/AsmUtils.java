package com.osum.common;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.ListIterator;

/**
 * User: Marty
 * Date: 3/28/13
 * Time: 9:59 PM
 */
public class AsmUtils
{

    public static FieldNode getField(final ClassNode cn, final FieldInsnNode fin)
    {
        for (final FieldNode fn : (List<FieldNode>) cn.fields)
        {
            if (!fn.name.equals(fin.name))
            {
                continue;
            }
            return fn;
        }
        return null;
    }

    public static boolean isStatic(ClassNode classNode, String fieldName)
    {
        for (FieldNode fieldNode : (List<FieldNode>) classNode.fields)
        {
            if (fieldNode.name.equals(fieldName))
            {
                if ((fieldNode.access & Opcodes.ACC_STATIC) != 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void addInterface(ClassNode classNode, String interfaceName)
    {
        classNode.interfaces.add(interfaceName);
    }

    public static void renameMethod(ClassNode classNode, String name, String newName)
    {
        ListIterator<?> mli = classNode.methods.listIterator();
        while (mli.hasNext())
        {
            MethodNode mn = (MethodNode) mli.next();
            if(mn.name.equals(name)) {
                mn.name = newName;
            }
        }
    }

    public static void setSuper(ClassNode classNode, final String superClass, final String newSuperClass)
    {
        ListIterator<?> mli = classNode.methods.listIterator();
        while (mli.hasNext())
        {
            MethodNode mn = (MethodNode) mli.next();
            ListIterator<?> ili = mn.instructions.iterator();
            while (ili.hasNext())
            {
                AbstractInsnNode ain = (AbstractInsnNode) ili.next();
                if (ain.getOpcode() == Opcodes.INVOKESPECIAL)
                {
                    MethodInsnNode min = (MethodInsnNode) ain;
                    if (min.owner.equals(superClass))
                    {
                        min.owner = newSuperClass;
                        break;
                    }
                }
            }
        }
        classNode.superName = newSuperClass;
    }


    public static void addGetter(ClassNode classNode, ClassNode targetClassNode, String methodName, String methodType, String fieldName, String fieldType, int multiplier)
    {
        MethodNode methodNode = new MethodNode(Opcodes.ACC_PUBLIC, methodName, methodType, null, null);
        methodNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        methodNode.visitFieldInsn((isStatic(classNode, fieldName) == true) ? Opcodes.GETSTATIC : Opcodes.GETFIELD, classNode.name, fieldName, fieldType);
        int returnOpcode = Opcodes.ARETURN;
        if (fieldType.equals("I") || fieldType.equals("Z"))
        {
            returnOpcode = Opcodes.IRETURN;
        }
        if (fieldType.equals("J") || fieldType.equals("S"))
        {
            returnOpcode = Opcodes.LRETURN;
        }
        if (multiplier != -1)
        {
            methodNode.instructions.add(new LdcInsnNode(multiplier));
            methodNode.instructions.add(new InsnNode(Opcodes.IMUL));
        }
        methodNode.visitInsn(returnOpcode);
        methodNode.visitMaxs(1, 1);
        methodNode.visitEnd();
        targetClassNode.methods.add(methodNode);
    }

    public static void addGetter(ClassNode classNode, String fieldName, ClassNode fieldOwner, String fieldDesc, String methodName, String methodDesc, int multiplier)
    {
        MethodNode methodNode = new MethodNode(Opcodes.ACC_PUBLIC, methodName, methodDesc, null, null);
        methodNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        methodNode.visitFieldInsn((isStatic(fieldOwner, fieldName) == true) ? Opcodes.GETSTATIC : Opcodes.GETFIELD, fieldOwner.name, fieldName, fieldDesc);
        int returnOpcode = Opcodes.ARETURN;
        if (fieldDesc.equals("I") || fieldDesc.equals("Z"))
        {
            returnOpcode = Opcodes.IRETURN;
        }
        if (fieldDesc.equals("J") || fieldDesc.equals("S"))
        {
            returnOpcode = Opcodes.LRETURN;
        }
        if (multiplier != -1)
        {
            methodNode.instructions.add(new LdcInsnNode(multiplier));
            methodNode.instructions.add(new InsnNode(Opcodes.IMUL));
        }
        methodNode.visitInsn(returnOpcode);
        methodNode.visitMaxs(1, 1);
        methodNode.visitEnd();
        classNode.methods.add(methodNode);
    }

}
