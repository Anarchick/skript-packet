package fr.anarchick.skriptpacket.elements.expressions;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

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
import fr.anarchick.skriptpacket.util.Converter;

@Name("NMSBlockPosition")
@Description("Get the NMS (net.minecraft.server) BlockPosition from a location or a vector")
@Examples({
	"set {_nmsBlockPosition} to nms block position from location of player",
	"set {_nmsBlockPosition} to nms block position from new vector 0, 0, 0"
})
@Since("1.0")

public class ExprNMSBlockPosition extends SimpleExpression<Object> {

	private Expression<Location> loc;
	private Expression<Vector> vector;
	private int pattern;
	
	private final static String[] patterns;

	static {
		patterns = new String[] {
				"NMS block position from %location%",
				"NMS block position from %vector%"
		};
		Skript.registerExpression(ExprNMSBlockPosition.class, Object.class, ExpressionType.SIMPLE, patterns);
	}
	
	@Override
	public Class<? extends Object> getReturnType() {
		return Object.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		pattern = matchedPattern;
		if (pattern == 0) loc = (Expression<Location>) exprs[0];
		if (pattern == 1) vector = (Expression<Vector>) exprs[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return patterns[pattern];
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		if (pattern == 0) { // Location
			Location _loc = loc.getSingle(e);
			return new Object[] {Converter.toNMSBlockPosition(_loc)};
		} else if (pattern == 1) { // Vector
			Vector _vec = vector.getSingle(e);
			return new Object[] {Converter.toNMSBlockPosition(_vec)};
		}
		Object nmsPosition = null;
		return new Object[] {nmsPosition};
	}
}