package fr.anarchick.skriptpacket.elements.expressions;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("Enum")
@Description("Get an Enum from a class")
@Examples({
        "set {_enum} to enum \"GAME_INFO\" from class \"net.minecraft.server.v1_16_R3.ChatMessageType\"",
        "set {_enum} to enum \"GAME_INFO\" from nms class \"ChatMessageType\""
})
@Since("1.2")

public class ExprEnum extends SimpleExpression<Object> {

    private Expression<String> enumExpr;
    private Expression<String> classExpr;
    private Expression<Object> objExpr;
    private int pattern;
    private static String[] patterns = new String[] {
            "enum %string% from class %string%",
            "enum %string% from nms class %string%",
            "enum %string% from class of %object%"
    };
    
    static {
        Skript.registerExpression(ExprEnum.class, Object.class, ExpressionType.SIMPLE, patterns);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        enumExpr = (Expression<String>) exprs[0];
        if (pattern <= 1) {
            classExpr = (Expression<String>) exprs[1];
        } else {
            objExpr = (Expression<Object>) exprs[1];
        }
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        String className = "";
        if (pattern <= 1) {
            className = classExpr.getSingle(e);
            if (className == null || className.isEmpty()) return new Object[0];
        }
        Class<?> clazz = null;
        switch (pattern) {
            case 0:
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e1) {
                    Skript.error("Failed to find class '" + className + "'");
                }
                break;
            case 1:
                String aliases[] = className.split("\\.");
                try {
                    clazz = MinecraftReflection.getMinecraftClass(className, aliases[aliases.length -1]);
                } catch (RuntimeException e1) {}
                if (clazz == null) Skript.error("Failed to find NMS class '" + className + "'");
                break;
            case 2:
                Object obj = objExpr.getSingle(e);
                if (obj != null) clazz = obj.getClass();
                break;
            default:
                break;
        }
        String enumStr = enumExpr.getSingle(e);
        if (enumStr == null || clazz == null || enumStr.isEmpty()) return new Object[0];
        Object value = null;
        try {
            value = clazz.getDeclaredMethod("valueOf", String.class).invoke(null, enumStr);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e1) {
            Skript.error("Failed to find enum '" + enumStr + "' from " + clazz);
            Skript.error("Possible enums are : '" + getEnumsNames(clazz)+ "'" );
        }
        return CollectionUtils.array(value);
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
    }
    
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }
    
    public String getEnumsNames(Class<?> clazz) {
        Object[] values = new Object[0];
        try {
            values = (Object[]) clazz.getDeclaredMethod("values").invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e2) {
        }
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
    
}
