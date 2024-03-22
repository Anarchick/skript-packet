package fr.anarchick.skriptpacket.elements.deprecated;

import ch.njol.skript.Skript;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;
import fr.anarchick.skriptpacket.packets.PacketManager.Mode;

public class EvtPacketDefault extends EvtPacketAbstact {
    
    static {
        Skript.registerEvent("Packet Event - Skript-Packet", EvtPacketDefault.class, BukkitPacketEvent.class,
                "packet event %packettype%");
    }

    @Override
    Mode getMode() {
        return Mode.SYNC;
    }
    
}
