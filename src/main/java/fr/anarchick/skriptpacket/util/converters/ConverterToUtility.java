package fr.anarchick.skriptpacket.util.converters;

import ch.njol.skript.Skript;
import com.btk5h.skriptmirror.ObjectWrapper;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import fr.anarchick.skriptpacket.SkriptPacket;
import it.unimi.dsi.fastutil.ints.IntList;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Matcher;

public enum ConverterToUtility implements Converter {

    HIMSELF {
        @Override
        public Object convert(final Object single) {
            return single;
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Object.class;
        }
    },

    SKRIPTMIRROR_UNWRAPPER {
        @Override
        public Object convert(final Object single) {
            if (SkriptPacket.isReflectAddon && single != null) {
                return ObjectWrapper.unwrapIfNecessary(single);
            }
            return single;
        }

        @Override
        public Class<?> getInputType() {
            return (SkriptPacket.isReflectAddon) ? ObjectWrapper.class : Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Object.class;
        }
    },

    INTLIST_TO_INTEGER_ARRAY {
        @Override
        public boolean returnArray() {
            return true;
        }

        @Override
        public Object convert(final Object single) {
            if (single instanceof IntList intList) {
                Integer[] array = new Integer[intList.size()];
                for (int i = 0; i < intList.size(); i++) {
                    array[i] = intList.getInt(i);
                }
            }
            return new Integer[0];
        }

        @Override
        public Class<?> getInputType() {
            return IntList.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Integer[].class;
        }
    },

    OBJECT_TO_OPTIONAL {
        @Override
        public Object convert(final Object single) {
            return Optional.of(single);
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Optional.class;
        }
    },

    /**
     * String to UUID
     * Bukkit Entity's UUID
     */
    RELATED_TO_UUID {
        @Override
        public Object convert(final Object single) {
            if (single instanceof String uuid) {
                Matcher matcher = ConverterLogic.regexUUID.matcher(uuid);
                if (matcher.find()) return UUID.fromString(uuid);
            } else if (single instanceof Entity) {
                return ((Entity)single).getUniqueId();
            }
            return single;
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return UUID.class;
        }
    },

    OBJECT_TO_LIST {
        @Override
        public boolean isArrayInput() {
            return true;
        }

        @Override
        public Object convert(final Object single) {
            final ArrayList<Object> list = new ArrayList<>();
            if (single instanceof Object[] array) {
                Collections.addAll(list, array);
            }
            return list;
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ArrayList.class;
        }
    },

    OBJECT_TO_SET {
        @Override
        public boolean isArrayInput() {
            return true;
        }

        @Override
        public Object convert(final Object single) {
            final HashSet<Object> set = new HashSet<>();
            if (single instanceof Object[] array) {
                Collections.addAll(set, array);
            }
            return set;
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return HashSet.class;
        }
    },

    // TODO check
    STRING_TO_MOJANGSON {
        @Override
        public Object convert(@Nonnull final Object single) {
            final String nbt = Optional.ofNullable((String)single).orElse("");
            Object nms = null;
            try {
                nms = ConverterLogic.MojangsonClass.getMethod("parse", String.class).invoke(null, nbt);
            } catch (Exception ex) {
                Skript.exception(ex);
            }
            return nms;
        }

        @Override
        public Class<?> getInputType() {
            return String.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.MojangsonClass;
        }
    },

    // TODO check
    NMS_CHATCOMPONENTTEXT_TO_STRING {
        @Override
        public Object convert(final Object single) {
            return WrappedChatComponent.fromHandle(single).getJson();
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return String.class;
        }
    },

    STRING_TO_MD5_BASECOMPONENT {
        @Override
        public Object convert(@Nonnull final Object single) {
            final String json = Optional.ofNullable((String)single).orElse("");
            WrappedChatComponent wrapper;
            if (json.startsWith("{") && json.endsWith("}")) {
                wrapper = WrappedChatComponent.fromJson(json);
            } else {
                wrapper = WrappedChatComponent.fromText(json);
            }
            return ComponentConverter.fromWrapper(wrapper);
        }

        @Override
        public Class<?> getInputType() {
            return String.class;
        }

        @Override
        public Class<?> getOutputType() {
            return BaseComponent.class;
        }
    },

    NMS_MINECRAFTKEY_TO_STRING {
        @Override
        public Object convert(final Object single) {
            return MinecraftKey.getConverter().getSpecific(single).getKey();
        }

        @Override
        public Class<?> getInputType() {
            return ConverterLogic.MinecraftKeyClass;
        }

        @Override
        public Class<?> getOutputType() {
            return String.class;
        }
    },

    BUKKIT_VECTOR_TO_PROTOCOLLIB_VECTOR3F {
        @Override
        public Object convert(final Object single) {
            if (single instanceof Vector vec) {
                float x = (float) vec.getX();
                float y = (float) vec.getY();
                float z = (float) vec.getZ();
                return new Vector3F(x, y, z);
            }
            return single;
        }

        @Override
        public Class<?> getInputType() {
            return Vector.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Vector3F.class;
        }
    };

    @Override
    public ConverterType getType() {
        return ConverterType.TO_UTILITY;
    }

}
