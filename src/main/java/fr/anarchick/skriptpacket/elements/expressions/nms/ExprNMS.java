package fr.anarchick.skriptpacket.elements.expressions.nms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.EventValues;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import fr.anarchick.skriptpacket.util.Converter;

@Name("NMS")
@Description("Get the NMS (net.minecraft.server) from a convertable object or invert")
@Examples({
    "set {_nms} to nms of player",
    "set {_bukkit} to wrap {_nms}"
})
@Since("2.0")

public class ExprNMS extends SimpleExpression<Object> {

    private Expression<?> expr;
    private int pattern;
    private final static String[] patterns = new String[] {
            "NMS (of|from) %location/block/itemtype/itemstack/chunk/biome/entity/world/datawatcher/vector%",
            "(convert|wrap) [from NMS] %object%"
    };
    private Object result = null;

    static {
        Skript.registerExpression(ExprNMS.class, Object.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        expr = exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        Object obj = expr.getSingle(e);
        switch (pattern) {
            case 0:
                result = Converter.unwrap(obj);
                break;
            case 1:
                result = Converter.wrap(obj);
                if (result instanceof Location) {
                    @Nullable World world = EventValues.getEventValue(e, World.class, 0);
                    if (world != null) ((Location) result).setWorld(world);
                }
                break;
            default:
                break;
        }
        return CollectionUtils.array(result);
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<?> getReturnType() {
        return (result == null) ? Object.class : result.getClass();
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }
    
}