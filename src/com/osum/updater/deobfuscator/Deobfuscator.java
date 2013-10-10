package com.osum.updater.deobfuscator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public interface Deobfuscator extends Opcodes
{

    public abstract Map<String, ClassNode> visit(final Map<String, ClassNode> node);

}
