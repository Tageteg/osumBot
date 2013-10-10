package com.osum.updater.impl.tree2.entity;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import com.osum.updater.util.bytecode.WildCardMatcher;
import org.objectweb.asm.tree.*;

import java.util.List;

public class ActorAnalyzer extends ClassAnalyzer
{

    public ActorAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (new TypeQuerier(node.access).is(ACC_ABSTRACT) && node.superName.equals(getInstance().getAccessors().get("RenderableAccessor").name))
        {
            add("ActorAccessor", node);
            return new ClassIdentity("Actor", node.name, node, 0);
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final MethodNode method : (List<MethodNode>) getInstance().getClasses().get("client").methods)
        {
            TypeQuerier querier = new TypeQuerier(method.access);
            if (method.desc.contains("L" + getInstance().getAccessors().get("ActorAccessor").name + ";") && querier.is(ACC_STATIC) && querier.is(ACC_FINAL))
            {
                final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                final List<AbstractInsnNode[]> nodes = matcher.match("(dmul d2i ldc iand ldc|dmul d2i sipush) iand ldc imul putfield");
                //final List<AbstractInsnNode[]> nodes = matcher.match(new int[]{DMUL, D2I, LDC, IAND, LDC, IMUL, PUTFIELD});
                if (nodes.size() >= 1)
                {
                    AbstractInsnNode node = nodes.get(0)[6];
                    if (node.getOpcode() == PUTFIELD)
                    {
                        final FieldInsnNode field = (FieldInsnNode) node;
                        if (field.owner.equals(identity.getClassName()))
                        {
                            add(new FieldIdentity("getOrientation", field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                        }
                    }
                    break;
                }
            }
        }
        for (final FieldNode field : identity.getFields())
        {
            final TypeQuerier querier = new TypeQuerier(field.access);
            if (querier.isNot(ACC_STATIC))
            {
                if (querier.isNot(ACC_STATIC) && field.desc.equals("Z"))
                {
                    add(new FieldIdentity("isAnimating", field.desc, field.name, field.desc, identity.getClassName(), false));
                } else if (querier.isNot(ACC_STATIC) && field.desc.equals("Ljava/lang/String;"))
                {
                    add(new FieldIdentity("getTextSpoken", field.desc, field.name, field.desc, identity.getClassName(), false));
                }


                /* else if (field.desc.equals("L" + getInstance().getAccessors().get("LinkedListAccessor").name + ";"))
                {
                    add(new FieldIdentity("getCombatList", field.desc, field.name, field.desc, identity.getClassName(), false));
                } else if (field.desc.equals("L" + getInstance().getAccessors().get("AnimatorAccessor").name + ";"))
                {
                    add(new FieldIdentity("getAnimator", field.desc, field.name, field.desc, identity.getClassName(), false));
                }       */
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            if (method.name.equals("<init>"))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTFIELD);
                final String[] names = new String[]{
                        "getStandAnimation", "getInteracting", "getAnimation"
                };
                final int[] validIndex = new int[]{15, 17, 41};
                if (fields.size() >= 40)
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
            final TypeQuerier querier = new TypeQuerier(method.access);
            if (querier.is(ACC_FINAL))
            {
                if (new WildCardMatcher(method, "(??Z?)V").match())
                {
                    InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                    List<AbstractInsnNode[]> nodes = matcher.match(new int[]{IMUL, IADD, PUTFIELD});
                    String[] names = new String[]{"getY", "getX"};
                    if (nodes.size() > 0)
                    {
                        for (int index = 0; index < 2; index++)
                        {
                            for (final AbstractInsnNode node : nodes.get(index))
                            {
                                if (node instanceof FieldInsnNode)
                                {
                                    FieldInsnNode field = ((FieldInsnNode) node);
                                    final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                                    add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                                }
                            }
                        }
                    }
                    matcher = new InstructionMatcher(method.instructions);
                    nodes = matcher.match(new int[]{ALOAD, ICONST_0, PUTFIELD});
                    names = new String[]{"getWalkingQueueXPos", "getWalkingQueueYPos"};
                    if (nodes.size() >= 3)
                    {
                        for (int index = 1; index < 3; index++)
                        {
                            for (final AbstractInsnNode node : nodes.get(index))
                            {
                                if (node instanceof FieldInsnNode)
                                {
                                    FieldInsnNode field = ((FieldInsnNode) node);
                                    final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                                    add(new FieldIdentity(names[(index - 1)], field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                                }
                            }
                        }
                    }
                    matcher = new InstructionMatcher(method.instructions);
                    nodes = matcher.match("imul (aload|ldc aload) getfield");
                    names = new String[]{"getWalkingQueueX", "getWalkingQueueY"};
                    if (nodes.size() >= 2)
                    {
                        for (int index = 0; index < 2; index++)
                        {
                            for (final AbstractInsnNode node : nodes.get(index))
                            {
                                if (node instanceof FieldInsnNode)
                                {
                                    FieldInsnNode field = ((FieldInsnNode) node);
                                    add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false));
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
        return new String[]{"RenderableAccessor", "ModelAccessor"};
    }
}
