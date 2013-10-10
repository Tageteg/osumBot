package com.osum.updater.examine.field;

import com.osum.updater.util.bytecode.TypeQuerier;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.HashMap;

public class FieldExaminer
{

    private final ClassNode node;

    public FieldExaminer(final ClassNode node)
    {
        this.node = node;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, Integer> getMap()
    {
        final HashMap<String, Integer> map = new HashMap<>();
        int intCount = 0, shortCount = 0, longCount = 0, floatCount = 0, doubleCount = 0,
                intArrayCount = 0, shortArrayCount = 0, longArrayCount = 0, floatArrayCount = 0, doubleArrayCount = 0,
                byteCount = 0, byteDArray = 0, byteTArray = 0, stringCount = 0, stringArrayCount = 0,
                objectCount = 0, objectArrayCount = 0, booleanCount = 0, booleanArrayCount = 0, unknownCount = 0, totalCount = 0;
        for (final FieldNode field : (ArrayList<FieldNode>) node.fields)
        {
            if (new TypeQuerier(field.access).isNot(Opcodes.ACC_STATIC))
            {
                switch (field.desc)
                {
                    case "I":
                        intCount++;
                        break;
                    case "S":
                        shortCount++;
                        break;
                    case "D":
                        doubleCount++;
                        break;
                    case "J":
                        longCount++;
                        break;
                    case "F":
                        floatCount++;
                        break;
                    case "Z":
                        booleanCount++;
                        break;
                    case "[I":
                        intArrayCount++;
                        break;
                    case "[S":
                        shortArrayCount++;
                        break;
                    case "[J":
                        longArrayCount++;
                        break;
                    case "[F":
                        floatArrayCount++;
                        break;
                    case "[Z":
                        booleanArrayCount++;
                        break;
                    case "B":
                        byteCount++;
                        break;
                    case "[B":
                        byteDArray++;
                        break;
                    case "[[B":
                        byteTArray++;
                        break;
                    case "Ljava/lang/String;":
                        stringCount++;
                        break;
                    case "[Ljava/lang/String;":
                        stringArrayCount++;
                        break;
                    case "Ljava/lang/Object;":
                        objectCount++;
                        break;
                    case "[Ljava/lang/Object;":
                        objectArrayCount++;
                    default:
                        unknownCount++;
                        break;
                }
                totalCount++;
            }
        }
        map.put("Int", intCount);
        map.put("Short", shortCount);
        map.put("Long", longCount);
        map.put("Float", floatCount);
        map.put("Double", doubleCount);
        map.put("Boolean", booleanCount);
        map.put("IntArray", intArrayCount);
        map.put("ShortArray", shortArrayCount);
        map.put("LongArray", longArrayCount);
        map.put("FloatArray", floatArrayCount);
        map.put("DoubleArray", doubleArrayCount);
        map.put("BooleanArray", booleanArrayCount);
        map.put("String", stringCount);
        map.put("StringArray", stringArrayCount);
        map.put("Object", objectCount);
        map.put("ObjectArray", objectArrayCount);
        map.put("Unknown", unknownCount);
        map.put("Byte", byteCount);
        map.put("ByteArray", byteDArray);
        map.put("ByteTArray", byteTArray);
        map.put("Total", totalCount);
        return map;
    }

}
