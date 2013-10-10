package com.osum.updater.impl;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.List;

public class ClientAnalyzer extends ClassAnalyzer
{

    public ClientAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        return node.name.equals("client") || node.name.equals("Client") ? new ClassIdentity("Client", node.name, node, 0) : null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            final TypeQuerier querier = new TypeQuerier(field.access);
            if (field.desc.equals("[Ljava/awt/Rectangle;"))
            {
                add(new FieldIdentity("getComponentBounds", field.desc, field.name, field.desc, identity.getClassName(), false));
            } else if (field.desc.equals("[L" + getInstance().getAccessors().get("PlayerAccessor").name + ";"))
            {
                add(new FieldIdentity("getPlayerArray", "Player", field.name, field.desc, identity.getClassName(), false));
            } else if (field.desc.equals("[L" + getInstance().getAccessors().get("NPCAccessor").name + ";"))
            {
                add(new FieldIdentity("getNPCArray", "NPC", field.name, field.desc, identity.getClassName(), false));
            } else if (field.desc.equals("I") && querier.is(ACC_VOLATILE))
            {
                add(new FieldIdentity("getComponentIndex", field.desc, field.name, field.desc, identity.getClassName(), false, Multipliers.get(identity.getClassName(), field.name)));
            } else if (field.desc.equals("[[[L" + getInstance().getAccessors().get("NodeDequeAccessor").name + ";"))
            {
                add(new FieldIdentity("getGroundItems", "NodeDeque", field.name, field.desc, identity.getClassName(), false));
            } /* else if (field.desc.equals("L" + getInstance().getAccessors().get("LandscapeAccessor").name + ";"))
            {
                add(new FieldIdentity("getLandscape", field.desc, field.name, field.desc, identity.getClassName(), false));
            } else if (field.desc.equals("[L" + getInstance().getAccessors().get("NpcNodeAccessor").name + ";"))
            {
                add(new FieldIdentity("getNpcNodes", field.desc, field.name, field.desc, identity.getClassName(), false));
            }
            */
        }
        boolean foundCameraStuff = false;
        for (final MethodNode method : identity.getMethods())
        {
            final TypeQuerier querier = new TypeQuerier(method.access);
            if (method.desc.contains("IIIIIIII)V") && querier.is(ACC_STATIC) && querier.is(ACC_FINAL))
            {
                final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                final List<AbstractInsnNode[]> nodes = matcher.match("getstatic getstatic ldc imul");
                final String[] names = new String[]{"getWidgetBoundsXArray", "getWidgetBoundsYArray"};
                if (nodes.size() >= 2)
                {
                    for (int index = 0; index < 2; index++)
                    {
                        for (final AbstractInsnNode node : nodes.get(index))
                        {
                            if (node instanceof FieldInsnNode)
                            {
                                final FieldInsnNode field = (FieldInsnNode) node;
                                add(new FieldIdentity(names[index], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                            }
                        }
                    }
                }
            }
            if (method.desc.equals("()V") && querier.is(ACC_STATIC) && querier.is(ACC_FINAL))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                List<FieldInsnNode> fieldList = examiner.findAll(FieldInsnNode.class, PUTSTATIC);
                String[] names = new String[]{"isMenuOpen", "getMenuX", "getMenuY", "getMenuWidth", "getMenuHeight"};
                if (fieldList != null)
                {
                    FieldInsnNode[] fields = fieldList.toArray(new FieldInsnNode[fieldList.size()]);
                    if (fields.length == 5 && fields[fields.length - 5].desc.equals("Z"))
                    {
                        for (int i = 0; i < fields.length; i++)
                        {
                            FieldInsnNode field = fields[i];
                            add(new FieldIdentity(names[i], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                        }
                    }
                }
            }
            if (method.desc.equals("()V") && querier.is(ACC_STATIC) && querier.is(ACC_FINAL))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final List<FieldInsnNode> fieldList = examiner.findAll(FieldInsnNode.class, GETSTATIC);
                if (fieldList != null)
                {
                    FieldInsnNode[] fields = fieldList.toArray(new FieldInsnNode[fieldList.size()]);
                    boolean foundMouse = false;
                    boolean foundKeyboard = false;
                    for (int i = 0; i < fields.length; i++)
                    {
                        FieldInsnNode field = fields[i];
                        if (field.desc.equals("L" + getInstance().getAccessors().get("MouseAccessor").name + ";"))
                        {
                            foundMouse = true;
                        }
                        if (field.desc.equals("L" + getInstance().getAccessors().get("KeyboardAccessor").name + ";"))
                        {
                            foundKeyboard = true;
                        }
                    }
                    if (foundMouse && foundKeyboard)
                    {
                        final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                        final List<AbstractInsnNode[]> nodes = matcher.match("imul if_icmpne getstatic ifne getstatic");
                        final String[] names = new String[]{"getMenuActions", "getMenuTargets", "", "getMenuSize"};
                        if (nodes.size() > 0)
                        {
                            AbstractInsnNode current = nodes.get(0)[4];
                            int foundMenuStuff = 0;
                            while (foundMenuStuff < 4)
                            {
                                if (current instanceof FieldInsnNode)
                                {
                                    FieldInsnNode field = (FieldInsnNode) current;
                                    if (foundMenuStuff < 2 && !field.desc.equals("[Ljava/lang/String;"))
                                    {
                                        current = current.getNext();
                                        continue;
                                    }
                                    if (foundMenuStuff != 2)
                                    {
                                        add(new FieldIdentity(names[foundMenuStuff], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                                    }
                                    foundMenuStuff++;
                                }
                                current = current.getNext();
                            }
                        }
                    }
                }
            }
        }
        for (final MethodNode method : identity.getMethods())
        {
            final TypeQuerier querier = new TypeQuerier(method.access);
            if (method.desc.equals("(II)V") && querier.is(ACC_STATIC) && querier.is(ACC_FINAL))     // ()I
            {
                final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                final List<AbstractInsnNode[]> nodes = matcher.match(new int[]{GETSTATIC, LDC, GETSTATIC});
                if (nodes.size() >= 2)
                {
                    AbstractInsnNode node = nodes.get(0)[2];
                    if (node.getOpcode() == GETSTATIC)
                    {
                        final FieldInsnNode field = (FieldInsnNode) node;
                        add(new FieldIdentity("getPlane", field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                    }
                    break;
                }
            }
            if (method.desc.equals("()V") && querier.is(ACC_FINAL) && querier.is(ACC_PROTECTED))
            {
                {
                    final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                    final List<AbstractInsnNode[]> nodes = matcher.match("invokestatic goto bipush ldc getstatic"); // getstatic imul ifne ldc invokestatic");
                    if (nodes.size() > 0)
                    {
                        for (final AbstractInsnNode node : nodes.get(0))
                        {
                            if (node instanceof FieldInsnNode)
                            {
                                final FieldInsnNode field = (FieldInsnNode) node;
                                add(new FieldIdentity("getGameState", field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                            }
                        }
                    }
                }
            }
            if (method.desc.equals("(Z)V"))
            {
                {
                    final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                    final List<AbstractInsnNode[]> nodes = matcher.match("imul (if_icmpge|bipush) (ishr|goto) ldc getstatic");
                    final String[] names = new String[]{"getWalkingDestX", "getWalkingDestY"};
                    if (nodes.size() >= 2)
                    {
                        for (int index = 0; index < 2; index++)
                        {
                            for (final AbstractInsnNode node : nodes.get(index))
                            {
                                if (node instanceof FieldInsnNode)
                                {
                                    final FieldInsnNode field = (FieldInsnNode) node;
                                    add(new FieldIdentity(names[index], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                                }
                            }
                        }
                    }
                }
            }
            if (method.desc.equals("()V") && querier.is(ACC_STATIC) && querier.is((ACC_FINAL)))
            {
                {
                    final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                    final List<AbstractInsnNode[]> nodes = matcher.match("isub ldc imul putstatic");
                    final String[] names = new String[]{"getCompassAngle", "getMapOffset", "getMapScale"};
                    if (nodes.size() >= 4)
                    {
                        for (int index = 3; index < 5; index++)
                        {
                            for (final AbstractInsnNode node : nodes.get(index))
                            {
                                if (node instanceof FieldInsnNode)
                                {
                                    FieldInsnNode field = (FieldInsnNode) node;
                                    add(new FieldIdentity(names[(index - 3)], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                                    if (index == 4)
                                    {
                                        AbstractInsnNode currentNode = field.getNext();
                                        while (currentNode != null)
                                        {
                                            if (currentNode instanceof FieldInsnNode)
                                            {
                                                field = (FieldInsnNode) currentNode;
                                                add(new FieldIdentity(names[2], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                                                break;
                                            }
                                            currentNode = currentNode.getNext();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            int foundSkillStuff = 0;
            if (method.desc.equals("()V") && querier.is(ACC_STATIC) && (querier.is(ACC_FINAL) || querier.is(ACC_STATIC)))
            {
                {
                    final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                    final List<AbstractInsnNode[]> nodes = matcher.match("(ldc invokevirtual istore) getstatic iload");
                    final String[] names = new String[]{"getSkillLevels", "getSkillMaxLevels", "getSkillExperiences"};
                    if (nodes.size() >= 3)
                    {
                        for (int index = 0; index < 3; index++)
                        {
                            for (final AbstractInsnNode node : nodes.get(index))
                            {
                                if (node instanceof FieldInsnNode)
                                {
                                    FieldInsnNode field = (FieldInsnNode) node;
                                    if (!field.desc.equals("[I") || foundSkillStuff >= 2)
                                    {
                                       continue;
                                    }
                                    add(new FieldIdentity(names[index], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                                    AbstractInsnNode currentNode = field.getNext();
                                    while (currentNode != null && foundSkillStuff < 2)
                                    {
                                        if (currentNode instanceof FieldInsnNode)
                                        {
                                            field = (FieldInsnNode) currentNode;
                                            add(new FieldIdentity(names[1 + foundSkillStuff], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                                            foundSkillStuff++;
                                        }
                                        currentNode = currentNode.getNext();
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (method.desc.equals("(I)V") && querier.isNot(ACC_STATIC) && querier.is(ACC_PROTECTED) && querier.is(ACC_FINAL))
            {
                {
                    final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                    final List<AbstractInsnNode[]> nodes = matcher.match("imul ldc getstatic");
                    final String[] names = new String[]{"getBaseX", "getBaseY"};
                    if (nodes.size() >= 2)
                    {
                        for (int index = 0; index < 2; index++)
                        {
                            for (final AbstractInsnNode node : nodes.get(index))
                            {
                                if (node instanceof FieldInsnNode)
                                {
                                    final FieldInsnNode field = (FieldInsnNode) node;
                                    add(new FieldIdentity(names[index], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                                }
                            }
                        }
                    }
                }
            } else if (method.desc.contains("III)V") && querier.is(ACC_STATIC) && querier.is(ACC_FINAL) && !foundCameraStuff)
            {
                final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                final List<AbstractInsnNode[]> nodes = matcher.match("isub (ldc imul|imul) putstatic");
                final String[] names = new String[]{"getCameraX", "getCameraZ", "getCameraY", "getCameraYaw", "getCameraPitch"};
                if (nodes.size() >= 3)
                {
                    for (int index = 0; index < 3; index++)
                    {
                        for (final AbstractInsnNode node : nodes.get(index))
                        {
                            if (node instanceof FieldInsnNode)
                            {
                                final FieldInsnNode field = (FieldInsnNode) node;
                                add(new FieldIdentity(names[index], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                            }
                        }
                    }
                    foundCameraStuff = true;
                }
                int foundPitchYaw = 0;
                AbstractInsnNode currentNode = method.instructions.getLast();
                while (currentNode != null && foundPitchYaw < 2 && foundCameraStuff)
                {
                    if (currentNode instanceof FieldInsnNode)
                    {
                        FieldInsnNode field = (FieldInsnNode) currentNode;
                        add(new FieldIdentity(names[3 + foundPitchYaw], field.desc, field.name, field.desc, field.owner, false, Multipliers.get(field.owner, field.name)));
                        foundPitchYaw++;
                    }
                    currentNode = currentNode.getPrevious();
                }
            }
            method.accept(new MethodVisitor(ASM4)
            {

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc)
                {
                    ClassNode classNode = getInstance().getClasses().get(owner);
                    if (classNode == null || !classNode.superName.equals("java/lang/Object"))
                    {
                        return;
                    }
                    for (FieldNode field : (List<FieldNode>) classNode.fields)
                    {
                        TypeQuerier querier = new TypeQuerier(field.access);
                        if (field.desc.equals("Ljava/io/File;") && querier.is(ACC_PUBLIC) && querier.is(ACC_STATIC))
                            for (MethodNode method : (List<MethodNode>) classNode.methods)
                            {
                                querier = new TypeQuerier(method.access);
                                if (method.desc.contains("String") && method.desc.contains(")V") && method.desc.contains("B") && querier.is(ACC_STATIC) && querier.is(ACC_FINAL))
                                {
                                    final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                                    final List<AbstractInsnNode[]> nodes = matcher.match("getstatic iload getstatic");
                                    final String[] names = new String[]{"getChatMessages", "getChatNames"};
                                    if (nodes.size() >= 2)
                                    {
                                        for (int index = 1; index < 3; index++)
                                        {
                                            for (final AbstractInsnNode node : nodes.get(index))
                                            {
                                                if (node instanceof FieldInsnNode)
                                                {
                                                    final FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
                                                    add(new FieldIdentity(names[(index - 1)], fieldInsnNode.desc, fieldInsnNode.name, fieldInsnNode.desc, fieldInsnNode.owner, false));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }

                @Override
                public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc)
                {
                    if (desc.equals("L" + getInstance().getAccessors().get("PlayerAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getLocalPlayer", "Player", name, desc, owner, false));
                    } else if (desc.equals("L" + getInstance().getAccessors().get("SceneAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getCurrentScene", "Scene", name, desc, owner, false));
                    } else if (desc.equals("[[L" + getInstance().getAccessors().get("WidgetAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getWidgets", "Widget", name, desc, owner, false));
                    } else if (desc.equals("L" + getInstance().getAccessors().get("MouseAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getMouse", "Mouse", name, desc, owner, false));
                    } else if (desc.equals("L" + getInstance().getAccessors().get("KeyboardAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getKeyboard", "Keyboard", name, desc, owner, false));
                    }
                    /* else if (getInstance().getAccessors().get("FacadeAccessor") != null && desc.equals("L" + getInstance().getAccessors().get("FacadeAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getFacade", name, desc, owner, false));
                    } else if (desc.equals("L" + getInstance().getAccessors().get("RenderAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getRender", name, desc, owner, false));
                    } else if (desc.equals("L" + getInstance().getAccessors().get("ItemLoaderAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getItemLoader", name, desc, owner, false));
                    } else if (desc.equals("L" + getInstance().getAccessors().get("MouseAbstractAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getMouse", name, desc, owner, false));
                    } else if (desc.equals("L" + getInstance().getAccessors().get("KeyboardAbstractAccessor").name + ";"))
                    {
                        add(new FieldIdentity("getKeyboard", name, desc, owner, false));
                    }*/
                }
            });

        }



        for (MethodNode method : (List<MethodNode>) getInstance().getAccessors().get("RegionAccessor").methods)
        {
            if (method.name.equals("<clinit>"))
            {
                final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                final List<AbstractInsnNode[]> nodes = matcher.match(new int[]{MULTIANEWARRAY, PUTSTATIC});
                final String[] names = new String[]{"getTileHeightMap", "getSceneFlags"};
                if (nodes.size() > 0)
                {
                    for (int index = 0; index < 2; index++)
                    {
                        for (final AbstractInsnNode node : nodes.get(index))
                        {
                            if (node instanceof FieldInsnNode)
                            {
                                final FieldInsnNode field = ((FieldInsnNode) node);
                                final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                                add(new FieldIdentity(names[index], field.desc, field.name, field.desc, field.owner, false, multiplier));
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
        return new String[]{
                "PlayerAccessor", "NPCAccessor",
                "SceneAccessor", "RegionAccessor", "WidgetAccessor",
                "MouseAccessor", "KeyboardAccessor", "NodeDequeAccessor"};
    }
}
