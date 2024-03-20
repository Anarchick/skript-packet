package fr.anarchick.skriptpacket.elements.expressions.utility;

import org.bukkit.entity.Entity;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;

@Name("ID of Entity")
@Description("Get the ID of an entity. This method return a number and not the UUID")
@Examples({"broadcast \"%entity id of player%\""})
@Since("1.0")

public class ExprEntityID extends SimplePropertyExpression<Entity, Number>{
    
    static {
        register(ExprEntityID.class, Number.class, "[entity] id", "entity");
    }

    @Override
    public Number convert(Entity ent) {
        return ent.getEntityId();
    }
    
    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "entity id";
    }
    
}