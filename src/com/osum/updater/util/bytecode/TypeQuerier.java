package com.osum.updater.util.bytecode;

public class TypeQuerier
{

    private final int access;

    public TypeQuerier(final int access)
    {
        this.access = access;
    }

    public boolean is(final int opcode)
    {
        return (access & opcode) == opcode;
    }

    public boolean isNot(final int opcode)
    {
        return (access & opcode) == 0;
    }

}