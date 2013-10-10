package com.osum.updater.impl.tree5.component;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.*;

import java.util.List;

public class WidgetAnalyzer extends ClassAnalyzer
{

    public WidgetAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        short selfArrayCount = 0, self2DArrayCount = 0;
        if (node.superName.equals(getInstance().getAccessors().get("NodeAccessor").name))
        {
            for (final FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).isNot(ACC_STATIC))
                {
                    if (field.desc.equals("[L" + node.name + ";"))
                    {
                        selfArrayCount++;
                    }
                } else
                {

                    if (field.desc.equals("[[L" + node.name + ";"))
                    {
                        self2DArrayCount++;
                    }
                }
            }
            if (selfArrayCount == 1 && self2DArrayCount == 1)
            {
                add("WidgetAccessor", node);
                return new ClassIdentity("Widget", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final MethodNode method : identity.getMethods())
        {
            if (method.desc.equals("(II)V") && new TypeQuerier(method.access).isNot(ACC_STATIC))
            {
                final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                final List<AbstractInsnNode[]> nodes = matcher.match("(return|iastore) aload getfield");
                final String[] names = new String[]{"getInventory", "getInventoryStackSizes"};
                if (nodes.size() >= 2)
                {
                    for (int i = 0; i < 2; i++)
                    {
                        for (AbstractInsnNode node : nodes.get(i))
                        {
                            if (node instanceof FieldInsnNode)
                            {
                                FieldInsnNode field = (FieldInsnNode) node;
                                add(new FieldIdentity(names[i], field.desc, field.name, field.desc, identity.getClassName(), false));
                            }
                        }
                    }
                }
            }
            if (method.name.equals("<init>"))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTFIELD);
                final String[] names = new String[]{
                        "getModelId", "getActionType", "getRelativeY", "getX", "getY", "getWidth", "getHeight",
                        "getRoot", "getRelativeX", "getParentId", "getContentType", "getTooltip", "getText", "getSpellName",
                        "getBoundsArrayIndex", "getId", "getSelectedAction"
                };
                int num = 0;
                for(FieldInsnNode name : fields) {
                    //System.out.println(num++ + " " + name.name);
                }
                final int[] validIndex = new int[]{2, 3, 6, 7, 8, 9, 10, 11, 18, 22, 31, 32, 33, 42, 44, 51, 60};
                if (fields.size() >= 69)
                {
                    for (int index = 0; index < validIndex.length; index++)
                    {
                        final FieldInsnNode field = fields.get(validIndex[index]);
                        if (field != null)
                        {
                            if (field.desc.equals("I"))
                            {
                                final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                                add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                                continue;
                            }
                            add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false));
                        }
                    }
                }
            }
        }
        for (final FieldNode field : identity.getFields())
        {
            final TypeQuerier querier = new TypeQuerier(field.access);
            if (querier.isNot(ACC_STATIC) && field.desc.equals("[L" + identity.getClassName() + ";"))
            {
                add(new FieldIdentity("getComponents", field.desc, field.name, field.desc, identity.getClassName(), false));
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"NodeAccessor"};
    }

}
