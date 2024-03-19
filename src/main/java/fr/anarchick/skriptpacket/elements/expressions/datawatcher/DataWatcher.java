package fr.anarchick.skriptpacket.elements.expressions.datawatcher;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fr.anarchick.skriptpacket.util.NumberEnums;
import fr.anarchick.skriptpacket.util.Utils;
import fr.anarchick.skriptpacket.util.converters.Converter;
import fr.anarchick.skriptpacket.util.converters.ConverterLogic;
import fr.anarchick.skriptpacket.util.converters.ConverterToUtility;
import org.bukkit.util.Vector;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DataWatcher {

    private final PacketContainer packet;

    public DataWatcher(@Nonnull PacketContainer packet) {
        this.packet = packet;
    }

    public List<WrappedDataValue> getWrappedDataValues() {
        return packet.getDataValueCollectionModifier().readSafely(0);
    }

    public List<WrappedDataValue> getWrappedDataValuesWithout(int index) {
        final List<WrappedDataValue> wrappedDataValues = getWrappedDataValues();

        for (int i = 0; i < wrappedDataValues.size(); i++) {

            if (wrappedDataValues.get(i).getIndex() == index) {
                wrappedDataValues.remove(i);
                break;
            }

        }
        return wrappedDataValues;
    }

    public void set(Number index, Object value) {
        if (index == null || value == null) return;
        WrappedDataWatcher.Serializer serializer = getSerializer(index.intValue());
        if (value instanceof Vector vector) {
            value = ConverterToUtility.BUKKIT_VECTOR_TO_PROTOCOLLIB_VECTOR3F.convert(vector);
        }

        if (serializer == null) {
            serializer = WrappedDataWatcher.Registry.get(value.getClass());
        } else if (!serializer.getType().isInstance(value)) {
            final Class<?> targetClass = serializer.getType();

            if (NumberEnums.isNumber(targetClass) && value instanceof Number number) {
                value = NumberEnums.convert(serializer.getType(), number);
            } else if (targetClass.isEnum() && value instanceof String enumName) {
                value = Utils.getEnum(targetClass, enumName, true);
            } else {
                final Converter converter = ConverterLogic.getConverter(value.getClass(), targetClass);

                if (converter != null) {
                    value = converter.convert(value);
                }


            }

        }

        set(index.intValue(), serializer, value);
    }

    public void set(int index, @Nonnull WrappedDataWatcher.Serializer serializer, Object value) {

        try {
            final WrappedDataValue wrappedDataValue = new WrappedDataValue(index, serializer, value);
            final List<WrappedDataValue> wrappedDataValues = getWrappedDataValuesWithout(index);
            wrappedDataValues.add(wrappedDataValue);
            packet.getDataValueCollectionModifier().writeSafely(0, wrappedDataValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Nullable
    public WrappedDataValue get(int index) {
        for (WrappedDataValue wrappedDataValue : getWrappedDataValues()) {

            if (wrappedDataValue.getIndex() == index) {
                return wrappedDataValue;
            }

        }

        return null;
    }

    @Nullable
    public Object getValue(int index) {
        @Nullable WrappedDataValue wrappedDataValue = get(index);
        return (wrappedDataValue == null) ? null : wrappedDataValue.getValue();
    }

    @Nullable
    public WrappedDataWatcher.Serializer getSerializer(int index) {
        @Nullable WrappedDataValue wrappedDataValue = get(index);
        return (wrappedDataValue == null) ? null : wrappedDataValue.getSerializer();
    }

    public void remove(int index) {
        packet.getDataValueCollectionModifier().writeSafely(0, getWrappedDataValuesWithout(index));
    }

    public Set<Integer> getIndexes() {
        Set<Integer> indexes = new HashSet<>();
        for (WrappedDataValue wrappedDataValue : getWrappedDataValues()) {
            indexes.add(wrappedDataValue.getIndex());
        }
        return indexes;
    }

    public List<WrappedDataValue> toList() {
        return getWrappedDataValues();
    }

    @Nonnull
    public Map<Integer, Object> toMap() {
        Map<Integer, Object> map = new HashMap<>();
        for (WrappedDataValue wrappedDataValue : getWrappedDataValues()) {
            map.put(wrappedDataValue.getIndex(), wrappedDataValue.getValue());
        }
        return map;
    }

    public JSONObject toJSON() {
        try {
            return new JSONObject(toMap());
        } catch (Exception ex) {
            return new JSONObject();
        }
    }

    @Override
    public String toString() {
        return toJSON().toString(0);
    }

}
