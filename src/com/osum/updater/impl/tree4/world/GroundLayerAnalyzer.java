package com.osum.updater.impl.tree4.world;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import org.objectweb.asm.tree.ClassNode;

/**
 * User: Marty
 * Date: 4/2/13
 * Time: 8:00 AM
 */
public class GroundLayerAnalyzer extends ClassAnalyzer
{
    public GroundLayerAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        if (node.name.equals(getInstance().getAccessors().get("GroundLayerAccessor").name))
        {
            return new ClassIdentity("GroundLayer", node.name, node, 0);
        }
        return null;
    }
    @Override
    public void analyse(ClassIdentity identity)
    {

    }

    @Override
    public String[] accessors()
    {
        return new String[] {"GroundLayerAccessor"};
    }
}
