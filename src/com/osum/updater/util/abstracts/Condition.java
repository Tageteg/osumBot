package com.osum.updater.util.abstracts;

import org.objectweb.asm.tree.AbstractInsnNode;

public abstract class Condition
{

    public abstract boolean equals(final AbstractInsnNode node);

}
