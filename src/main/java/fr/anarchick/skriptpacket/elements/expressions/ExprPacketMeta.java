package fr.anarchick.skriptpacket.elements.expressions;

import java.util.Optional;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@Name("Packet Meta")
@Description({
    "Get or set a packet meta",
    "Meta are extra-datas that can be added to a packet"
})
@Examples({
    "set meta \"meta_name\" of packet {_packet} to (1, 2 and 3)",
    "set {_meta::*} to meta \"meta_name\" of packet {_packet}"
})
@Since("1.1")

public class ExprPacketMeta extends SimpleExpression<Object> {

    private Expression<String> metaExpr;
    private Expression<PacketContainer> packetExpr;
    
    static {
       Skript.registerExpression(ExprPacketMeta.class, Object.class, ExpressionType.SIMPLE,
               "[the] meta %string% of [packet] %packet%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        metaExpr = (Expression<String>) exprs[0];
        packetExpr = (Expression<PacketContainer>) exprs[1];
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        String metaID = metaExpr.getSingle(e);
        PacketContainer packet = packetExpr.getSingle(e);
        if ((packet != null) && (metaID != null)) {
            Optional<Object> meta = packet.getMeta(metaID);
            if (meta.isPresent()) {
                return (@Nullable Object[]) meta.get();
            }
        }
        return null;
    }
    
    @Override
    @Nullable
    public Class<?>[] acceptChange(final ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
            case RESET:
                return new Class[] {Object[].class};
            default:
                return null;
        }
    }
    
    @Override
    public void change(Event e, Object[] delta, ChangeMode mode){
        PacketContainer packet = packetExpr.getSingle(e);
        String metaID = metaExpr.getSingle(e);
        if ((packet != null) && (metaID != null)) {
            if (mode == ChangeMode.SET) {
                packet.setMeta(metaID, delta);
            } else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET){
                packet.removeMeta(metaID);
            }
                
        }
    }
    
    @Override
    public boolean isSingle() {
        return false;
    }
    
    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "the meta " + metaExpr.getSingle(e) + " of packet " + packetExpr.getSingle(e);
    }
    
}