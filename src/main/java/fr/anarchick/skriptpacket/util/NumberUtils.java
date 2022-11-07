package fr.anarchick.skriptpacket.util;

import java.util.Arrays;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class NumberUtils {
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> NUMBER = Arrays.asList(new Class[] {
            Integer.TYPE,
            Float.TYPE,
            Long.TYPE,
            Double.TYPE,
            Short.TYPE,
            Byte.TYPE,
            IntList.class});
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> OBJECT_NUMBER = Arrays.asList(new Class[] {
            Integer.class,
            Float.class,
            Long.class,
            Double.class,
            Short.class,
            Byte.class,
            Integer.TYPE,
            Float.TYPE,
            Long.TYPE,
            Double.TYPE,
            Short.TYPE,
            Byte.TYPE});
    
    public static final List<Class<? extends Number[]>> OBJECT_NUMBER_ARRAY = Arrays.asList(new Class[] {
            Integer[].class,
            Float[].class,
            Long[].class,
            Double[].class,
            Short[].class,
            Byte[].class});
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> PRIMITIVE_NUMBER_ARRAY = Arrays.asList(new Class[] {
            int[].class,
            float[].class,
            long[].class,
            double[].class,
            short[].class,
            byte[].class});
    
    @SuppressWarnings("unchecked")
    public static <T> T convert(Class<?> targetClass, Number ...array) {
        if (array == null || array.length == 0) return null;
        if (PRIMITIVE_NUMBER_ARRAY.contains(targetClass)) return toPrimitiveArray(targetClass, array);
        if (targetClass.equals(IntList.class)) {
            return (T) new IntArrayList(toPrimitiveIntArray(array));
        }
        return (targetClass.isArray()) ? (T) toArray(targetClass, array) : (T) toSingle(targetClass, array[0]);
    }

    /**
     * Can produce an exception if the target class does not support a high value
     */
    public static Number toSingle(Class<?> targetClass, Number n) {
        return switch (OBJECT_NUMBER.indexOf(targetClass)) {
            case 6, 0 -> n.intValue();
            case 7, 1 -> n.floatValue();
            case 8, 2 -> n.longValue();
            case 9, 3 -> n.doubleValue();
            case 10, 4 -> n.shortValue();
            case 11, 5 -> n.byteValue();
            default -> null;
        };
    }

    /**
     * Can produce an exception if the target class does not support a high value
     */
    public static Number[] toArray(Class<?> targetClass, Number ...array) {
        return switch (OBJECT_NUMBER_ARRAY.indexOf(targetClass)) {
            case 0 -> toIntegerArray(array);
            case 1 -> toFloatArray(array);
            case 2 -> toLongArray(array);
            case 3 -> toDoubleArray(array);
            case 4 -> toShortArray(array);
            case 5 -> toByteArray(array);
            default -> null;
        };
    }
    
    public static <T> T toPrimitiveArray(Class<?> targetClass, Number ...array) {
        return switch (PRIMITIVE_NUMBER_ARRAY.indexOf(targetClass)) {
            case 0 -> (T) toPrimitiveIntArray(array);
            case 1 -> (T) toPrimitiveFloatArray(array);
            case 2 -> (T) toPrimitiveLongArray(array);
            case 3 -> (T) toPrimitiveDoubleArray(array);
            case 4 -> (T) toPrimitiveShortArray(array);
            case 5 -> (T) toPrimitiveByteArray(array);
            default -> null;
        };
    }
    
    public static Integer[] toIntegerArray(final Number ...array) {
        final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }

    public static Float[] toFloatArray(final Number ...array) {
        final Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }

    public static Long[] toLongArray(final Number ...array) {
        final Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }
    
    public static Short[] toShortArray(final Number ...array) {
        final Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }
    
    public static Double[] toDoubleArray(final Number ...array) {
        final Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }
    
    public static Byte[] toByteArray(final Number ...array) {
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }
    
    public static int[] toPrimitiveIntArray(final Number ...array) {
        return Arrays.stream(array).mapToInt(Number::intValue).toArray();
    }
    public static float[] toPrimitiveFloatArray(final Number ...array) {
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }
    public static long[] toPrimitiveLongArray(final Number ...array) {
        return Arrays.stream(array).mapToLong(Number::longValue).toArray();
    }
    public static double[] toPrimitiveDoubleArray(final Number ...array) {
        return Arrays.stream(array).mapToDouble(Number::doubleValue).toArray();
    }
    public static short[] toPrimitiveShortArray(final Number ...array) {
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }
    public static byte[] toPrimitiveByteArray(final Number ...array) {
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }
    
    public static boolean isNumber(Class<?> clazz) {
        if (clazz == null) return false;
        if (clazz.isArray()) clazz = clazz.getComponentType();
        return NUMBER.contains(clazz);
    }

}