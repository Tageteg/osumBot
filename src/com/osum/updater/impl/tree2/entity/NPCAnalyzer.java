package com.osum.updater.impl.tree2.entity;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.field.FieldExaminer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class NPCAnalyzer extends ClassAnalyzer
{

    public NPCAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("ActorAccessor").name))
        {
            final Map<String, Integer> map = new FieldExaminer(node).getMap();
            if (map.get("Boolean") == 0)
            {
                add("NPCAccessor", node);
                return new ClassIdentity("NPC", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final FieldNode field : identity.getFields())
        {
            if (new TypeQuerier(field.access).isNot(ACC_STATIC))
            {
                if (field.desc.equals("L" + getInstance().getAccessors().get("NPCDefinitionAccessor").name + ";"))
                {
                    add(new FieldIdentity("getNPCDefinition", "NPCDefinition", field.name, field.desc, identity.getClassName(), false));
                }
            }
        }
    }

    @Override
    public String[] accessors()
    {
        return new String[]{"NPCDefinitionAccessor"};
    }
}
