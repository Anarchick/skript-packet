package fr.anarchick.skriptpacket.elements.expressions;

import java.util.List;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.events.PacketContainer;

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

    private static Expression<PacketContainer> packet;
    
    static {
       Skript.registerExpression(ExprPacketFieldsClasses.class, String.class, ExpressionType.SIMPLE,
               "[all] fields class[es] of [packet] %packet%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        packet = (Expression<PacketContainer>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected String[] get(Event e) {
        PacketContainer _packet = packet.getSingle(e);
        List<Object> fields = _packet.getModifier().getValues();
        String[] classNames = new String[fields.size()];
        for (int i = 0; i < fields.size() ; i++) {
            classNames[i] = fields.get(i).getClass().getName();
        }
        return (classNames != null) ? classNames : null;
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
        return "all packets fields classes from " + packet;
    }
    
}