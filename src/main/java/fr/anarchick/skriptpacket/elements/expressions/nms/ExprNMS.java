package fr.anarchick.skriptpacket.elements.expressions.nms;

import fr.anarchick.skriptpacket.util.converters.ConverterLogic;
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
import org.jetbrains.annotations.NotNull;

@Name("NMS")
@Description("Get the NMS (net.minecraft.server) from a convertible object or invert")
@Examples({
    "set {_nms} to nms of player",
    "set {_bukkit} to wrap from {_nms}"
})
@Since("2.0, 2.2.0 remove %chunk% and add %block data/material/slot/string% + wrap pattern modified")

public class ExprNMS extends SimpleExpression<Object> {

    private Expression<?> expr;
    private int pattern;
    private final static String[] patterns = new String[] {
            "NMS (of|from) %location/block/blockdata/itemtype/itemstack/material/slot/biome/entity/world/datawatcher/vector/string%",
            "(convert|wrap) from nms %object%"
    };
    private Object result = null;

    static {
        Skript.registerExpression(ExprNMS.class, Object.class, ExpressionType.COMBINED, patterns);
    }
    
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        pattern = matchedPattern;
        expr = exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Object @NotNull [] get(@NotNull Event e) {
        Object obj = expr.getSingle(e);
        switch (pattern) {
            case 0 -> result = ConverterLogic.toNMS(obj);
            case 1 -> {
                result = ConverterLogic.toBukkit(obj);
                if (result instanceof Location) {
                    @Nullable World world = EventValues.getEventValue(e, World.class, 0);
                    if (world != null) ((Location) result).setWorld(world);
                }
            }
            default -> {
            }
        }
        return CollectionUtils.array(result);
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public @NotNull Class<?> getReturnType() {
        return (result == null) ? Object.class : result.getClass();
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }
    
}