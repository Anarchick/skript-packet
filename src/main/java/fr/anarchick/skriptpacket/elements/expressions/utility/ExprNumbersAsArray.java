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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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

    private Expression<Number> NumbersExpr;
    private boolean toPrimitive = false;
    private NumberEnums numberEnum;

    static {
        String[] patterns = new String[] {
                "%numbers% as (0¦Int[eger]|1¦Float|2¦Long|3¦Double|4¦Short|5¦Byte) array",
                "%numbers% as primitive (0¦int|1¦float|2¦long|3¦double|4¦short|5¦byte) array"
        };
        Skript.registerExpression(ExprNumbersAsArray.class, Object.class, ExpressionType.COMBINED, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parser) {
        NumbersExpr = (Expression<Number>) exprs[0];
        toPrimitive = (matchedPattern == 1);
        numberEnum = NumberEnums.get(parser.mark);
        return true;
    }
    
    @Override
    protected Object @NotNull [] get(@NotNull Event e) {
        final Number[] numbers = NumbersExpr.getAll(e);

        if (toPrimitive) {
            return new Object[] {numberEnum.toPrimitiveArray(numbers)};
        } else {
            return new Object[] {numberEnum.toArray(numbers)};
        }

    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public @NotNull Class<?> getReturnType() {
        return (toPrimitive) ? numberEnum.primitiveArrayClass : numberEnum.objectArrayClass;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        final String str = (toPrimitive) ? " as primitive " : " as ";
        return Arrays.toString(NumbersExpr.getAll(e)) + str + numberEnum.name() + " array";
    }
    
}