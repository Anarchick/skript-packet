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

@Name("DataWatcher Index")
@Description("Get or set a datawatcher's index. Used for metadata packet")
//@Examples("")
@Since("2.0")

public class ExprDataWatcherIndex extends SimpleExpression<Object> {
    
    private Expression<Number> indexExpr;
    private Expression<DataWatcher> dataWatcherExpr;

    static {
        Skript.registerExpression(ExprDataWatcherIndex.class, Object.class, ExpressionType.SIMPLE,
                "[the] datawatcher index %number% of %datawatcher%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        indexExpr = (Expression<Number>) exprs[0];
        dataWatcherExpr = (Expression<DataWatcher>) exprs[1];
        return true;
    }
    
    @Override
    protected Object[] get(Event event) {
        Number index = indexExpr.getSingle(event);
        DataWatcher dataWatcher = dataWatcherExpr.getSingle(event);
        if (index == null || dataWatcher == null) return new Object[0];
        return new Object[] {dataWatcher.getObject(index.intValue())};
    }

    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
            return CollectionUtils.array(Object.class);
        }
        return null;
    }
    
    @Override
    public void change(Event e, Object[] delta, ChangeMode mode) {
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
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "datawatcher index" + indexExpr.toString(e, debug) + " of " + dataWatcherExpr.toString(e, debug);
    }
    
}
