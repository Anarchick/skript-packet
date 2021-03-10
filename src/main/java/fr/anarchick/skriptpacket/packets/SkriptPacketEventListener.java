package fr.anarchick.skriptpacket.packets;


import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;

import fr.anarchick.skriptpacket.SkriptPacket;

public class SkriptPacketEventListener {
    
    // Cumulative, need a way to reset on a script reload
    private static List<PacketType> listener = new ArrayList<>();
    
    public static void addPacketTypes(PacketType[] packetTypes, ListenerPriority priority) {
        List<PacketType> addToListener = new ArrayList<>();
        for (PacketType packetType : packetTypes) {
            if (!listener.contains(packetType)) {
                listener.add(packetType);
                addToListener.add(packetType);
                
            }
        }
        if (!addToListener.isEmpty()) PacketManager.onPacketEvent(addToListener.toArray(new PacketType[0]), priority, SkriptPacket.getInstance());
    }
    
    public static PacketType[] getListener() {
        return listener.toArray(new PacketType[0]);
    }
    
}
