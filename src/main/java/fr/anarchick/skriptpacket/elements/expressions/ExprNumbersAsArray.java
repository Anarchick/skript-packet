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

@Name("Number As Array")
@Description({
    "Convert %numbers% to int/float/long/double/short/byte array",
    "Support conversion to primitive array"
})
@Examples({
    "set {_int::*} to 5, 3 and 1 as primitive int array # Return int[]",
    "set {_integer::*} to 5, 3 and 1 as int array # Return Integer[]"
})
@Since("1.1")

public class ExprNumbersAsArray extends SimpleExpression<Object> {

    private Expression<Number> expr;
    private int mark;
    private boolean toPrimitive = false;

    static {
        String[] patterns = new String[] {
                "%numbers% as (0�Int[eger]|1�Float|2�Long|3�Double|4�Short|5�Byte) array",
                "%numbers% as primitive (0�int|1�float|2�long|3�double|4�short|5�byte) array"
        };
        Skript.registerExpression(ExprNumbersAsArray.class, Object.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        expr = (Expression<Number>) exprs[0];
        mark = parser.mark;
        toPrimitive = (matchedPattern == 1);
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        Number[] _expr = (Number[]) expr.getAll(e);
        if (_expr == null) return null;
        if (toPrimitive) {
            return new Object[] {NumberUtils.toPrimitiveArray(NumberUtils.PRIMITIVE_NUMBER.get(mark), _expr)};
        } else {
            return new Object[] {NumberUtils.toArray(NumberUtils.OBJECT_NUMBER.get(mark), _expr)};
        }
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return expr.getAll(e) + " as " + NumberUtils.PRIMITIVE_NUMBER.get(mark).getName() + " array";
    }
    
}