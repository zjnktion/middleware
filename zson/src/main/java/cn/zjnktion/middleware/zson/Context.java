package cn.zjnktion.middleware.zson;

import cn.zjnktion.middleware.zson.serializer.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zjnktion
 */
public final class Context {

    private static final Map<Class, ZsonSerializer> SERIALIZER_MAP = new HashMap<Class, ZsonSerializer>();

    static {
        SERIALIZER_MAP.put(boolean.class, BooleanSerializer.INSTANCE);
        SERIALIZER_MAP.put(Boolean.class, BooleanSerializer.INSTANCE);
        SERIALIZER_MAP.put(boolean[].class, BooleanArraySerializer.INSTANCE);
        SERIALIZER_MAP.put(Boolean[].class, BooleanArraySerializer.INSTANCE);

        SERIALIZER_MAP.put(byte.class, ByteSerializer.INSTANCE);
        SERIALIZER_MAP.put(Byte.class, ByteSerializer.INSTANCE);
        SERIALIZER_MAP.put(byte[].class, ByteArraySerializer.INSTANCE);
        SERIALIZER_MAP.put(Byte[].class, ByteArraySerializer.INSTANCE);

        SERIALIZER_MAP.put(char.class, CharacterSerializer.INSTANCE);
        SERIALIZER_MAP.put(Character.class, CharacterSerializer.INSTANCE);
        SERIALIZER_MAP.put(char[].class, CharacterArraySerializer.INSTANCE);
        SERIALIZER_MAP.put(Character[].class, CharacterArraySerializer.INSTANCE);

        SERIALIZER_MAP.put(short.class, ShortSerializer.INSTANCE);
        SERIALIZER_MAP.put(Short.class, ShortSerializer.INSTANCE);
        SERIALIZER_MAP.put(short[].class, ShortArraySerializer.INSTANCE);
        SERIALIZER_MAP.put(Short[].class, ShortArraySerializer.INSTANCE);

        SERIALIZER_MAP.put(int.class, IntegerSerializer.INSTANCE);
        SERIALIZER_MAP.put(Integer.class, IntegerSerializer.INSTANCE);
        SERIALIZER_MAP.put(int[].class, IntegerArraySerializer.INSTANCE);
        SERIALIZER_MAP.put(Integer[].class, IntegerArraySerializer.INSTANCE);

        SERIALIZER_MAP.put(long.class, LongSerializer.INSTANCE);
        SERIALIZER_MAP.put(Long.class, LongSerializer.INSTANCE);
        SERIALIZER_MAP.put(long[].class, LongArraySerializer.INSTANCE);
        SERIALIZER_MAP.put(Long[].class, LongArraySerializer.INSTANCE);

        SERIALIZER_MAP.put(double.class, DoubleSerializer.INSTANCE);
        SERIALIZER_MAP.put(Double.class, DoubleSerializer.INSTANCE);
        SERIALIZER_MAP.put(double[].class, DoubleArraySerializer.INSTANCE);
        SERIALIZER_MAP.put(Double[].class, DoubleArraySerializer.INSTANCE);

        SERIALIZER_MAP.put(String.class, StringSerializer.INSTANCE);
        SERIALIZER_MAP.put(String[].class, StringArraySerializer.INSTANCE);

        SERIALIZER_MAP.put(Date.class, DateSerializer.INSTANCE);

        SERIALIZER_MAP.put(Map.class, MapSerializer.INSTANCE);

        SERIALIZER_MAP.put(List.class, ListSerializer.INSTANCE);
    }

    public static ZsonSerializer getSerializer(Class clazz) {
        ZsonSerializer serializer = null;

        serializer = SERIALIZER_MAP.get(clazz);
        if (serializer != null) {
            return serializer;
        }

        if (Map.class.isAssignableFrom(clazz)) {
            return MapSerializer.INSTANCE;
        }

        if (List.class.isAssignableFrom(clazz)) {
            return ListSerializer.INSTANCE;
        }

        if (Date.class.isAssignableFrom(clazz)) {
            return DateSerializer.INSTANCE;
        }

        if (clazz.isArray()) {
            return ReferenceArraySerializer.INSTANCE;
        }

        return ReferenceSerializer.INSTANCE;
    }
}
