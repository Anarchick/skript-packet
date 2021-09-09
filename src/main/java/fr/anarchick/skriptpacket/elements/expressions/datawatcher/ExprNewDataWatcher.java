package fr.anarchick.skriptpacket.elements.expressions.datawatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

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

// Adapted from ThatPacketAddon

@Name("DataWatcher")
@Description("Create a new datawatcher. Used for metadata packet.")
@Examples({"on packet event play_server_entity_metadata:",
    "\tset {_dw} to new data watcher from nms field 1",
    "\tdatawatcher index 0 of {_dw} exist"})
@Since("2.0")

public class ExprNewDataWatcher extends SimpleExpression<DataWatcher> {
    
    private Optional<Expression<Entity>> entityExpr;
    private Expression<Object> nmsExpr;
    private int pattern;
    private static final String[] patterns = new String[] {
            "new data[ ]watcher [for %-entity%]",
            "new data[ ]watcher from NMS %object%"
    };

    static {
        Skript.registerExpression(ExprNewDataWatcher.class, DataWatcher.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        if (pattern == 0) {
            entityExpr = Optional.ofNullable((Expression<Entity>) exprs[0]);
        } else {
            nmsExpr = (Expression<Object>) exprs[0];
        }
        return true;
    }
    
    @Override
    protected DataWatcher[] get(Event e) {
        if (pattern == 0) {
            Optional<Entity> entityOptional = entityExpr.map(expr -> expr.getSingle(e));
            return new DataWatcher[] {
                    entityOptional.map(DataWatcher::new).orElseGet(DataWatcher::new)
            };
        } else {
            Object nms = nmsExpr.getSingle(e);
            if (nms == null) return new DataWatcher[0];
            DataWatcher dw = null;
            if (nms instanceof List) {
                List<WrappedWatchableObject> list = new ArrayList<>();
                for (Object obj : (List<?>) nms) {
                    if (MinecraftReflection.isMinecraftObject(obj, "DataWatcher$Item"))
                        list.add(new WrappedWatchableObject(obj));
                }
                dw = new DataWatcher(list);
            } else if (MinecraftReflection.isMinecraftObject(nms, "DataWatcher")) {
                dw = new DataWatcher(nms);
            }
            return new DataWatcher[] {dw};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends DataWatcher> getReturnType() {
        return DataWatcher.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        if (pattern == 0) return "new datawatcher" + entityExpr.map(expr -> " for " + expr).orElse("");
        return "new datawatcher from nms " + nmsExpr.toString(e, debug);
    }

}
