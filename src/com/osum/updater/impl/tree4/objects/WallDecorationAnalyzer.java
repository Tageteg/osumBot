package com.osum.updater.impl.tree4.objects;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import org.objectweb.asm.tree.*;

import java.util.List;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 8:00 AM
 */
public class WallDecorationAnalyzer extends ClassAnalyzer
{

    public WallDecorationAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.name.equals(getInstance().getAccessors().get("WallDecorationAccessor").name))
        {
            return new ClassIdentity("WallDecoration", node.name, node, 0);
        }
        return null;
    }
    @Override
    public void analyse(ClassIdentity identity)
    {
        for (ClassNode classNode : getInstance().getClasses().values())
        {
            if (classNode.name.equals(getInstance().getAccessors().get("SceneAccessor").name))
            {
                for (MethodNode method : (List<MethodNode>) classNode.methods)
                {
                    if (method.desc.contains("L" + getInstance().getAccessors().get("RenderableAccessor").name + ";"))
                    {
                        final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                        final List<AbstractInsnNode[]> nodes = matcher.match("imul ldc iadd putfield");
                        final String[] names = new String[]{"getX", "getY"};
                        if (nodes.size() >= 2)
                        {
                            for (int i = 0; i < 2; i++)
                            {
                                FieldInsnNode field = (FieldInsnNode) nodes.get(i)[3];
                                if (field.owner.equals(identity.getClassName()))
                                {
                                    add(new FieldIdentity(names[i], field.desc, field.name, field.desc, identity.getClassName(), false, Multipliers.get(identity.getClassName(), field.name)));
                                }
                            }
                        }
                    }
                }
            }
        }
        for (final FieldNode field : identity.getFields())
        {
            if (field.desc.equals("L" + getInstance().getAccessors().get("RenderableAccessor").name + ";"))
            {
                add(new FieldIdentity("getRenderable", "Renderable", field.name, field.desc, identity.getClassName(), false));
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            if (method.name.equals("<init>"))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTFIELD);
                String[] names = new String[]{"getFlags", "getId"};
                if (fields.size() > 0)
                {
                    for (int i = 0; i < 2; i++)
                    {
                        final FieldInsnNode field = fields.get(i);
                        add(new FieldIdentity(names[i], field.desc, field.name, field.desc, identity.getClassName(), false, Multipliers.get(field.owner, field.name)));
                    }
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[] {"WallDecorationAccessor"};
    }

}
