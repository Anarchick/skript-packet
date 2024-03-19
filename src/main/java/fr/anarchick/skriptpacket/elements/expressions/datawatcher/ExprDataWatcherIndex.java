package fr.anarchick.skriptpacket.elements.expressions.datawatcher;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.jetbrains.annotations.NotNull;

@Name("DataWatcher Index")
@Description("Get or set a datawatcher's index. Used for metadata packet")
//@Examples("")
@Since("2.0")

public class ExprDataWatcherIndex extends SimpleExpression<Object> {
    
    private Expression<Number> indexExpr;
    private Expression<DataWatcher> dataWatcherExpr;

    static {
        Skript.registerExpression(ExprDataWatcherIndex.class, Object.class, ExpressionType.COMBINED,
                "[the] data[ ]watcher index %number% of %datawatcher%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        indexExpr = (Expression<Number>) exprs[0];
        dataWatcherExpr = (Expression<DataWatcher>) exprs[1];
        return true;
    }
    
    @Override
    protected Object @NotNull [] get(@NotNull Event event) {
        Number index = indexExpr.getSingle(event);
        DataWatcher dataWatcher = dataWatcherExpr.getSingle(event);
        if (index == null || dataWatcher == null) return new Object[0];
        return new Object[] {dataWatcher.getValue(index.intValue())};
    }

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
            return CollectionUtils.array(Object.class);
        }
        return super.acceptChange(mode);
    }
    
    @Override
    public void change(@NotNull Event e, Object @NotNull [] delta, @NotNull ChangeMode mode) {
        Number index = indexExpr.getSingle(e);
        DataWatcher dataWatcher = dataWatcherExpr.getSingle(e);
        if (index == null || dataWatcher == null) return;
        if (mode == ChangeMode.SET) {
            dataWatcher.set(index.intValue(), delta[0]);
        } else if (mode == ChangeMode.DELETE) {
            dataWatcher.remove(index.intValue());
        }
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "datawatcher index" + indexExpr.toString(e, debug) + " of " + dataWatcherExpr.toString(e, debug);
    }
    
}
