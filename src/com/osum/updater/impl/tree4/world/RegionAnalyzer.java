package com.osum.updater.impl.tree4.world;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionMatcher;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.*;

import java.util.List;

public class RegionAnalyzer extends ClassAnalyzer
{

    public RegionAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals("java/lang/Object"))
        {
            boolean found3DIntArray = false, found3DByteArray = false;
            for (FieldNode field : (List<FieldNode>) node.fields)
            {
                if (new TypeQuerier(field.access).is(ACC_STATIC))
                {
                    if (field.desc.equals("[[[I"))
                    {
                        found3DIntArray = true;
                    } else if (field.desc.equals("[[[B"))
                    {
                        found3DByteArray = true;
                    }
                }
            }
            if (found3DIntArray && found3DByteArray)
            {
                add("RegionAccessor", node);
                return new ClassIdentity("Region", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {

        for (final MethodNode method : identity.getMethods())
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
                                add(new FieldIdentity(names[index], field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
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
        return null;
    }
}
