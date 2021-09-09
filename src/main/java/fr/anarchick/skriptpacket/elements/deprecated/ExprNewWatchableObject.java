package fr.anarchick.skriptpacket.elements.deprecated;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.anarchick.skriptpacket.SkriptPacket;

public class ExprNewWatchableObject extends SimpleExpression<WrappedWatchableObject> {
    
    private Expression<Number> indexExpr;
    private Expression<Object> objExpr;
    private int pattern;
    private static final String[] patterns = new String[] {
            "new watchable [object] from NMS %object%",
            "new watchable [object] with index %number% and value %object%"
    };

    static {
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprNewWatchableObject.class, WrappedWatchableObject.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        if (pattern == 0) {
            objExpr = (Expression<Object>) exprs[0];
        } else {
            indexExpr = (Expression<Number>) exprs[0];
            objExpr = (Expression<Object>) exprs[1];
        }
        return true;
    }
    
    @Override
    protected WrappedWatchableObject[] get(Event event) {
        Object obj = objExpr.getSingle(event);
        if (obj == null) return new WrappedWatchableObject[0];
        WrappedWatchableObject wrapper = null;
        if (pattern == 0) {
            wrapper = new WrappedWatchableObject(obj);
        } else {
            Number index = indexExpr.getSingle(event);
            if (index == null) return new WrappedWatchableObject[0];
            wrapper = new WrappedWatchableObject(index.intValue(), obj);
        }
        return new WrappedWatchableObject[] {wrapper};
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WrappedWatchableObject> getReturnType() {
        return WrappedWatchableObject.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }
    
}
