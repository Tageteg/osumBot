package com.osum.updater.impl.tree1.input;

import com.osum.updater.Analyzer;
import com.osum.updater.examine.instruction.InstructionExaminer;
import com.osum.updater.examine.multiplier.Multipliers;
import com.osum.updater.identifier.ClassAnalyzer;
import com.osum.updater.identifier.identity.ClassIdentity;
import com.osum.updater.identifier.identity.FieldIdentity;
import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

public class MouseAnalyzer extends ClassAnalyzer
{

    public MouseAnalyzer(Analyzer instance)
    {
        super(instance);
    }

    @Override
    public ClassIdentity accept(ClassNode node)
    {
        for (final String iface : (ArrayList<String>) node.interfaces)
        {
            if (iface.equals("java/awt/event/MouseListener"))
            {
                add("MouseAccessor", node);
                return new ClassIdentity("Mouse", node.name, node, 0);
            }
        }
        return null;
    }

    @Override
    public void analyse(ClassIdentity identity)
    {
        for (final MethodNode method : identity.getMethods())
        {
            final TypeQuerier querier = new TypeQuerier(method.access);
            if (method.name.equals("mouseExited"))
            {
                final InstructionExaminer examiner = new InstructionExaminer(method.instructions);
                final List<FieldInsnNode> fields = examiner.findAll(FieldInsnNode.class, PUTSTATIC);
                int num = 0;
                for (FieldInsnNode field : fields)
                {
                    final int multiplier = Multipliers.get(identity.getClassName(), field.name);
                    if(multiplier != -1)
                    {
                        add(new FieldIdentity((num == 0) ? "getY" : "getX", field.desc, field.name, field.desc, identity.getClassName(), false, multiplier));
                        num++;
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
