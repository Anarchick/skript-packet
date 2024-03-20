package fr.anarchick.skriptpacket.elements.expressions.datawatcher;

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
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("DataWatcher")
@Description("Create a new datawatcher. Used for metadata packet. The data watcher is linked to the packet and you don't need to set the packet field to it")
@Examples({
        "on packet event play_server_entity_metadata:",
        "\tset {_dw} to new data watcher from event-packet",
        "\tdatawatcher index 0 of {_dw} exist"
})
@Since("2.0, 2.2.0 need a packet")

public class ExprNewDataWatcher extends SimpleExpression<DataWatcher> {

    private Expression<PacketContainer> packetExpr;
    private int pattern;
    private static final String[] patterns = new String[] {
            "new data[ ]watcher from %packet%"
    };

    static {
        Skript.registerExpression(ExprNewDataWatcher.class, DataWatcher.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        packetExpr = (Expression<PacketContainer>) exprs[0];
        pattern = matchedPattern;
        return true;
    }
    
    @Override
    protected DataWatcher @NotNull [] get(@NotNull Event e) {
        final PacketContainer packet = packetExpr.getSingle(e);

        if (packet == null) {
            return new DataWatcher[0];
        }

        return new DataWatcher[] {new DataWatcher(packet)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends DataWatcher> getReturnType() {
        return DataWatcher.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }

}
