package fr.anarchick.skriptpacket.packets;


import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;

import fr.anarchick.skriptpacket.SkriptPacket;

public class SkriptPacketEventListener {
	
	// Removed support of priority
	/*
	private Set<PacketType> packetTypesListenedFor = new HashSet<PacketType>();
	private static final Map<ListenerPriority, SkriptPacketEventListener> listeners = new HashMap<>();
	private final ListenerPriority priority;
    

    private SkriptPacketEventListener(ListenerPriority priority) {
        this.priority = priority;
    }
    
    public static void addPacketTypes(PacketType[] packetTypes, ListenerPriority priority) {
        SkriptPacketEventListener listener = listeners.computeIfAbsent(priority, SkriptPacketEventListener::new);
        listener.addPacketTypes(packetTypes);
    }

    private void addPacketTypes(PacketType[] packetTypes) {
        List<PacketType> packetTypesToStartListeningFor = new ArrayList<>();
        for (int i = 0; i < packetTypes.length; i++) {
            if (!packetTypesListenedFor.contains(packetTypes[i])) {
                packetTypesListenedFor.add(packetTypes[i]);
                packetTypesToStartListeningFor.add(packetTypes[i]);
            }
        }
        if (!packetTypesToStartListeningFor.isEmpty()) {
            PacketManager.onPacketEvent(packetTypesToStartListeningFor.toArray(new PacketType[0]), priority, packetEvent -> {
                SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(packetEvent, priority));
            });
        }
    }
    */
	
	// Cumulative, need a way to reset on a script reloadS
	private static List<PacketType> listener = new ArrayList<>();
	
	public static void addPacketTypes(PacketType[] packetTypes, ListenerPriority priority) {
		List<PacketType> addToListener = new ArrayList<>();
		for (PacketType packetType : packetTypes) {
			if (!listener.contains(packetType)) {
				listener.add(packetType);
				addToListener.add(packetType);
				
			}
		}
        if (!addToListener.isEmpty()) PacketManager.onPacketEvent(addToListener.toArray(new PacketType[0]), priority, packetEvent -> {
            SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(packetEvent, priority));
        });
    }
	
	public static PacketType[] getListener() {
		return listener.toArray(new PacketType[0]);
	}
}
