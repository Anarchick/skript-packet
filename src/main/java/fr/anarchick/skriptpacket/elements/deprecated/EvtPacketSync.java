package fr.anarchick.skriptpacket.elements.deprecated;

import ch.njol.skript.Skript;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;
import fr.anarchick.skriptpacket.packets.PacketManager.Mode;

public class EvtPacketSync extends EvtPacketAbstact {
    
    static {
        Skript.registerEvent("Packet Event - Skript-Packet", EvtPacketSync.class, BukkitPacketEvent.class,
                "sync packet event %packettype%");
    }

    @Override
    Mode getMode() {
        return Mode.SYNC;
    }

    @Override
    public boolean canExecuteAsynchronously() {
        return false;
    }
    
}
