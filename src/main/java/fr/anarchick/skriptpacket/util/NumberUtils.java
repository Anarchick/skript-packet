package fr.anarchick.skriptpacket.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumberUtils {
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> NUMBER = Arrays.asList(new Class[] {
            Integer.class,
            Float.class,
            Long.class,
            Double.class,
            Short.class,
            Byte.class,
            int.class,
            float.class,
            long.class,
            double.class,
            short.class,
            byte.class});
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> NUMBER_ARRAY = Arrays.asList(new Class[] {
            Integer[].class,
            Float[].class,
            Long[].class,
            Double[].class,
            Short[].class,
            Byte[].class,
            int[].class,
            float[].class,
            long[].class,
            double[].class,
            short[].class,
            byte[].class});
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> OBJECT_NUMBER = Arrays.asList(new Class[] {
            Integer.class,
            Float.class,
            Long.class,
            Double.class,
            Short.class,
            Byte.class});
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> OBJECT_NUMBER_ARRAY = Arrays.asList(new Class[] {
            Integer[].class,
            Float[].class,
            Long[].class,
            Double[].class,
            Short[].class,
            Byte[].class});
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> PRIMITIVE_NUMBER = Arrays.asList(new Class[] {
            int.class,
            float.class,
            long.class,
            double.class,
            short.class,
            byte.class});
    
    @SuppressWarnings("rawtypes")
    public static final List<Class> PRIMITIVE_NUMBER_ARRAY = Arrays.asList(new Class[] {
            int[].class,
            float[].class,
            long[].class,
            double[].class,
            short[].class,
            byte[].class});
    
    public static Object convert(Class<?> targetClass, Number ...array) {
        if (array == null || array.length == 0) return null;
        boolean isArray = targetClass.isArray();
        if (PRIMITIVE_NUMBER.contains(targetClass) || PRIMITIVE_NUMBER_ARRAY.contains(targetClass)) {
            return (isArray) ? toPrimitiveArray(targetClass, array) : toPrimitive(targetClass, array);
        } else if (OBJECT_NUMBER.contains(targetClass) || OBJECT_NUMBER_ARRAY.contains(targetClass)) {
            return (isArray) ? toArray(targetClass, array) : toSingle(targetClass, array);
        }
        return null;
    }
    
    public static Number toSingle(Class<?> targetClass, Number ...array) {
        if (array == null || array.length == 0) return null;
        Class<?> component = targetClass.getComponentType();
        if (component != null) targetClass = component;
        
        switch(NUMBER.indexOf(targetClass)) {
            case 6:
            case 0:
                return new Integer(array[0].intValue());
            case 7:
            case 1:
                return new Float(array[0].floatValue());
            case 8:
            case 2:
                return new Long(array[0].longValue());
            case 9:
            case 3:
                return new Double(array[0].doubleValue());
            case 10:
            case 4:
                return new Short(array[0].shortValue());
            case 11:
            case 5:
                return new Byte(array[0].byteValue());
            default:
                return null;
        }
    }
    
    public static Number[] toArray(Class<?> targetClass, Number ...array) {
        if (array == null || array.length == 0) return null;
        Class<?> component = targetClass.getComponentType();
        if (component != null) targetClass = component;
        switch(NUMBER.indexOf(targetClass)) {
            case 6:
            case 0:
                return toIntegerArray(array);
            case 7:
            case 1:
                return toIntegerArray(array);
            case 8:
            case 2:
                return toLongArray(array);
            case 9:
            case 3:
                return toDoubleArray(array);
            case 10:
            case 4:
                return toShortArray(array);
            case 11:
            case 5:
                return toByteArray(array);
            default:
                return null;
        }
    }
    
    public static Object toPrimitive(Class<?> targetClass, Number ...array) {
        if (array == null || array.length == 0) return null;
        Class<?> component = targetClass.getComponentType();
        if (component != null) targetClass = component;
        switch(NUMBER.indexOf(targetClass)) {
            case 6:
            case 0:
                return array[0].intValue();
            case 7:
            case 1:
                return array[0].floatValue();
            case 8:
            case 2:
                return array[0].longValue();
            case 9:
            case 3:
                return array[0].doubleValue();
            case 10:
            case 4:
                return array[0].shortValue();
            case 11:
            case 5:
                return array[0].byteValue();
            default:
                return null;
        }
    }
    
    public static Object toPrimitiveArray(Class<?> targetClass, Number ...array) {
        if (array == null || array.length == 0) return null;
        Class<?> component = targetClass.getComponentType();
        if (component != null) targetClass = component;
        switch(NUMBER.indexOf(targetClass)) {
            case 6:
            case 0:
                return toPrimitiveIntArray(array);
            case 7:
            case 1:
                return toPrimitiveFloatArray(array);
            case 8:
            case 2:
                return toPrimitiveLongArray(array);
            case 9:
            case 3:
                return toPrimitiveDoubleArray(array);
            case 10:
            case 4:
                return toPrimitiveShortArray(array);
            case 11:
            case 5:
                return toPrimitiveByteArray(array);
            default:
                return null;
        }
    }
        
    public static Object convertIntoPrimitive(Class<?> targetClass, Number ...array) {
        if (array == null || array.length == 0) return null;
        boolean isArray = targetClass.isArray();
        Class<?> component = targetClass.getComponentType();
        if (component != null) targetClass = component;
        switch(NUMBER.indexOf(targetClass)) {
            case 6:
            case 0:
                return (isArray) ? toPrimitiveIntArray(array) : array[0].intValue();
            case 7:
            case 1:
                return (isArray) ? toPrimitiveFloatArray(array) : array[0].floatValue();
            case 8:
            case 2:
                return (isArray) ? toPrimitiveLongArray(array) : array[0].longValue();
            case 9:
            case 3:
                return (isArray) ? toPrimitiveDoubleArray(array) : array[0].doubleValue();
            case 10:
            case 4:
                return (isArray) ? toPrimitiveShortArray(array) : array[0].shortValue();
            case 11:
            case 5:
                return (isArray) ? toPrimitiveByteArray(array) : array[0].byteValue();
            default:
                return null;
        }
    }
    
    public static Integer[] toIntegerArray(final Number ...array) {
        if (array == null) return null;
        final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }
    
    public static Float[] toFloatArray(final Number ...array) {
        if (array == null) return null;
        final Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }
    
    public static Long[] toLongArray(final Number ...array) {
        if (array == null) return null;
        final Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }
    
    public static Short[] toShortArray(final Number ...array) {
        if (array == null) return null;
        final Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }
    
    public static Double[] toDoubleArray(final Number ...array) {
        if (array == null) return null;
        final Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }
    
    public static Byte[] toByteArray(final Number ...array) {
        if (array == null) return null;
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }
    
    public static int[] toPrimitiveIntArray(final Number ...array) {
        if (array == null) return null;
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }
    public static float[] toPrimitiveFloatArray(final Number ...array) {
        if (array == null) return null;
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }
    public static long[] toPrimitiveLongArray(final Number ...array) {
        if (array == null) return null;
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }
    public static double[] toPrimitiveDoubleArray(final Number ...array) {
        if (array == null) return null;
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }
    public static short[] toPrimitiveShortArray(final Number ...array) {
        if (array == null) return null;
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }
    public static byte[] toPrimitiveByteArray(final Number ...array) {
        if (array == null) return null;
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }
    
    public static boolean isNumber(Object obj) {
        // Class<?> componentType = obj.getClass().getComponentType();
        // return (componentType != null) ? NUMBER.contains(componentType) : NUMBER.contains(obj.getClass());
        return (NUMBER.contains(obj.getClass()) || NUMBER_ARRAY.contains(obj.getClass()));
    }
    
    public static boolean isObjectNumber(Object obj) {
        return OBJECT_NUMBER.contains(obj.getClass());
    }
    
    public static boolean isPrimitiveNumberArray(Object obj) {
        return PRIMITIVE_NUMBER_ARRAY.contains(obj.getClass());
    }
    
    public static boolean isNumberArray(Object obj) {
        return NUMBER_ARRAY.contains(obj.getClass());
    }
    
    public static Number[] toNumeric(final Object ...array) {
        if (array == null || array.length == 0) return null;
        final List<Number> result = new ArrayList<>();
        for (Object o : array) {
            if (o instanceof Number) {
                result.add((Number) o);
                continue;
            }
            return null;
        }
        return result.toArray(new Number[0]);
    }
    
}
