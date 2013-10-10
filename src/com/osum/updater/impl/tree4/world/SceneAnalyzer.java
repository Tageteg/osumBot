package com.osum.updater.impl.tree4.world;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.ClassResolver;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class SceneAnalyzer extends ClassAnalyzer
{

    public SceneAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        for (FieldNode field : (List<FieldNode>) node.fields)
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (field.desc.equals("[[[L" + getInstance().getAccessors().get("TileAccessor").name + ";"))
                {
                    add("SceneAccessor", node);
                    return new ClassIdentity("Scene", node.name, node, 0);
                }
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {

        int foundObjectTypes = 0;
        /*
        for (final MethodNode method : identity.getMethods())
        {
            if (method.desc.contains("L" + getInstance().getAccessors().get("TileAccessor").name + ";"))
            {
                final InstructionMatcher matcher = new InstructionMatcher(method.instructions);
                final List<AbstractInsnNode[]> nodes = matcher.match("iconst_0 getstatic getstatic getstatic getstatic aload getfield");
                final int[] validIndexes = new int[] { 2, 3, 4, 5 };
                if (nodes.size() > 0)
                {
                    if (foundObjectTypes < 4)
                    {
                        for (int i = 0; i < validIndexes.length; i++)
                        {
                            FieldInsnNode field = (FieldInsnNode) nodes.get(validIndexes[i])[6];
                            if (foundObjectTypes == 0)
                            {
                                add("WallAccessor", getInstance().getClasses().get(new ClassResolver(field.owner).resolve()));
                            } else if (foundObjectTypes == 1)
                            {
                                add("WallDecorationAccessor", getInstance().getClasses().get(new ClassResolver(field.owner).resolve()));
                            } else if (foundObjectTypes == 2)
                            {
                                add("FloorDecorationAccessor", getInstance().getClasses().get(new ClassResolver(field.owner).resolve()));
                            } else if (foundObjectTypes == 3)
                            {
                                add("GroundLayerAccessor", getInstance().getClasses().get(new ClassResolver(field.owner).resolve()));
                            }
                            foundObjectTypes++;
                        }
                    }
                }
            }
        }
        */

        for (final FieldNode field : identity.getFields())
        {
            TypeQuerier querier = new TypeQuerier(field.access);
            if (querier.isNot(ACC_STATIC))
            {
                if (field.desc.equals("[[[L" + getInstance().getAccessors().get("TileAccessor").name + ";"))
                {
                    add(new FieldIdentity("getTiles", "Tile", field.name, field.desc, identity.getClassName(), false));
                } else if (field.desc.contains(("L")))
                {
                    add(new FieldIdentity("getObjects", "InteractableObject", field.name, field.desc, identity.getClassName(), false));
                    add("InteractableObjectAccessor", getInstance().getClasses().get(new ClassResolver(field.desc).resolve()));
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"TileAccessor"};
    }
}
