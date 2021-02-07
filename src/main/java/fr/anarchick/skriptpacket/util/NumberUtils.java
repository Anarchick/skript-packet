package fr.anarchick.skriptpacket.util;

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
	
	public static Number[] convert(Class<?> targetClass, boolean isArray, Number[] array) {
		if (array == null) return null;
		if (targetClass.getComponentType() != null) targetClass = targetClass.getComponentType();
		switch(NUMBER.indexOf(targetClass)) {
			case 0:
				return (isArray) ? toInteger(array) : new Integer[] {array[0].intValue()};
			case 1:
				return (isArray) ? toFloat(array) : new Float[] {array[0].floatValue()};
			case 2:
				return (isArray) ? toLong(array) : new Long[] {array[0].longValue()};
			case 3:
				return (isArray) ? toDouble(array) : new Double[] {array[0].doubleValue()};
			case 4:
				return (isArray) ? toShort(array) : new Short[] {array[0].shortValue()};
			case 5:
				return (isArray) ? toByte(array) : new Byte[] {array[0].byteValue()};
			case 6:
				return (isArray) ? toInteger(array) : new Integer[] {array[0].intValue()};
			case 7:
				return (isArray) ? toFloat(array) : new Float[] {array[0].floatValue()};
			case 8:
				return (isArray) ? toLong(array) : new Long[] {array[0].longValue()};
			case 9:
				return (isArray) ? toDouble(array) : new Double[] {array[0].doubleValue()};
			case 10:
				return (isArray) ? toShort(array) : new Short[] {array[0].shortValue()};
			case 11:
				return (isArray) ? toByte(array) : new Byte[] {array[0].byteValue()};
			default:
				return null;
		}
	}
	
	public static Object convertPrimitive(Class<?> targetClass, boolean isArray, Number[] array) {
		if (array == null) return null;
		if (targetClass.getComponentType() != null) targetClass = targetClass.getComponentType();
		switch(NUMBER.indexOf(targetClass)) {
			case 6:
			case 0:
				return (isArray) ? toPrimitiveInt(array) : array[0].intValue();
			case 7:
			case 1:
				return (isArray) ? toPrimitiveFloat(array) : array[0].floatValue();
			case 8:
			case 2:
				return (isArray) ? toPrimitiveLong(array) : array[0].longValue();
			case 9:
			case 3:
				return (isArray) ? toPrimitiveDouble(array) : array[0].doubleValue();
			case 10:
			case 4:
				return (isArray) ? toPrimitiveShort(array) : array[0].shortValue();
			case 11:
			case 5:
				return (isArray) ? toPrimitiveByte(array) : array[0].byteValue();
			default:
				return null;
		}
	}
	
	/*
	public static Object convertPrimitive(Class<?> targetClass, boolean isArray, Number[] array) {
		if (array == null) return null;
		if (targetClass.getComponentType() != null) targetClass = targetClass.getComponentType();
		switch(NUMBER.indexOf(targetClass)) {
			case 0:
				return array[0].intValue();
			case 1:
				return array[0].floatValue();
			case 2:
				return array[0].longValue();
			case 3:
				return array[0].doubleValue();
			case 4:
				return array[0].shortValue();
			case 5:
				return array[0].byteValue();
			case 6:
				return array[0].intValue();
			case 7:
				return array[0].floatValue();
			case 8:
				return array[0].longValue();
			case 9:
				return array[0].doubleValue();
			case 10:
				return array[0].shortValue();
			case 11:
				return array[0].byteValue();
			default:
				return null;
		}
	}
	*/
	
    public static Integer[] toInteger(final Number[] array) {
    	if (array == null) return null;
    	final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
	}
    
    public static Float[] toFloat(final Number[] array) {
    	if (array == null) return null;
    	final Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
	}
    
    public static Long[] toLong(final Number[] array) {
    	if (array == null) return null;
    	final Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
	}
    
    public static Short[] toShort(final Number[] array) {
    	if (array == null) return null;
    	final Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
	}
    
    public static Double[] toDouble(final Number[] array) {
    	if (array == null) return null;
    	final Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
	}
    
    public static Byte[] toByte(final Number[] array) {
    	if (array == null) return null;
    	final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
	}
    
    public static int[] toPrimitiveInt(final Number[] array) {
    	if (array == null) return null;
    	final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
	}
    public static float[] toPrimitiveFloat(final Number[] array) {
    	if (array == null) return null;
    	final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
	}
    public static long[] toPrimitiveLong(final Number[] array) {
    	if (array == null) return null;
    	final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
	}
    public static double[] toPrimitiveDouble(final Number[] array) {
    	if (array == null) return null;
    	final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
	}
    public static short[] toPrimitiveShort(final Number[] array) {
    	if (array == null) return null;
    	final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
	}
    public static byte[] toPrimitiveByte(final Number[] array) {
    	if (array == null) return null;
    	final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
	}
	
	public static boolean isNumber(Object obj) {
		Class<?> componentType = obj.getClass().getComponentType();
		return (componentType != null) ? NUMBER.contains(componentType) : NUMBER.contains(obj.getClass());
		//return (NUMBER.contains(obj.getClass()) || NUMBER_ARRAY.contains(obj.getClass()));
	}
	
	public static boolean isObjectNumber(Object obj) {
		return OBJECT_NUMBER.contains(obj.getClass());
	}
	
	// not works
	public static boolean isPrimitiveNumber(Object obj) {
		Class<?> componentType = obj.getClass().getComponentType();
		return (componentType != null) ? componentType.isPrimitive() : obj.getClass().isPrimitive();
	}
	
	public static boolean isPrimitiveNumberArray(Object obj) {
		return PRIMITIVE_NUMBER_ARRAY.contains(obj.getClass());
	}
	
	public static boolean isNumberArray(Object obj) {
		return NUMBER_ARRAY.contains(obj.getClass());
	}
}
