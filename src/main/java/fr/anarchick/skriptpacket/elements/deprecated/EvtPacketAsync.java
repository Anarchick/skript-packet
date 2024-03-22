package fr.anarchick.skriptpacket.elements.deprecated;

import ch.njol.skript.Skript;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;
import fr.anarchick.skriptpacket.packets.PacketManager.Mode;

public class EvtPacketAsync extends EvtPacketAbstact {
    
    static {
        Skript.registerEvent("Packet Event - Skript-Packet", EvtPacketAsync.class, BukkitPacketEvent.class,
                "async packet event %packettype%")
        .description("Called when a packet of one of the specified types is being sent or"
                + " received. You can optionally specify a priority triggers with higher"
                + " priority will be called later (so high priority will come after low"
                + " priority, and monitor priority will come last)."
                + " By default, the priority is normal.")
        .examples("packet event play_server_entity_equipments:",
                "\tbroadcast \"equipment changed\"")
        .since("1.0, 1.1 (priority), 2.0 (sync/async)");
    }

    @Override
    Mode getMode() {
        return Mode.ASYNC;
    }

    @Override
    public boolean canExecuteAsynchronously() {
        return true;
    }
    
}
