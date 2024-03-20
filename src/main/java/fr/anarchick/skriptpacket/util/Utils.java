package fr.anarchick.skriptpacket.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import ch.njol.skript.Skript;

public class Utils {
    
    public static Object getEnum(Class<?> clazz, String enumStr) {
        if (enumStr == null || clazz == null || enumStr.isEmpty()) {
            return new Object[0];
        }

        if (!clazz.isEnum()) {
            Skript.error(clazz + " is not an enum Class");
            return null;
        }

        for (Object enumConstant : clazz.getEnumConstants()) {

            if ( ((Enum<?>)enumConstant).name().equals(enumStr) ) {
                return enumConstant;
            }

        }

        Skript.error("Failed to find enum '"+enumStr+"' from "+clazz);
        Skript.error("Possible enums are : '"+getEnumsNames(clazz)+"'" );
        return null;
    }
    
    public static String getEnumsNames(Class<?> clazz) {
        Object[] values = new Object[0];

        try {
            values = (Object[]) clazz.getDeclaredMethod("values").invoke(null);
        } catch (Exception ignored) {}

        if (values.length == 0) {
            return "";
        }

        if (values.length > 1) {
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < values.length - 1; i++) {
                builder.append(values[i]);
                builder.append(", ");
            }

            builder.append(values[values.length - 1]);
            return builder.toString();
        } else {
            return values[0].toString();
        }
    }
    
    public static String regexGroup(String pattern, String matcher, int group) {
        final Pattern p = Pattern.compile(pattern);
        final Matcher m = p.matcher(matcher);

        if (!m.find()) {
            return "";
        }

        int count = m.groupCount();

        if (group <= 0 || group > count) {
            return "";
        }

        return m.group(group);
    }
    
    public static Number[] EntitiesIDs(Entity[] entities) {
        final Number[] ids = new Number[entities.length];

        for (int i = 0 ; i < entities.length ; i++ ) {
            final Entity ent = entities[i];
            ids[i] = ent.getEntityId();
        }

        return ids;
    }

}
