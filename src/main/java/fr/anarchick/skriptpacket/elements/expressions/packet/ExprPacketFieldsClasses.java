package fr.anarchick.skriptpacket.elements.expressions.packet;

import fr.anarchick.skriptpacket.util.converters.Converter;
import fr.anarchick.skriptpacket.util.converters.ConverterLogic;
import fr.anarchick.skriptpacket.util.converters.ConverterToUtility;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

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
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;

@Name("Packet Fields Classes")
@Description({
    "Get all fields's classes of a packet.",
    "This is not intended to be use on your final code,",
    "it's only to help you to know what is inside a packet"
})
@Examples({
    "set {_packet} to new play_server_block_break_animation packet",
    "broadcast \"%all wrap fields classes of packet {_packet}%\""
})
@Since("1.0, 2.2.0(wrap option)")

public class ExprPacketFieldsClasses extends SimpleExpression<String> {

    private static Expression<PacketContainer> packetExpr;
    private boolean shouldWrap;
    
    static {
       Skript.registerExpression(ExprPacketFieldsClasses.class, String.class, ExpressionType.PROPERTY,
               "[all] [packet] fields class[es] [of %-packet%]",
               "[all] (convert|wrap) [packet] fields class[es] [of %-packet%]");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        shouldWrap = ( matchedPattern == 1 );
        if (exprs[0] != null) {
            packetExpr = (Expression<PacketContainer>) exprs[0];
            return true;
        }
        // ScriptLoader must be replaced with getParser() with Skript 2.6+
        return SkriptPacket.isCurrentEvent(this, "A field expression can only be used with a packet", BukkitPacketEvent.class);
    }
    
    @Override
    @Nullable
    protected String[] get(Event e) {
        final PacketContainer packet;
        if (packetExpr == null) {
            packet = ((BukkitPacketEvent) e).getPacket();
        } else {
            packet = packetExpr.getSingle(e);
        }
        if (packet == null) return null;
        
        final StructureModifier<Object> modifier = packet.getModifier();
        final String[] classNames = new String[modifier.size()];
        for (int i = 0; i < classNames.length ; i++) {
            Class<?> fieldClass = modifier.getField(i).getType();
            if (shouldWrap) {
                Converter converter = ConverterLogic.getConverterToBukkit(fieldClass);
                if (ConverterToUtility.HIMSELF != converter
                        && converter.getOutputType() != Object.class ) {
                    fieldClass = converter.getOutputType();
                }
            }

            classNames[i] = fieldClass.getName();
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

        if (shouldWrap) {
            return "all wrap packets fields classes of " + packetExpr.toString(e, debug);
        } else {
            return "all packets fields classes of " + packetExpr.toString(e, debug);
        }

    }
    
}