package com.osum.updater.impl.tree4.objects;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class GameObjectDefinitionAnalyzer extends ClassAnalyzer
{

    public GameObjectDefinitionAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.superName.equals(getInstance().getAccessors().get("CacheableNodeAccessor").name))
        {
            for (MethodNode method : (List<MethodNode>) node.methods)
            {
                if (new TypeQuerier(method.access).isNot(ACC_STATIC))
                {
                    if (method.desc.contains("[[I"))
                    {
                        add("GameObjectDefinitionAccessor", node);
                        return new ClassIdentity("GameObjectDefinition", node.name, node, 0);
                    }
                }
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
                if (field.desc.equals("Ljava/lang/String;"))
                {
                    add(new FieldIdentity("getName", field.desc, field.name, field.desc, identity.getClassName(), false));
                } else if (field.desc.equals("[Ljava/lang/String;"))
                {
                    add(new FieldIdentity("getActions", field.desc, field.name, field.desc, identity.getClassName(), false));
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
