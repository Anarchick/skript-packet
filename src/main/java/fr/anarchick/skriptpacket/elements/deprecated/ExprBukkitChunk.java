package fr.anarchick.skriptpacket.elements.deprecated;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Chunk;
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

@Name("Bukkit Chunk")
@Description("Convert an NMS (net.minecraft.server) Chunk to his Bukkit equivalent")
@Examples("set {_chunk} to chunk from nms {_nmsChunk}")
@Since("1.2")

public class ExprBukkitChunk extends SimpleExpression<Chunk> {

    private Expression<Object> nmsExpr;

    static {
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprBukkitChunk.class, Chunk.class, ExpressionType.SIMPLE,
                "chunk from NMS %object%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        nmsExpr = (Expression<Object>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Chunk[] get(Event e) {
        Object nms = nmsExpr.getSingle(e);
        if (MinecraftReflection.isMinecraftObject(nms, "Chunk") ) {
            Chunk chunk;
            try {
                chunk = (Chunk) nms.getClass().getMethod("getBukkitChunk").invoke(nms);
                return new Chunk[] {chunk};
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e1) {
                Skript.exception(e1);
            }
        }
        return new Chunk[0];
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends Chunk> getReturnType() {
        return Chunk.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "chunk from NMS " + nmsExpr.toString(e, debug);
    }
    
}