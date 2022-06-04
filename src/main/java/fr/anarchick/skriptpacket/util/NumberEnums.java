package fr.anarchick.skriptpacket.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Replaced by NumberEnums since 2.1.0
 */
public enum NumberEnums {
    INTEGER(Integer.class, Integer.TYPE),
    FLOAT(Float.class, Float.TYPE),
    LONG(Long.class, Long.TYPE),
    DOUBLE(Double.class, Double.TYPE),
    SHORT(Short.class, Short.TYPE),
    BYTE(Byte.class, Byte.TYPE);

    public final Class<? extends Number> objectClass;
    public final Class<? extends Number[]> objectArrayClass;
    public final Class<?> primitiveClass;
    public final Class<?> primitiveArrayClass;

    @SuppressWarnings({"unchecked" })
    NumberEnums(Class<? extends Number> objectClass, Class<?> primitiveClass) {
        this.objectClass = objectClass;
        this.objectArrayClass = (Class<? extends Number[]>) objectClass.arrayType();
        this.primitiveClass = primitiveClass;
        this.primitiveArrayClass = primitiveClass.arrayType();
    }

    public static NumberEnums get(int ordinal) {
        for (NumberEnums e : values()) {
            if (e.ordinal() == ordinal) return e;
        }
        return null;
    }

    public static NumberEnums get(Class<?> clazz) {
        String className = (clazz.isArray()) ? clazz.getComponentType().getSimpleName().toUpperCase(): clazz.getSimpleName().toUpperCase();
        return switch (className) {
            case "BYTE" -> BYTE;
            case "INT", "INTEGER", "INTLIST" -> INTEGER;
            case "LONG" -> LONG;
            case "SHORT" -> SHORT;
            case "FLOAT" -> FLOAT;
            case "DOUBLE" -> DOUBLE;
            default -> null;
        };
    }

    public static boolean isNumber(Class<?> clazz) {
        return get(clazz) != null;
    }

    @SuppressWarnings({"unchecked" })
    public static <T> T convert(Class<T> targetClass, Number... input) {
        if (input == null || input.length == 0) return null;
        if (targetClass.equals(IntList.class)) {
            return (T) new IntArrayList((int[]) INTEGER.toPrimitiveArray(input));
        }
        NumberEnums target = get(targetClass);
        if (targetClass.isArray()) {
            return (targetClass.getComponentType().isPrimitive()) ? (T) target.toPrimitiveArray(input) : (T) target.toArray(input);
        } else {
            return (T) target.toSingle(input[0]);
        }
    }

    @SuppressWarnings({"unchecked" })
    private <T> T createPrimitiveArray(int length) {
        return switch (ordinal()) {
            case 0 -> (T) new int[length];
            case 1 -> (T) new float[length];
            case 2 -> (T) new long[length];
            case 3 -> (T) new double[length];
            case 4 -> (T) new Short[length];
            case 5 -> (T) new byte[length];
            default -> null;
        };
    }

    public Number toSingle(Number input) {
        return switch (ordinal()) {
            case 0 -> input.intValue();
            case 1 -> input.floatValue();
            case 2 -> input.longValue();
            case 3 -> input.doubleValue();
            case 4 -> input.shortValue();
            case 5 -> input.byteValue();
            default -> null;
        };
    }

    public Number[] toArray(Number... input) {
        int length = (input == null) ? 0 : input.length;
        if (this.objectArrayClass.isInstance(input)) return input;
        final Number[] output = (Number[]) Array.newInstance(this.objectClass, length);
        Arrays.setAll(output, (i) -> toSingle(input[i]));
        return output;
    }

    public Object toPrimitiveArray(Number... input) {
        int length = (input == null) ? 0 : input.length;
        if (this.primitiveArrayClass.isInstance(input)) return input;
        final Object output = createPrimitiveArray(length);
        for (int i = 0; i < length; i++) {
            Array.set(output, i, toSingle(input[i]));
        }
        return output;
    }

}
