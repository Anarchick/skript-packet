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
import ch.njol.util.coll.CollectionUtils;
import fr.anarchick.skriptpacket.util.Converter;

@Name("NMS")
@Description("Get the NMS (net.minecraft.server) from an entity/block/itemstack/world")
@Examples({
	"set {_nms} to nms of tool of player",
	"set {_nms} to nms of event-entity"
})
@Since("1.0")

public class ExprNMS extends SimpleExpression<Object> {

	private Expression<Object> expr;
	private int pattern;
	
	private final static String[] patterns;

	static {
		patterns = new String[] {
			"NMS [entity] of %entity%",
			"NMS [block] of %block%",
			"NMS [itemstack] of %itemstack%",
			"NMS [world] of %world%"
		};
		Skript.registerExpression(ExprNMS.class, Object.class, ExpressionType.SIMPLE, patterns);
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
		expr = (Expression<Object>) exprs[0];
		pattern = matchedPattern;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return patterns[pattern];
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		return CollectionUtils.array(Converter.auto(false, expr.getAll(e)));
	}
}