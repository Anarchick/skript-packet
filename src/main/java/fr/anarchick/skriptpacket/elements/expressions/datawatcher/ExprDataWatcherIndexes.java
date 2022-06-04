package fr.anarchick.skriptpacket.elements.expressions.datawatcher;

import java.util.Iterator;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.EmptyIterator;

@Name("DataWatcher Indexes")
@Description("Get all indexes of a datawatcher")
//@Examples("")
@Since("2.0")

public class ExprDataWatcherIndexes extends SimpleExpression<Number> {
    
    private Expression<DataWatcher> dataWatcherExpr;
    
    private static final String[] patterns = new String[] {
            "[all] datawatcher (indexes|indices) of %datawatcher%",
            "[all] %datawatcher%'s datawatcher (indexes|indices)"
    };

    static {
        Skript.registerExpression(ExprDataWatcherIndexes.class, Number.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        dataWatcherExpr = (Expression<DataWatcher>) exprs[0];
        return true;
    }
    
    @Override
    protected Number[] get(Event event) {
        DataWatcher dataWatcher = dataWatcherExpr.getSingle(event);
        if (dataWatcher == null) return new Number[0];
        return dataWatcher.getIndexes().toArray(new Number[0]);
    }
    
    @Override
    public Iterator<Integer> iterator(Event event) {
        DataWatcher dataWatcher = dataWatcherExpr.getSingle(event);
        if (dataWatcher == null) {
            return new EmptyIterator<>();
        }
        return dataWatcher.getIndexes().iterator();
    }
    
    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "all datawatcher indexes of " + dataWatcherExpr.toString(e, debug);
    }
    
}
