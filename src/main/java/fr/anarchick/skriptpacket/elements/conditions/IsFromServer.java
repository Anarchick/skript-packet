package fr.anarchick.skriptpacket.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.comphenix.protocol.events.PacketContainer;

@Name("Is from server")
@Description("Check if a given packet is sent by the server to the client")
@Examples("if {_packet} is from server:")
@Since("2.2.0")

public class IsFromServer extends PropertyCondition<PacketContainer> {

    static {
        register(IsFromServer.class, "from server", "packets");
    }

    @Override
    public boolean check(PacketContainer value) {
        return value.getType().isServer();
    }

    @Override
    protected String getPropertyName() {
        return "from server";
    }

}