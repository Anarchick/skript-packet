package fr.anarchick.skriptpacket.elements.expressions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.btk5h.skriptmirror.ObjectWrapper;
import com.mojang.datafixers.util.Pair;

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

@Name("NMS Pair")
@Description({
	"Pair object A with object B uning com.mojang.datafixers.util.Pair"
})
@Examples({
	"set {_pair} to pair {_itemSlot} with {_item}"
})
@Since("1.0")

public class ExprPair extends SimpleExpression<Object>{

    private Expression<Object> first;
    private Expression<Object> second;

    static {
        Skript.registerExpression(ExprPair.class, Object.class, ExpressionType.PATTERN_MATCHES_EVERYTHING, "pair %object% (with|and) %object%");
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
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		first = (Expression<Object>) exprs[0];
		second = (Expression<Object>) exprs[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "pair %object% with %object%";
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		Object _first = first.getSingle(e);
		Object _second = second.getSingle(e);
		if (SkriptPacket.isReflectAddon) {
			return new Object[] {new Pair<Object, Object>(ObjectWrapper.unwrapIfNecessary(_first), ObjectWrapper.unwrapIfNecessary(_second))} ;
		}
		return new Object[] {new Pair<Object, Object>(_first, _second)};
	}

}
