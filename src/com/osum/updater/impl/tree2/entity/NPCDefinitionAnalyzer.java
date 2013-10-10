package com.osum.updater.impl.tree2.entity;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;

public class NPCDefinitionAnalyzer extends ClassAnalyzer
{

    public NPCDefinitionAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        short boolCount = 0;
        if (node.superName.equals(getInstance().getAccessors().get("CacheableNodeAccessor").name))
        {
            for (final FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).isNot(Opcodes.ACC_STATIC))
                {
                    if (field.desc.equals("Z"))
                    {
                        boolCount++;
                    }
                }
            }
            if (boolCount == 4)
            {
                add("NPCDefinitionAccessor", node);
                return new ClassIdentity("NPCDefinition", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            final TypeQuerier querier = new TypeQuerier(field.access);
            if (querier.isNot(ACC_STATIC))
            {
                if (field.desc.equals("[Ljava/lang/String;"))
                {
                    add(new FieldIdentity("getActions", field.desc, field.name, field.desc, identity.getClassName(), false));
                }
                if (field.desc.equals("Ljava/lang/String;"))
                {
                    add(new FieldIdentity("getName", field.desc, field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            if (method.desc.equals("(I)L" + identity.getClassName() + ";"))
            {
                InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                List<AbstractInsnNode[]> nodes = matcher.match(new int[]{ILOAD, IMUL, PUTFIELD});
                if (nodes.size() > 0)
                {
                    AbstractInsnNode node = nodes.get(0)[2];
                    if (node instanceof FieldInsnNode)
                    {
                        FieldInsnNode field = ((FieldInsnNode) node);
                        final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                        add(new FieldIdentity("getId", field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                    }
                }
            }
            if (method.name.equals("<init>"))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTFIELD);
                final String[] names = new String[]{
                        "getHeadIcon", "getActions", "isClickable"
                };
                final int[] validIndex = new int[]{0, 4, 21};
                if (fields.size() >= 15)
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
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"CacheableNodeAccessor"};
    }
}
