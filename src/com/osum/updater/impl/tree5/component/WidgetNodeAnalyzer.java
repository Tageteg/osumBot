package com.osum.updater.impl.tree5.component;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.*;

import java.util.List;

public class WidgetNodeAnalyzer extends ClassAnalyzer
{

    public WidgetNodeAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        short byteArrayCount = 0;
        if (node.superName.equals(getInstance().getAccessors().get("NodeAccessor").name))
        {
            for (final FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).is(ACC_STATIC))
                {
                    if (field.desc.equals("[[[B"))
                    {
                        byteArrayCount++;
                    }
                }
            }
            if (byteArrayCount == 1)
            {
                add("WidgetNodeAccessor", node);
                return new ClassIdentity("WidgetNode", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (ClassNode classNode : getInstance().getClasses().values())
        {
            if (classNode.name.equals("client"))
            {
                for (final MethodNode method : (List<MethodNode>) classNode.methods)
                {
                    TypeQuerier querier = new TypeQuerier(method.access);
                    if (method.desc.contains("L" + identity.getClassName() + ";") && method.desc.contains(")V") && querier.is(ACC_STATIC) && querier.is(ACC_FINAL))
                    {
                        {
                            final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                            final List<AbstractInsnNode[]> nodes = matcher.match("ldc aload getfield");
                            if (nodes.size() >= 1)
                            {
                                for (final AbstractInsnNode node : nodes.get(0))
                                {
                                    if (node instanceof FieldInsnNode)
                                    {
                                        final FieldInsnNode field = (FieldInsnNode) node;
                                        add(new FieldIdentity("getID", field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"NodeAccessor"};
    }

}
