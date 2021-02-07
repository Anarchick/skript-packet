package fr.anarchick.skriptpacket.elements.expressions;

import org.bukkit.event.Event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

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

@Name("New Packet")
@Description("Create a new packet from a ProtocolLib's packettype")
@Examples({
	"set {_packet} to new play_server_block_break_animation packet"
})
@Since("1.0")

public class ExprNewPacket extends SimpleExpression<PacketContainer> {
	
    private Expression<PacketType> packetType;

    static {
        Skript.registerExpression(ExprNewPacket.class, PacketContainer.class, ExpressionType.SIMPLE, "new %packettype% packet");
 	}
    
    @Override
    public Class<? extends PacketContainer> getReturnType() {
        return PacketContainer.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean isDelayed, ParseResult parser) {
        packetType = (Expression<PacketType>) exprs[0];
        return true;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "new " + packetType.toString(e, debug) + "packet";
    }

    @Override
    protected PacketContainer[] get(Event e) {
        return new PacketContainer[]{ProtocolLibrary.getProtocolManager().createPacket(packetType.getSingle(e))};
    }

}
