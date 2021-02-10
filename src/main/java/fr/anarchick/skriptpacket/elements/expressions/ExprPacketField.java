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
	"This expression has an auto-converter for %number% to int/float/long/double/short/byte [array]"
})
@Examples({
	"set field 0 of packet {_packet} to 5",
	"set field 1 of packet {_packet} to id of player"
})
@Since("1.0")

public class ExprPacketField extends SimpleExpression<Object> {

	private Expression<Integer> index;
	private Expression<PacketContainer> packet;
	private Class<?> classField;
	
	static {
       Skript.registerExpression(ExprPacketField.class, Object.class, ExpressionType.SIMPLE, "[the] field %number% of [packet] %packet%");
	}
	
	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	public boolean isSingle() {
		return classField.isArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		index = (Expression<Integer>) exprs[0];
		packet = (Expression<PacketContainer>) exprs[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the field " + index.getSingle(e) + " of packet " + packet.getSingle(e);
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		PacketContainer _packet = packet.getSingle(e);
		Number _index = index.getSingle(e);
		if ((_packet != null) && (_index != null)) {
			int i = _index.intValue();
			StructureModifier<Object> modifier = _packet.getModifier();
			int size = modifier.getValues().size();
			if ((i >= 0 ) && (i < size)) {
				Object field = modifier.readSafely(i);
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
    public void change(Event e, Object[] delta, ChangeMode mode){
    	PacketContainer _packet = packet.getSingle(e);
    	Number _index = index.getSingle(e);
		if ((_packet != null) && (_index != null)) {
			PacketManager.setField(_packet, _index.intValue(), delta);
		}
    }

    @Override
	@Nullable
    public Class<?>[] acceptChange(final ChangeMode mode) {
        if (mode == ChangeMode.SET) {
        	return new Class[] {Number[].class, Object[].class};
        }
        return null;
    }
	
}