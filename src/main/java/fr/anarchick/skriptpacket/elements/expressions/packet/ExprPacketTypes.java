package fr.anarchick.skriptpacket.elements.expressions.packet;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.PacketType;

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
import fr.anarchick.skriptpacket.packets.PacketManager;

@Name("Packettypes")
@Description({
    "Return all available ProtocolLib packettypes"
})
@Examples({
    "function packetSearch(s: string):",
        "\tloop all packettypes:",
            "\t\tset {_packettype} to lowercase \"%loop-value%\"",
            "\t\t{_packettype} contain {_s}",
            "\t\tsend formatted \"<suggest command:%{_packettype}%>%{_packettype}%\" to all players"
})
@Since("1.0")

public class ExprPacketTypes extends SimpleExpression<PacketType>{
    
    static {
       Skript.registerExpression(ExprPacketTypes.class, PacketType.class, ExpressionType.SIMPLE,
               "all packettypes");
    }
    
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        return true;
    }
    
    @Override
    @Nullable
    protected PacketType[] get(Event e) {
        return PacketManager.PACKETTYPES;
    }
    
    @Override
    public boolean isSingle() {
        return false;
    }
    
    @Override
    public Class<? extends PacketType> getReturnType() {
        return PacketType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "all packettypes";
    }
    
}
