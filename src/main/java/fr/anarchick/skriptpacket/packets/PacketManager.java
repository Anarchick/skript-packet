package fr.anarchick.skriptpacket.packets;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import fr.anarchick.skriptpacket.Logging;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.util.Converter;
import fr.anarchick.skriptpacket.util.NumberUtils;

public class PacketManager {
	
	private static Map<String, PacketType> packetTypesByName = new HashMap<>();
	private static Map<PacketType, String> packetTypesToName = new HashMap<>();
	public static final PacketType[] PACKETTYPES;
	
	static {
		packetTypesByName = createNameToPacketTypeMap();
		for (Map.Entry<String, PacketType> entry : packetTypesByName.entrySet()) {
			packetTypesToName.put(entry.getValue(), entry.getKey());
		}
		PACKETTYPES = packetTypesByName.values().toArray(new PacketType[0]);
	}
	
	public static PacketType getPacketType(String name) {
		String _name = name.toUpperCase();
		return (packetTypesByName.containsKey(_name)) ? packetTypesByName.get(_name) : null;
	}
	
	public static String getPacketName(PacketType packettype) {
		return packetTypesToName.get(packettype);
	}
	
	private static Map<String, PacketType> createNameToPacketTypeMap() {
        Map<String, PacketType> packetTypesByName = new HashMap<>();
        addPacketTypes(packetTypesByName, PacketType.Play.Server.getInstance().iterator(), "PLAY", true);
        addPacketTypes(packetTypesByName, PacketType.Play.Client.getInstance().iterator(), "PLAY", false);
        addPacketTypes(packetTypesByName, PacketType.Handshake.Server.getInstance().iterator(), "HANDSHAKE", true);
        addPacketTypes(packetTypesByName, PacketType.Handshake.Client.getInstance().iterator(), "HANDSHAKE", false);
        addPacketTypes(packetTypesByName, PacketType.Login.Server.getInstance().iterator(), "LOGIN", true);
        addPacketTypes(packetTypesByName, PacketType.Login.Client.getInstance().iterator(), "LOGIN", false);
        addPacketTypes(packetTypesByName, PacketType.Status.Server.getInstance().iterator(), "STATUS", true);
        addPacketTypes(packetTypesByName, PacketType.Status.Client.getInstance().iterator(), "STATUS", false);
        return packetTypesByName;
    }
	
    private static void addPacketTypes(Map<String, PacketType> map, Iterator<PacketType> packetTypeIterator, String prefix, Boolean isServer) {
        while (packetTypeIterator.hasNext()) {
            PacketType current = packetTypeIterator.next();
            String fullname = prefix + "_" + (isServer ? "SERVER" : "CLIENT") + "_" + current.name().toUpperCase();
            map.put(fullname, current);
        }
    }
	
    
    
	public static void onPacketEvent(PacketType[] packetTypes, ListenerPriority priority, Consumer<PacketEvent> handler) {
        ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new PacketAdapter(SkriptPacket.getInstance(), priority, packetTypes) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handler.accept(event);
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                handler.accept(event);
            }
        }).syncStart();
    }

    public static void sendPacket(PacketContainer packet, Object exceptLoc, Player[] players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            Logging.exception(exceptLoc, e);
        }
    }
    
    
    
    
    public static void setField(final PacketContainer packet, final int i, final Object[] delta) {
		Object field = packet.getModifier().readSafely(i);
		Class<?> fieldClass = field.getClass();
		boolean isArray = fieldClass.isArray();
		if (isArray) {
			if (NumberUtils.isNumberArray(field) && delta instanceof Number[]) {
				packet.getModifier().writeSafely(i, NumberUtils.convertPrimitive(fieldClass, true, (Number[]) delta));
			} else {
				packet.getModifier().writeSafely(i, Converter.auto(true, delta));
			}
		} else {
			if (NumberUtils.isNumber(field) && delta[0] instanceof Number) {
				packet.getModifier().writeSafely(i, NumberUtils.convertPrimitive(fieldClass, false, new Number[] {(Number) delta[0]}));
			} else {
				packet.getModifier().writeSafely(i, Converter.auto(false, delta));
			}
		}
    }
}
