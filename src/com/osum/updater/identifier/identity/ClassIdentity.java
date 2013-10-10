package com.osum.updater.identifier.identity;

import com.osum.updater.identifier.Identity;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class ClassIdentity extends Identity
{

    private final String name;
    private final String className;
    private final ClassNode node;
    private final int indentation;
    private List<FieldIdentity> fieldIdentities;

    public ClassIdentity(final String name, final String className, final ClassNode node, final int indentation)
    {
        this.name = name;
        this.className = className;
        this.node = node;
        this.indentation = indentation;
    }

    public String getClassName()
    {
        return this.className;
    }

    public ClassNode getClassNode()
    {
        return this.node;
    }

    public String getName()
    {
        return this.name;
    }

    public int getIndentation()
    {
        return this.indentation;
    }

    public void addAll(final List<FieldIdentity> fieldIdentities)
    {
        this.fieldIdentities = fieldIdentities;
    }

    public List<FieldNode> getFields()
    {
        return node != null ? (List<FieldNode>) node.fields : null;
    }

    public List<MethodNode> getMethods()
    {
        return node != null ? (List<MethodNode>) node.methods : null;
    }

    public List<FieldIdentity> getFieldIdentities()
    {
        return fieldIdentities;
    }
}
