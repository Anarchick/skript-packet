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
import org.jetbrains.annotations.NotNull;

@Name("DataWatcher Indexes")
@Description("Get all indexes of a data watcher")
//@Examples("")
@Since("2.0")

public class ExprDataWatcherIndexes extends SimpleExpression<Number> {
    
    private Expression<DataWatcher> dataWatcherExpr;
    
    private static final String[] patterns = new String[] {
            "[all] data[ ]watcher (indexes|indices) of %datawatcher%",
            "[all] %datawatcher%'s data[ ]watcher (indexes|indices)"
    };

    static {
        Skript.registerExpression(ExprDataWatcherIndexes.class, Number.class, ExpressionType.PROPERTY, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        dataWatcherExpr = (Expression<DataWatcher>) exprs[0];
        return true;
    }
    
    @Override
    protected Number @NotNull [] get(@NotNull Event event) {
        DataWatcher dataWatcher = dataWatcherExpr.getSingle(event);
        if (dataWatcher == null) return new Number[0];
        return dataWatcher.getIndexes().toArray(Number[]::new);
    }
    
    @Override
    public Iterator<Integer> iterator(@NotNull Event event) {
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
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "all data watcher indexes of " + dataWatcherExpr.toString(e, debug);
    }
    
}
