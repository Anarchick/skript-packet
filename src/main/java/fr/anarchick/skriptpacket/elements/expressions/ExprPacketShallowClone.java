package fr.anarchick.skriptpacket.elements.expressions;

import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("Shallow Clone of Packet")
@Description("Get a fast copy of a packet")
@Examples("set {_copy} to shallow clone of event-packet")
@Since("1.1")

public class ExprPacketShallowClone extends SimplePropertyExpression<PacketContainer, PacketContainer>{
    
    static {
        register(ExprPacketShallowClone.class, PacketContainer.class, "[packet] shallow (clone|copy)", "packet");
    }

    @Override
    public PacketContainer convert(PacketContainer packet) {
        return packet.shallowClone();
    }
    
    @Override
    public Class<? extends PacketContainer> getReturnType() {
        return PacketContainer.class;
    }

    @Override
    protected String getPropertyName() {
        return "shallow clone of %packet%";
    }
    
}
