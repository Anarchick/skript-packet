package fr.anarchick.skriptpacket.elements.expressions.nms;

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
import org.jetbrains.annotations.NotNull;

@Name("NMS Pair")
@Description("Pair object A with object B uning com.mojang.datafixers.util.Pair")
@Examples("set {_pair} to pair {_itemSlot} with {_item}")
@Since("1.0")

public class ExprPair extends SimpleExpression<Object>{

    private Expression<Object> firstExpr;
    private Expression<Object> secondExpr;
    
    static {
        Skript.registerExpression(ExprPair.class, Object.class, ExpressionType.PATTERN_MATCHES_EVERYTHING,
                "pair %object% (with|and) %object%");
     }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        firstExpr = (Expression<Object>) exprs[0];
        secondExpr = (Expression<Object>) exprs[1];
        return true;
    }
    
    @Override
    @Nullable
    protected Object @NotNull [] get(@NotNull Event e) {
        final Object first = firstExpr.getSingle(e);
        final Object second = secondExpr.getSingle(e);

        if (SkriptPacket.isReflectAddon) {
            return new Object[] {new Pair<>(ObjectWrapper.unwrapIfNecessary(first),
                    ObjectWrapper.unwrapIfNecessary(second)
            )};
        }

        return new Object[] {new Pair<>(first, second)};
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
        return "pair %object% with %object%";
    }
    
}
