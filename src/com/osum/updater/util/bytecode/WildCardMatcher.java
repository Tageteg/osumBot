package com.osum.updater.util.bytecode;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class WildCardMatcher
{

    private final FieldNode fieldNode;
    private final MethodNode methodNode;
    private final FieldInsnNode fieldInsnNode;
    private final String wildcard;

    public WildCardMatcher(final FieldNode fieldNode, final String wildcard)
    {
        this.wildcard = wildcard;
        this.fieldNode = fieldNode;
        this.methodNode = null;
        this.fieldInsnNode = null;
    }

    public WildCardMatcher(final FieldInsnNode fieldInsnNode, final String wildcard)
    {
        this.wildcard = wildcard;
        this.fieldInsnNode = fieldInsnNode;
        this.methodNode = null;
        this.fieldNode = null;
    }

    public WildCardMatcher(final MethodNode methodNode, final String wildcard)
    {
        this.wildcard = wildcard;
        this.methodNode = methodNode;
        this.fieldNode = null;
        this.fieldInsnNode = null;
    }

    public boolean match()
    {
        final String[] rebuiltWildCard = buildWildCard().split("");
        final String[] parsedWildCard = wildcard.split("");
        for (int index = 0; index < parsedWildCard.length; index++)
        {
            if (!parsedWildCard[index].equals(rebuiltWildCard[index]))
            {
                return false;
            }
        }
        return true;
    }

    public String buildWildCard()
    {
        final StringBuilder rebuiltPattern = new StringBuilder();
        String[] defaultCard = getDefaultCard();
        for (int index = 0; index < defaultCard.length; index++)
        {
            if (index == 0)
            {
                continue;
            }
            if (defaultCard[index].equals("V"))
            {
                if (defaultCard[index - 1].equals(")"))
                {
                    rebuiltPattern.append("V");
                } else
                {
                    rebuiltPattern.append("?");
                }
            } else if (defaultCard[index].equals("Z"))
            {
                rebuiltPattern.append("Z");
            } else if (defaultCard[index].equals("C"))
            {
                if (defaultCard[index - 1].equals(")"))
                {
                    rebuiltPattern.append("C");
                } else
                {
                    rebuiltPattern.append("?");
                }
            } else if (defaultCard[index].equals("B"))
            {
                if (defaultCard[index - 1].equals(")"))
                {
                    rebuiltPattern.append("B");
                } else
                {
                    rebuiltPattern.append("?");
                }
            } else if (defaultCard[index].equals("S"))
            {
                if (defaultCard[index - 1].equals(")"))
                {
                    rebuiltPattern.append("S");
                } else
                {
                    rebuiltPattern.append("?");
                }
            } else if (defaultCard[index].equals("I"))
            {
                if (defaultCard[index - 1].equals(")"))
                {
                    rebuiltPattern.append("I");
                } else
                {
                    rebuiltPattern.append("?");
                }
            } else if (defaultCard[index].equals("F"))
            {
                if (defaultCard[index - 1].equals(")"))
                {
                    rebuiltPattern.append("F");
                } else
                {
                    rebuiltPattern.append("?");
                }
            } else if (defaultCard[index].equals("J"))
            {
                if (defaultCard[index - 1].equals(")"))
                {
                    rebuiltPattern.append("J");
                } else
                {
                    rebuiltPattern.append("?");
                }
            } else if (defaultCard[index].equals("D"))
            {
                if (defaultCard[index - 1].equals(")"))
                {
                    rebuiltPattern.append("D");
                } else
                {
                    rebuiltPattern.append("?");
                }
            } else if (defaultCard[index].equals("["))
            {
                rebuiltPattern.append("[");
            } else if (defaultCard[index].equals("L"))
            {
                rebuiltPattern.append("L");
            } else if (defaultCard[index].equals(";"))
            {
                rebuiltPattern.append(";");
            } else if (defaultCard[index].equals("("))
            {
                rebuiltPattern.append("(");
            } else if (defaultCard[index].equals(")"))
            {
                rebuiltPattern.append(")");
            } else
            {
                rebuiltPattern.append("?");
            }
        }
        return rebuiltPattern.toString();
    }

    private String[] getDefaultCard()
    {
        if (fieldNode != null)
        {
            return fieldNode.desc.split("");
        } else if (methodNode != null)
        {
            return methodNode.desc.split("");
        } else if (fieldInsnNode != null)
        {
            return fieldInsnNode.desc.split("");
        } else
        {
            return null;
        }
    }

}
