package fr.anarchick.skriptpacket.elements.expressions;

import org.bukkit.event.Event;
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
import fr.anarchick.skriptpacket.util.NumberUtils;

@Name("Number As")
@Description({
	"Convert a %number% to int/float/long/double/short/byte",
	"Support conversion to array"
})
@Examples({
	"set {_byte} to 5 as byte",
	"set {_byte::*} to 5, 3, 1 as short array"
})
@Since("1.0")

public class ExprNumberAs extends SimpleExpression<Number> {

	private Expression<Object> expr;
	private int mark;
	private boolean isArray = false;

	static {
		String[] patterns = new String[] {
			"%number% as (0¦int|1¦float|2¦long|3¦double|4¦short|5¦byte)",
			"%numbers% as (0¦int|1¦float|2¦long|3¦double|4¦short|5¦byte) array"
		};
		Skript.registerExpression(ExprNumberAs.class, Number.class, ExpressionType.SIMPLE, patterns);
	}
	
	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public boolean isSingle() {
		return !isArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		expr = (Expression<Object>) exprs[0];
		mark = parser.mark;
		isArray = (matchedPattern == 1);
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "parse " + expr.getSingle(e) + " as " + NumberUtils.PRIMITIVE_NUMBER.get(mark).getName();
	}

	@Override
	@Nullable
	protected Number[] get(Event e) {
		Number[] _expr = (Number[]) expr.getAll(e);
		if (_expr == null) return null;
		return NumberUtils.convert(NumberUtils.PRIMITIVE_NUMBER.get(mark), isArray, _expr);
	}
}