package fr.anarchick.skriptpacket.elements.expressions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("Packettype")
@Description("Return the packet type of a %packet%")
@Examples("set {_packettype} to packetttype of event-packet")
@Since("1.1")

public class ExprPacketType extends SimplePropertyExpression<PacketContainer, PacketType>{
    
    static {
        register(ExprPacketType.class, PacketType.class, "packettype", "packet");
    }

    @Override
    public PacketType convert(PacketContainer packet) {
        return packet.getType();
    }
    
    @Override
    public Class<? extends PacketType> getReturnType() {
        return PacketType.class;
    }

    @Override
    protected String getPropertyName() {
        return "packettype of %packet%";
    }
    
}
