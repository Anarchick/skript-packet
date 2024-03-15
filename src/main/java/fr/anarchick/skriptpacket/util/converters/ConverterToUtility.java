package fr.anarchick.skriptpacket.util.converters;

import ch.njol.skript.Skript;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Matcher;

public enum ConverterToUtility implements Converter {

    INTLIST_TO_INTEGER_ARRAY {
        @Override
        public Object convert(final Object array) {
            return ((IntList)array).toIntArray();
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
    },

    OBJECT_TO_LIST {
        @Override
        public Object convert(final Object array) {
            return (array instanceof ArrayList) ? array : Collections.singletonList(array);
        }
    },

    // TODO check
    STRING_TO_MOJANGSON {
        @Override
        public Object convert(@Nonnull final Object single) {
            String nbt = (String) single;
            Object nms = null;
            try {
                nms = ConverterLogic.MojangsonClass.getMethod("parse", String.class).invoke(null, nbt);
            } catch (Exception ex) {
                Skript.exception(ex);
            }
            return nms;
        }
    },
    // TODO check
    NMS_CHATCOMPONENTTEXT_TO_STRING {
        @Override
        public Object convert(final Object single) {
            return WrappedChatComponent.fromHandle(single).getJson();
        }
    },

    STRING_TO_MD5BASECOMPONENT{
        @Override
        public Object convert(@Nonnull final Object single) {
            String json = (String)single;
            WrappedChatComponent wrapper;
            if (json.startsWith("{") && json.endsWith("}")) {
                wrapper = WrappedChatComponent.fromJson(json);
            } else {
                wrapper = WrappedChatComponent.fromText(json);
            }
            return ComponentConverter.fromWrapper(wrapper);
        }
    };

}
