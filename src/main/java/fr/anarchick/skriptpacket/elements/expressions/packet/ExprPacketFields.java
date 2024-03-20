package fr.anarchick.skriptpacket.elements.expressions.packet;

import fr.anarchick.skriptpacket.util.converters.ConverterLogic;
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
import fr.anarchick.skriptpacket.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;

@Name("Packet Fields")
@Description("Get all packet fields, can't be set")
@Examples("set {_fields::*} to all fields of event-packet")
@Since("1.0, 2.2.0(wrap option)")

public class ExprPacketFields extends SimpleExpression<Object> {
    
    private static Expression<PacketContainer> packetExpr;
    private boolean shouldWrap;
    
    static {
       Skript.registerExpression(ExprPacketFields.class, Object.class, ExpressionType.SIMPLE,
               "[all] [packet] fields [of %-packet%]",
               "[all] (convert|wrap) [packet] fields [of %-packet%]");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
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
    protected Object @NotNull [] get(@NotNull Event e) {
        final PacketContainer packet;

        if (packetExpr == null) {
            packet = ((BukkitPacketEvent) e).getPacket();
        } else {
            packet = packetExpr.getSingle(e);
        }

        if (packet != null) {
            final StructureModifier<Object> modifier = packet.getModifier();
            int size = modifier.getValues().size();
            final Object[] values = new Object[size];

            for (int i = 0; i < size ; i++) {
                final Object field = modifier.readSafely(i);

                if (field == null) {
                    values[i] = null;
                } else if (field.getClass().isArray()) {
                    values[i]  = ArrayUtils.unknownToObject(field);
                } else {
                    values[i] = ConverterLogic.toObject(field);
                }

                if (shouldWrap) {
                    values[i] = ConverterLogic.toBukkit(values[i]);
                }

            }

            return values;
        }

        return new Object[0];
    }
    
    @Override
    public boolean isSingle() {
        return false;
    }
    
    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {

        if (shouldWrap) {
            return "all wrap packet fields of " + packetExpr.toString(e, debug);
        } else {
            return "all packet fields of " + packetExpr.toString(e, debug);
        }

    }
    
}