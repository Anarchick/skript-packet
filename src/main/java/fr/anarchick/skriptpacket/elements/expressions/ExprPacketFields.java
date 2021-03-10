package fr.anarchick.skriptpacket.elements.expressions;

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
import fr.anarchick.skriptpacket.util.ArrayUtils;
import fr.anarchick.skriptpacket.util.Converter;

@Name("Packet Fields")
@Description("Get all packet fields, can't be set")
@Examples("set {_fields::*} to all fields of packet event-packet")
@Since("1.0")

public class ExprPacketFields extends SimpleExpression<Object> {
    
    private static Expression<PacketContainer> packet;
    
    static {
       Skript.registerExpression(ExprPacketFields.class, Object.class, ExpressionType.SIMPLE,
               "[all] fields of [packet] %packet%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        packet = (Expression<PacketContainer>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        PacketContainer _packet = packet.getSingle(e);
        if (_packet != null) {
            StructureModifier<Object> modifier = _packet.getModifier();
            int size = modifier.getValues().size();
            Object[] values = new Object[size];
            for (int i = 0; i < size ; i++) {
                Object field = modifier.readSafely(i);
                if (field.getClass().isArray()) {
                    values[i]  = ArrayUtils.unknownToObject(field);
                } else {
                    values[i] = Converter.toObject(field);
                }
            }
            return values;
        }
        return null;
    }
    
    @Override
    public boolean isSingle() {
        return false;
    }
    
    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "all fields of packet " + packet;
    }
    
}