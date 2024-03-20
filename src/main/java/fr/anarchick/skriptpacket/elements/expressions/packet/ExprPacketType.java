package fr.anarchick.skriptpacket.elements.expressions.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;

@Name("Packettype")
@Description("Return the packet type of a %packet%")
@Examples("set {_packettype} to packetttype of event-packet")
@Since("1.1")

public class ExprPacketType extends SimplePropertyExpression<PacketContainer, PacketType>{
    
    static {
        register(ExprPacketType.class, PacketType.class, "packettype", "packet");
    }

    @Override
    public PacketType convert(final PacketContainer packet) {
        return packet.getType();
    }
    
    @Override
    public @NotNull Class<? extends PacketType> getReturnType() {
        return PacketType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "packettype of %packet%";
    }
    
}
