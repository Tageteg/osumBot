package com.osum.updater.impl.tree3.model;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.*;

import java.util.List;

public class ModelAnalyzer extends ClassAnalyzer
{

    public ModelAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("RenderableAccessor").name))
        {
            for (MethodNode method : (List<MethodNode>) node.methods)
            {
                if (method.desc.equals("(IIIIIII)V"))
                {
                    add("ModelAccessor", node);
                    return new ClassIdentity("Model", node.name, node, 0);
                }
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final MethodNode method : identity.getMethods())
        {
            if (method.desc.contains(")V") && new TypeQuerier(method.access).is(ACC_FINAL))
            {
                InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                List<AbstractInsnNode[]> nodes = matcher.match("getfield newarray putfield");
                String[] names = new String[]{"getXTriangles", "getXVertices", "getYVertices"};
                final int[] validIndex = new int[]{0, 3, 4, 5, 14, 15};
                if (nodes.size() >= 6)
                {
                    for (int index = 0; index < validIndex.length; index++)
                    {
                        AbstractInsnNode node = nodes.get(validIndex[index])[2];
                        if (node instanceof FieldInsnNode)
                        {
                            FieldInsnNode field = ((FieldInsnNode) node);
                            final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                            add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"RenderableAccessor"};
    }
}
