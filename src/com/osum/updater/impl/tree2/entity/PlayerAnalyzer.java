package com.osum.updater.impl.tree2.entity;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.field.FieldExaminer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.ClassResolver;
import com.osum.updater.util.bytecode.TypeQuerier;
import com.osum.updater.util.bytecode.WildCardMatcher;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;

public class PlayerAnalyzer extends ClassAnalyzer
{

    public PlayerAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("ActorAccessor").name))
        {
            final Map<String, Integer> map = new FieldExaminer(node).getMap();
            if (map.get("Boolean") > 0)
            {
                add("PlayerAccessor", node);
                return new ClassIdentity("Player", node.name, node, 0);
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
                if (field.desc.equals("Ljava/lang/String;"))
                {
                    add(new FieldIdentity("getName", field.desc, field.name, field.desc, identity.getClassName(), false));
                } else if (field.desc.equals("L" + getInstance().getAccessors().get("ModelAccessor").name + ";"))
                {
                    add(new FieldIdentity("getModel", "Model", field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            final TypeQuerier querier = new TypeQuerier(method.access);
            if (querier.isNot(ACC_STATIC))
            {
                if (querier.is(ACC_FINAL) && new WildCardMatcher(method, "(?)Z").match())
                {
                    final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                    final FieldInsnNode field = examiner.next(FieldInsnNode.class, GETFIELD);
                    if (field != null)
                    {
                        add(new FieldIdentity("getDefinition", field.desc, field.name, field.desc, identity.getClassName(), false));
                        add("PlayerDefinitionAccessor", getInstance().getClasses().get(new ClassResolver(field.desc).resolve()));
                    }
                } else if (method.name.equals("<init>"))
                {
                    final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                    List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTFIELD);
                    final String[] names = new String[]{
                            "getSkullIcon", "getHeadIcon", "getTotalLevel", "getCombatLevel", "isSpotAnimating"
                    };
                    final int[] validIndex = new int[]{1, 3, 4, 5, 6};
                    if (fields.size() >= 5)
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
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"ActorAccessor", "ModelAccessor"};
    }
}
