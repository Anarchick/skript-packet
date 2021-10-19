package fr.anarchick.skriptpacket.elements.deprecated;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;

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

@Name("Location From NMS")
@Description("Convert an NMS (net.minecraft.server) BlockPosition to a Bukkit Location")
@Examples("set {_loc} to location from nms {_nmsblockPosition} in world of player")
@Since("1.2")

public class ExprLocationFromNMS extends SimpleExpression<Location> {

    private Expression<Object> nmsExpr;
    private Expression<World> worldExpr;

    static {
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprLocationFromNMS.class, Location.class, ExpressionType.SIMPLE,
                "location from NMS %object% in %world%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        nmsExpr = (Expression<Object>) exprs[0];
        worldExpr = (Expression<World>) exprs[1];
        return true;
    }
    
    @Override
    @Nullable
    protected Location[] get(Event e) {
        Object nms = nmsExpr.getSingle(e);
        World world = worldExpr.getSingle(e);
        if (MinecraftReflection.isBlockPosition(nms) ) {
            return new Location[] { BlockPosition.getConverter().getSpecific(nms).toLocation(world)};
        } else if (nms instanceof Location) {
            return new Location[] {(Location) nms};
        }
        return new Location[0];
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "location from NMS " + nmsExpr.toString(e, debug) +" in "+ worldExpr.toString(e, debug);
    }
    
}