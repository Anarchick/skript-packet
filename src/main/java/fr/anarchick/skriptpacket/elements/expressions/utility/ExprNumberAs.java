package fr.anarchick.skriptpacket.elements.expressions.utility;

import fr.anarchick.skriptpacket.util.NumberEnums;
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

@Name("Number As")
@Description("Convert a %number% to int/float/long/double/short/byte")
@Examples("set {_byte} to 5 as byte")
@Since("1.0")

public class ExprNumberAs extends SimpleExpression<Number> {

    private Expression<Number> expr;
    private int mark;

    static {
        Skript.registerExpression(ExprNumberAs.class, Number.class, ExpressionType.SIMPLE, 
                "%number% as [primitive] (0¦int[eger]|1¦float|2¦long|3¦double|4¦short|5¦byte)");
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        expr = (Expression<Number>) exprs[0];
        mark = parser.mark;
        return true;
    }
    
    @Override
    @Nullable
    protected Number[] get(Event e) {
        Number _expr = expr.getSingle(e);
        if (_expr == null) return null;
        return CollectionUtils.array(
                NumberEnums.get(mark).toSingle(_expr)
        );
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return expr.getSingle(e) + " as " + NumberEnums.get(mark).name();
    }
    
}