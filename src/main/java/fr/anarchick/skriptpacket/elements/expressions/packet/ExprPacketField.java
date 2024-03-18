package fr.anarchick.skriptpacket.elements.expressions.packet;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;
import fr.anarchick.skriptpacket.packets.PacketManager;
import fr.anarchick.skriptpacket.util.ArrayUtils;
import fr.anarchick.skriptpacket.util.converters.ConverterLogic;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Packet Field")
@Description({
    "Get or set a packet field",
    "Field id start from 0 and increase by 1 for each existent field",
    "This expression has an auto-converter. More informations on the wiki https://github.com/Anarchick/skript-packet/wiki"
})
@Examples({
    "set field 0 of packet {_packet} to 5",
    "set field 1 of packet {_packet} to id of player"
})
@Since("1.0, 1.2 (optional packet), 2.2.0(wrap option)")

public class ExprPacketField extends SimpleExpression<Object> {

    private Expression<Number> indexExpr;
    private Expression<PacketContainer> packetExpr;
    private boolean shouldWrap;

    private boolean isSingle = true;
    private PacketContainer packet;
    
    static {
       Skript.registerExpression(ExprPacketField.class, Object.class, ExpressionType.COMBINED,
               "[packet] field %number% [of %-packet%]",
               "(convert|wrap) [packet] field %number% [of %-packet%]"
       );
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        shouldWrap = ( matchedPattern == 1 );
        indexExpr = (Expression<Number>) exprs[0];

        if (indexExpr instanceof Literal<Number> litIndex) {
            int index = litIndex.getSingle().intValue();
            if (index < 0) {
                Skript.error("Indexes starts from 0");
                return false;
            }
        }

        if (exprs[1] != null) {
            packetExpr = (Expression<PacketContainer>) exprs[1];
            return true;
        }
        // ScriptLoader must be replaced with getParser() with Skript 2.6+
        return SkriptPacket.isCurrentEvent(this,"A field expression can only be used with a packet", BukkitPacketEvent.class);
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        PacketContainer packet;

        if (packetExpr == null) {
            packet = ((BukkitPacketEvent) e).getPacket();
        } else {
            packet = packetExpr.getSingle(e);
        }

        Number index = indexExpr.getSingle(e);

        if ((packet != null) && (index != null)) {
            int i = index.intValue();
            StructureModifier<Object> modifier = packet.getModifier();
            int size = modifier.size();

            if ((i < 0 ) || (i >= size)) {
                Skript.error("Available indexes for the packketype '"+PacketManager.getPacketName(packet.getType())+"' are from 0 to "+(size -1));
                return null;
            }

            Object field = modifier.readSafely(i);

            if (field == null) {
                return null;
            }

            if (field.getClass().isArray()) {
                isSingle = false;
                return ArrayUtils.unknownToObject(field);
            }

            Object obj = ConverterLogic.toObject(field);

            if (shouldWrap) {
                obj = ConverterLogic.toBukkit(obj);
            }

            return new Object[] {obj};
        }

        return null;
    }
    
    @Override
    @Nullable
    public Class<?>[] acceptChange(final ChangeMode mode) {
        if ( mode == ChangeMode.SET ) {
            return new Class[] {Number[].class, Object[].class};
        }
        return null;
    }
    
    @Override
    public void change(Event e, Object[] delta, ChangeMode mode) {
        if (mode != ChangeMode.SET) return;
        if (packetExpr == null) {
            packet = ((BukkitPacketEvent) e).getPacket();
        } else {
            packet = packetExpr.getSingle(e);
        }
        Number index = indexExpr.getSingle(e);
        if ((packet != null) && (index != null)) {
            PacketManager.setField(packet, index.intValue(), ArrayUtils.toArray(delta));
        }
    }
    
    @Override
    public boolean isSingle() {
        return isSingle;
    }
    
    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {

        if (shouldWrap) {
            return "(convert|wrap) [packet] field %number% [of %-packet%]";
        } else {
            return "[packet] field %number% [of %-packet%]";
        }

    }
    
}