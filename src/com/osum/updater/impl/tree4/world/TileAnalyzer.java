package com.osum.updater.impl.tree4.world;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.ClassResolver;
import org.objectweb.asm.tree.*;

import java.util.List;

public class TileAnalyzer extends ClassAnalyzer {

    public TileAnalyzer(Analyzer instance) {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node) {
        if (node.superName.equals(getInstance().getAccessors().get("NodeAccessor").name)) {
            for (MethodNode method : (List<MethodNode>) node.methods) {
                if (method.name.equals("<init>") && method.desc.equals("(III)V")) {
                    add("TileAccessor", node);
                    return new ClassIdentity("Tile", node.name, node, 0);
                }
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity) {
        int foundObjectTypes = 0;
        for (final ClassNode classNode : getInstance().getClasses().values()) {
            for (final MethodNode method : (List<MethodNode>) classNode.methods) {
                if (method.desc.contains("L" + getInstance().getAccessors().get("TileAccessor").name + ";")) {
                    {
                        final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                        final List<AbstractInsnNode[]> nodes = matcher.match("iconst_0 getstatic getstatic getstatic getstatic aload getfield");
                        final int[] validIndexes = new int[] { 2, 3, 4, 5 };
                        if (nodes.size() > 0) {
                            if (foundObjectTypes < 4) {
                                for (int i = 0; i < validIndexes.length; i++) {
                                    FieldInsnNode field = (FieldInsnNode) nodes.get(validIndexes[i])[6];
                                    if (foundObjectTypes == 0) {
                                        add("WallAccessor", getInstance().getClasses().get(new ClassResolver(field.owner).resolve()));
                                    } else if (foundObjectTypes == 1) {
                                        add("WallDecorationAccessor", getInstance().getClasses().get(new ClassResolver(field.owner).resolve()));
                                    } else if (foundObjectTypes == 2) {
                                        add("FloorDecorationAccessor", getInstance().getClasses().get(new ClassResolver(field.owner).resolve()));
                                    } else if (foundObjectTypes == 3) {
                                        add("GroundLayerAccessor", getInstance().getClasses().get(new ClassResolver(field.owner).resolve()));
                                    }
                                    foundObjectTypes++;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (final FieldNode field : identity.getFields()) {
            if (field.desc.equals("L" + getInstance().getAccessors().get("WallAccessor").name + ";")) {
                add(new FieldIdentity("getWall", "Wall", field.name, field.desc, identity.getClassName(), false));
            } else if (field.desc.equals("L" + getInstance().getAccessors().get("WallDecorationAccessor").name + ";")) {
                add(new FieldIdentity("getWallDecoration", "WallDecoration", field.name, field.desc, identity.getClassName(), false));
            } else if (field.desc.equals("L" + getInstance().getAccessors().get("FloorDecorationAccessor").name + ";")) {
                add(new FieldIdentity("getFloorDecoration", "FloorDecoration", field.name, field.desc, identity.getClassName(), false));
            } else if (field.desc.equals("L" + getInstance().getAccessors().get("GroundLayerAccessor").name + ";")) {
                add(new FieldIdentity("getGroundLayer", "GroundLayer", field.name, field.desc, identity.getClassName(), false));
            }
        }
        for (final MethodNode method : identity.getMethods()) {
            if (method.name.equals("<init>")) {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTFIELD);
                if (fields.size() > 0) {
                    for (FieldInsnNode field : fields) {
                        if (field.desc.contains("L")) {
                            add(new FieldIdentity("getObjects", "InteractableObject", field.name, field.desc, identity.getClassName(), false));
                        }
                    }
                }
                /*
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTFIELD);
                String[] names = new String[]{"getY", "getHeight", "getX", "getObjects"};
                int[] validIndexes = new int[]{0, 3, 4, 6};
                if (fields.size() > 0)
                {
                    for (int i = 0; i < validIndexes.length; i++)
                    {
                        final FieldInsnNode field = fields.get(validIndexes[i]);
                        add(new FieldIdentity(names[i], (validIndexes[i] != 6) ? field.desc : "InteractableObject", field.name, field.desc, identity.getClassName(), false));
                    }
                }
                */
            }
        }
    }

    @Override
    public String[] accessors() {
        return new String[] { "NodeAccessor" };
    }
}
