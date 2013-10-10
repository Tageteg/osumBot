package com.osum.updater.identifier.identity;

import com.osum.updater.identifier.Identity;

public class FieldIdentity extends Identity
{

    private String methodName;
    private String methodType;
    private String fieldName;
    private String fieldClass;
    private String fieldType;
    private int multiplier;
    private boolean isStatic;

    public FieldIdentity(String methodName, String methodType, String fieldName, String fieldType, String fieldClass, boolean isStatic, int multiplier)
    {
        this.methodName = methodName;
        this.methodType = methodType;
        this.fieldName = fieldName;
        this.methodType = methodType;
        this.fieldType = fieldType;
        this.fieldClass = fieldClass;
        this.multiplier = multiplier;
    }

    public FieldIdentity(String methodName, String methodType, String fieldName, String fieldType, String fieldClass, boolean isStatic)
    {
        this(methodName, methodType, fieldName, fieldType, fieldClass, false, -1);
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public String getFieldClass()
    {
        return fieldClass;
    }

    public String getFieldType()
    {
        return fieldType;
    }

    public int getMultiplier()
    {
        return multiplier;
    }

    public boolean isStatic()
    {
        return isStatic;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public String getMethodType()
    {
        return methodType;
    }

}
