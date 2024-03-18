package fr.anarchick.skriptpacket.util.converters;

public interface Converter {

    Object convert(final Object single);

    default boolean isArrayInput() {
        return false;
    }

    default boolean returnArray() {
        return false;
    }

    Class<?> getReturnType();

}
