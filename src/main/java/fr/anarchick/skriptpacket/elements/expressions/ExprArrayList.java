package fr.anarchick.skriptpacket.elements.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.btk5h.skriptmirror.ObjectWrapper;

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
import fr.anarchick.skriptpacket.SkriptPacket;

@Name("ArrayList")
@Description("Create a java ArrayList from objects")
@Examples({
	"set {_arraylist} to all players as arraylist"
})
@Since("1.0")

public class ExprArrayList extends SimpleExpression<Object> {

	private Expression<Object> expr;

	static {
		Skript.registerExpression(ExprArrayList.class, Object.class, ExpressionType.SIMPLE, "%objects% as arraylist");
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
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "%Objects% as arraylist";
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		ArrayList<Object> Array = new ArrayList<Object>();
		if (SkriptPacket.isReflectAddon) {
			for (Object _expr : expr.getAll(e)) {
				Array.add(ObjectWrapper.unwrapIfNecessary(_expr));
			}
		} else {
			List<Object> list = Arrays.asList(expr.getAll(e));
			Array.addAll(list);
		}
		return CollectionUtils.array(Array);
	}
}