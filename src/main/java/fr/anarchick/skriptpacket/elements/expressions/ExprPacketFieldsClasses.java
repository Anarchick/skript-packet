package fr.anarchick.skriptpacket.elements.expressions;

import java.util.List;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;

@Name("Packet Fields Classes")
@Description({
    "Get all fields's classes of a packet.",
    "This is not intended to be use on your final code,",
    "it's only to help you to know what is inside a packet"
})
@Examples({
    "set {_packet} to new play_server_block_break_animation packet",
    "broadcast \"%all fields classes of packet {_packet}%\""
})
@Since("1.0")

public class ExprPacketFieldsClasses extends SimpleExpression<String> {

    private static Expression<PacketContainer> packetExpr;
    
    static {
       Skript.registerExpression(ExprPacketFieldsClasses.class, String.class, ExpressionType.SIMPLE,
               "[all] [packet] fields class[es] [of %-packet%]");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        if (exprs[0] != null) {
            packetExpr = (Expression<PacketContainer>) exprs[0];
            return true;
        }
         // TODO ScriptLoader must be replaced with getLoad() with Skript 2.6+
        if (!ScriptLoader.isCurrentEvent(BukkitPacketEvent.class)) {
            Skript.error("A field expression can only be used with a packet", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return true;
    }
    
    @Override
    @Nullable
    protected String[] get(Event e) {
        PacketContainer packet;
        if (packetExpr == null) {
            packet = ((BukkitPacketEvent) e).getPacket();
        } else {
            packet = packetExpr.getSingle(e);
        }
        if (packet == null) return null;
        List<Object> fields = packet.getModifier().getValues();
        String[] classNames = new String[fields.size()];
        for (int i = 0; i < fields.size() ; i++) {
            Object field = fields.get(i);
            if (field != null) {
                classNames[i] = field.getClass().getName();
            } else {
                classNames[i] = "null";
            }
        }
        return classNames;
    }
    
    @Override
    public boolean isSingle() {
        return false;
    }
    
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "all packets fields classes of " + packetExpr.toString(e, debug);
    }
    
}