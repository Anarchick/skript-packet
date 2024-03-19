package fr.anarchick.skriptpacket.elements.expressions.utility;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.ProtocolLibrary;


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

@Name("Entity From ID")
@Description("Get the entity related to his ID in specified world")
@Examples("set {_entity} to entity from id 39 in world of player")
@Since("1.1")

public class ExprEntityFromID extends SimpleExpression<Entity> {

    private Expression<Number> idExpr;
    private Expression<World> worldExpr;
    
    static {
        Skript.registerExpression(ExprEntityFromID.class, Entity.class, ExpressionType.COMBINED,
                "entity from id %number% in %world%");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        idExpr = (Expression<Number>) exprs[0];
        worldExpr = (Expression<World>) exprs[1];
        return true;
    }
    
    @Override
    @Nullable
    protected Entity[] get(Event e) {
        Number id = idExpr.getSingle(e);
        World world = worldExpr.getSingle(e);
        if (id == null || world == null) return new Entity[0];
        return CollectionUtils.array(ProtocolLibrary.getProtocolManager().getEntityFromID(world, id.intValue()));
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }
    
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "entity from id %number% in %world%";
    }
    
}
