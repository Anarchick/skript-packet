package fr.anarchick.skriptpacket.elements.deprecated;

import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.anarchick.skriptpacket.SkriptPacket;

@Name("Deep Clone of Packet")
@Description("Get a full copy of a packet")
@Examples("set {_copy} to deep clone of event-packet")
@Since("1.1")

public class ExprPacketDeepClone extends SimplePropertyExpression<PacketContainer, PacketContainer>{
    
    static {
        if (SkriptPacket.enableDeprecated) register(ExprPacketDeepClone.class, PacketContainer.class, "[packet] deep (clone|copy)", "packet");
    }

    @Override
    public PacketContainer convert(PacketContainer packet) {
        return packet.deepClone();
    }
    
    @Override
    public Class<? extends PacketContainer> getReturnType() {
        return PacketContainer.class;
    }

    @Override
    protected String getPropertyName() {
        return "deep clone of %packet%";
    }
    
}
