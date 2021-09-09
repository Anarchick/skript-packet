package fr.anarchick.skriptpacket.elements.expressions.packet;

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
@Examples("set {_packet} to new play_server_block_break_animation packet")
@Since("1.0, 2.0 (default)")

public class ExprNewPacket extends SimpleExpression<PacketContainer> {

    private Expression<PacketType> packetTypeExpr;
    private boolean hasDefault = false;
    
    static {
        Skript.registerExpression(ExprNewPacket.class, PacketContainer.class, ExpressionType.SIMPLE,
                "new %packettype% packet [(1¦with default values)]");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int i, Kleenean isDelayed, ParseResult parser) {
        packetTypeExpr = (Expression<PacketType>) exprs[0];
        hasDefault = (parser.mark == 1);
        return true;
    }
    
    @Override
    protected PacketContainer[] get(Event e) {
        return new PacketContainer[]{ProtocolLibrary.getProtocolManager().createPacket(packetTypeExpr.getSingle(e), hasDefault)};
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends PacketContainer> getReturnType() {
        return PacketContainer.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        String str = "new " + packetTypeExpr.toString(e, debug) + "packet";
        if (hasDefault) str += " with default values";
        return str;
    }
    
}
