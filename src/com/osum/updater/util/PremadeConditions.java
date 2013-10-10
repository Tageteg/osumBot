package com.osum.updater.util;

import com.osum.updater.util.abstracts.Condition;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * Created with IntelliJ IDEA.
 * User: UPT Digital Admin
 * Date: 3/21/13
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class PremadeConditions
{

    /*
    Ban Conventions but it looks better XD
     */

    public final Condition IntegerCondition = new Condition()
    {
        @Override
        public boolean equals(AbstractInsnNode node)
        {
            return node instanceof FieldInsnNode && ((FieldInsnNode) node).desc.equals("I");
        }
    };

    public final Condition BooleanCondition = new Condition()
    {
        @Override
        public boolean equals(AbstractInsnNode node)
        {
            return node instanceof FieldInsnNode && ((FieldInsnNode) node).desc.equals("Z");
        }
    };

    public final Condition ByteCondition = new Condition()
    {
        @Override
        public boolean equals(AbstractInsnNode node)
        {
            return node instanceof FieldInsnNode && ((FieldInsnNode) node).desc.equals("B");
        }
    };

}
