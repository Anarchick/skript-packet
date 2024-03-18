package fr.anarchick.skriptpacket.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Material;

@Name("Is from client")
@Description("Check if a given packet is sent by the client to the server")
@Examples("if {_packet} is from client:")
@Since("2.2.0")

public class IsFromClient extends PropertyCondition<PacketContainer> {

    static {
        register(IsFromClient.class, "from client", "packets");
    }

    @Override
    public boolean check(PacketContainer value) {
        return value.getType().isClient();
    }

    @Override
    protected String getPropertyName() {
        return "from client";
    }

}