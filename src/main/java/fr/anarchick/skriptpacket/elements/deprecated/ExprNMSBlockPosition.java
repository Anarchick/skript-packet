package fr.anarchick.skriptpacket.elements.deprecated;

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
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.util.converters.ConverterToNMS;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

@Name("NMS Block Position")
@Description("Get the NMS (net.minecraft.server) BlockPosition from a location or a vector")
@Examples({
    "set {_nmsBlockPosition} to nms block position from location of player",
    "set {_nmsBlockPosition} to nms block position from new vector 0, 0, 0"
})
@Since("1.0")

public class ExprNMSBlockPosition extends SimpleExpression<Object> {

    private Expression<Location> loc;
    private Expression<Vector> vector;
    private int pattern;
    private final static String[] patterns;

    static {
        patterns = new String[] {
                "NMS block[ ]position from %location%",
                "NMS block[ ]position from %vector%"
        };
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprNMSBlockPosition.class, Object.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        if (pattern == 0) loc = (Expression<Location>) exprs[0];
        if (pattern == 1) vector = (Expression<Vector>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        if (pattern == 0) { // Location
            Location _loc = loc.getSingle(e);
            return new Object[] {ConverterToNMS.RELATED_TO_NMS_BLOCKPOSITION.convert(_loc)};
        } else if (pattern == 1) { // Vector
            Vector _vec = vector.getSingle(e);
            return new Object[] {ConverterToNMS.RELATED_TO_NMS_BLOCKPOSITION.convert(_vec)};
        }
        return new Object[] {null};
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
    
}