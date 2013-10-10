package com.osum.updater.identifier;

import com.osum.updater.Analyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.PremadeConditions;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

public abstract class ClassAnalyzer extends PremadeConditions implements Opcodes
{

    private final Analyzer instance;
    private final List<FieldIdentity> identities = new ArrayList<FieldIdentity>();

    public ClassAnalyzer(final Analyzer instance)
    {
        this.instance = instance;
    }

    public abstract ClassIdentity accept(final ClassNode node);

    public abstract void analyse(final ClassIdentity identity);

    public abstract String[] accessors();

    public Analyzer getInstance()
    {
        return this.instance;
    }

    public void add(final FieldIdentity identity)
    {
        if (contains(identity.getMethodName()))
        {
            for (final FieldIdentity check : identities)
            {
                if (check.getMethodName().equals(identity.getMethodName()))
                {
                    if (!check.getFieldName().equals(identity.getFieldName()))
                    {
                        System.out.println("Duplicate Fields Identified, " + identity.getMethodName() + " " + identity.getFieldClass().toString() + "." + identity.getFieldName() + " : " + identity.getFieldClass().toString() + "." + check.getFieldName());
                    } else
                    {
                        return;
                    }
                }
            }
        } else
        {
            for (final FieldIdentity check : identities)
            {
                if (check.getFieldName().equals(identity.getFieldName()))
                {
                    //System.out.println("Duplicate Fields Identified, " + identity.getMethodName() + " " + identity.getFieldClass().toString() + "." + identity.getFieldName() + " : " + identity.getFieldClass().toString() + "." + check.getFieldName());
                    //return;
                }
            }
            this.identities.add(identity);
        }
    }

    public void add(final String name, final ClassNode node)
    {
        getInstance().getAccessors().add(name, node);
    }

    public List<FieldIdentity> getIdentities()
    {
        return this.identities;
    }

    public boolean contains(final String name)
    {
        for (final FieldIdentity identity : identities)
        {
            if (identity.getMethodName().equals(name))
            {
                return true;
            }
        }
        return false;
    }
}
