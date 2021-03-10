package fr.anarchick.skriptpacket.elements.expressions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

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
import fr.anarchick.skriptpacket.packets.PacketManager;
import fr.anarchick.skriptpacket.util.ArrayUtils;
import fr.anarchick.skriptpacket.util.Converter;

@Name("Packet Field")
@Description({
    "Get or set a packet field",
    "Field id start from 0 and increase by 1 for each existent field",
    "This expression has an auto-converter for %number% to [primitive] int/float/long/double/short/byte [array]"
})
@Examples({
    "set field 0 of packet {_packet} to 5",
    "set field 1 of packet {_packet} to id of player"
})
@Since("1.0")

public class ExprPacketField extends SimpleExpression<Object> {

    private Expression<Integer> indexExpr;
    private Expression<PacketContainer> packetExpr;
    private Class<?> classField;
    
    static {
       Skript.registerExpression(ExprPacketField.class, Object.class, ExpressionType.SIMPLE,
               "[packet] field %number% of %packet%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        indexExpr = (Expression<Integer>) exprs[0];
        packetExpr = (Expression<PacketContainer>) exprs[1];
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        PacketContainer packet = packetExpr.getSingle(e);
        Number index = indexExpr.getSingle(e);
        if ((packet != null) && (index != null)) {
            int i = index.intValue();
            StructureModifier<Object> modifier = packet.getModifier();
            int size = modifier.getValues().size();
            if ((i >= 0 ) && (i < size)) {
                Object field = modifier.readSafely(i);
                if (field == null) return null;
                classField = field.getClass();
                if (classField.isArray()) {
                    return ArrayUtils.unknownToObject(field);
                }
                return new Object[] {Converter.toObject(field)};
            }
        }
        return null;
    }
    
    @Override
    @Nullable
    public Class<?>[] acceptChange(final ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return new Class[] {Number[].class, Object[].class};
        }
        return null;
    }
    
    @Override
    public void change(Event e, Object[] delta, ChangeMode mode){
        PacketContainer packet = packetExpr.getSingle(e);
        Number index = indexExpr.getSingle(e);
        if ((packet != null) && (index != null)) {
            PacketManager.setField(packet, index.intValue(), delta);
        }
    }
    
    @Override
    public boolean isSingle() {
        return classField.isArray();
    }
    
    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "[packet] field " + indexExpr.getSingle(e) + " of %packet%";
    }
    
}