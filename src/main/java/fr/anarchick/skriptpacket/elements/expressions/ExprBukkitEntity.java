package fr.anarchick.skriptpacket.elements.expressions;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;

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

@Name("Bukkit Entity")
@Description("Convert an NMS (net.minecraft.server) Entity to his Bukkit equivalent")
@Examples({
    "set {_player} to entity from nms {_nmsPlayer}",
    "set {_entity} to entity from nms {_nmsEntity}"
})
@Since("1.2")

public class ExprBukkitEntity extends SimpleExpression<Entity> {

    private Expression<Object> nmsExpr;

    static {
        Skript.registerExpression(ExprBukkitEntity.class, Entity.class, ExpressionType.SIMPLE, "entity from NMS %object%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        nmsExpr = (Expression<Object>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Entity[] get(Event e) {
        Object nms = nmsExpr.getSingle(e);
        if (MinecraftReflection.isMinecraftEntity(nms) ) { // include Player
            return new Entity[] {(Entity) MinecraftReflection.getBukkitEntity(nms)};
        }
        return new Entity[0];
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
        return "entity from NMS " + nmsExpr.toString(e, debug);
    }
    
}