package fr.anarchick.skriptpacket.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import ch.njol.skript.Skript;

public class Utils {
    
    public static Object getEnum(Class<?> clazz, String enumStr, boolean debug) {
        if (enumStr == null || clazz == null || enumStr.isEmpty()) return new Object[0];
        try {
            return clazz.getDeclaredMethod("valueOf", String.class).invoke(null, enumStr);
        } catch (Exception ex) {
            if (debug) {
                Skript.error("Failed to find enum '"+enumStr+"' from "+clazz);
                Skript.error("Possible enums are : '"+getEnumsNames(clazz)+"'" );
            }
        }
        return null;
    }
    
    public static String getEnumsNames(Class<?> clazz) {
        Object[] values = new Object[0];
        try {
            values = (Object[]) clazz.getDeclaredMethod("values").invoke(null);
        } catch (Exception ignored) {}
        if (values.length == 0) return "";
        if (values.length > 1) {
            StringBuilder builder = new StringBuilder();
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
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(matcher);
        if (!m.find()) return "";
        int count = m.groupCount();
        if (group <= 0 || group > count) return "";
        return m.group(group);
    }
    
    public static Number[] EntitiesIDs(Entity[] entities) {
        Number[] ids = new Number[entities.length];
        for (int i = 0 ; i < entities.length ; i++ ) {
            final Entity ent = entities[i];
            ids[i] = ent.getEntityId();
        }
        return ids;
    }
    
    public static boolean classExist(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
