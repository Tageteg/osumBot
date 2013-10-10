package com.osum.updater.impl.tree4.world;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class GroundItemAnalyzer extends ClassAnalyzer
{

    public GroundItemAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("RenderableAccessor").name))
        {
            boolean foundWidget = false;
            boolean found3DIntArray = false;
            for (FieldNode field : (List<FieldNode>) node.fields)
            {
                if (field.desc.equals("L" + getInstance().getAccessors().get("WidgetAccessor").name + ";"))
                {
                    foundWidget = true;
                }
                if (field.desc.equals("[[[I"))
                {
                    found3DIntArray = true;
                }
            }
            if (foundWidget/* && found3DIntArray*/)
            {
                add("GroundItemAccessor", node);
                return new ClassIdentity("GroundItem", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (MethodNode method : identity.getMethods())
        {
            if (method.name.equals("<init>"))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTFIELD);
                final String[] names = new String[]{
                        "getPlane", "isNonStackable", "getX", "getDuration", "getId", "getY"
                };
                final int[] validIndex = new int[]{1, 2, 3, 5, 7, 10};
                if (fields != null && fields.size() >= 10)
                {
                    for (int index = 0; index < validIndex.length; index++)
                    {
                        final FieldInsnNode field = fields.get(validIndex[index]);
                        if (field != null)
                        {
                            if (field.desc.equals("I"))
                            {
                                //final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                                final int multiplier = -1;
                                add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                                continue;
                            }
                            add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"RenderableAccessor", "WidgetAccessor"};
    }
}
