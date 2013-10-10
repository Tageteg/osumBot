package com.osum.updater.impl.tree4.objects;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.ClassResolver;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.*;

import java.util.List;

public class GameObjectAnalyzer extends ClassAnalyzer
{

    public GameObjectAnalyzer(Analyzer instance)
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
                if (method.name.equals("<init>"))
                {
                    if (method.desc.contains("" + getInstance().getAccessors().get("RenderableAccessor").name + ""))
                    {
                        add("GameObjectAccessor", node);
                        return new ClassIdentity("GameObject", node.name, node, 0);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            if (field.desc.equals("L" + getInstance().getAccessors().get("RenderableAccessor").name + ";"))
            {
                add(new FieldIdentity("getRenderable", "Renderable", field.name, field.desc, identity.getClassName(), false));
            }
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                /*
                if (field.desc.equals("I"))
                {
                    final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                    add(new FieldIdentity("getId", field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                }
                */
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            if (method.name.equals("<init>"))
            {
                final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                final List<AbstractInsnNode[]> nodes = matcher.match(new int[]{ALOAD, ALOAD, PUTFIELD});
                if (nodes.size() > 0)
                {
                    for (final AbstractInsnNode node : nodes.get(0))
                    {
                        if (node instanceof FieldInsnNode)
                        {
                            final FieldInsnNode field = (FieldInsnNode) node;
                            add("GameObjectDefinitionAccessor", getInstance().getClasses().get(new ClassResolver(field.desc).resolve()));
                            add(new FieldIdentity("getDefinition", "GameObjectDefinition", field.name, field.desc, identity.getClassName(), false));
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
