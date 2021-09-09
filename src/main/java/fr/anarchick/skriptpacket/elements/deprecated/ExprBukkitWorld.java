package fr.anarchick.skriptpacket.elements.deprecated;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.World;
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
import fr.anarchick.skriptpacket.SkriptPacket;

@Name("Bukkit World")
@Description("Convert an NMS (net.minecraft.server) WorldServer to his Bukkit equivalent")
@Examples("set {_world} to world from nms {_nmsWorld}")
@Since("1.2")

public class ExprBukkitWorld extends SimpleExpression<World> {

    private Expression<Object> nmsExpr;

    static {
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprBukkitWorld.class, World.class, ExpressionType.SIMPLE, "world from NMS %object%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        nmsExpr = (Expression<Object>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected World[] get(Event e) {
        Object nms = nmsExpr.getSingle(e);
        if (MinecraftReflection.isMinecraftObject(nms, "WorldServer") ) {
            World world;
            try {
                world = (World) nms.getClass().getMethod("getWorld").invoke(nms);
                return new World[] {world};
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e1) {
                Skript.exception(e1);
            }
        }
        return new World[0];
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends World> getReturnType() {
        return World.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "world from NMS " + nmsExpr.toString(e, debug);
    }
    
}