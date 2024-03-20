package fr.anarchick.skriptpacket.elements.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.comphenix.protocol.ProtocolLibrary;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.NotNull;

@Name("Packet Update Entity")
@Description("Force the update of an entity to a specific player(s)")
@Examples("packet update {_entity} for all players in world of {_entity}")
@Since("1.1")

public class EffUpdateEntity extends Effect{
    
    private Expression<Entity> entitiesExpr;
    private Expression<Player> playersExpr;
    
    static {
        Skript.registerEffect(EffUpdateEntity.class,
                "packet update %entities% for %players%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expr, int i, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        entitiesExpr = (Expression<Entity>) expr[0];
        playersExpr = (Expression<Player>) expr[1];
        return true;
    }

    @Override
    protected void execute(@NotNull Event e) {
        final Player[] players = playersExpr.getAll(e);
        final List<Player> list = Arrays.asList(players);

        for (Entity entity : entitiesExpr.getAll(e)) {
            ProtocolLibrary.getProtocolManager().updateEntity(entity, list);
        }

    }

    @Override
    public @NotNull String toString(Event e, boolean b) {
        return "packet update " + Arrays.toString(entitiesExpr.getAll(e)) + " to " + Arrays.toString(playersExpr.getAll(e));
    }
    
}
