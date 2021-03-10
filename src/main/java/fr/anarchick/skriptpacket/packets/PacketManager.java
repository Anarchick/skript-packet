package fr.anarchick.skriptpacket.packets;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import fr.anarchick.skriptpacket.Logging;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.util.Converter;
import fr.anarchick.skriptpacket.util.NumberUtils;

public class PacketManager {
    
    private static Map<String, PacketType> packetTypesByName = new HashMap<>();
    private static Map<PacketType, String> packetTypesToName = new HashMap<>();
    public static final PacketType[] PACKETTYPES;
    
    // Init
    static {
        packetTypesByName = createNameToPacketTypeMap();
        for (Map.Entry<String, PacketType> entry : packetTypesByName.entrySet()) {
            packetTypesToName.put(entry.getValue(), entry.getKey());
        }
        PACKETTYPES = packetTypesByName.values().toArray(new PacketType[0]);
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
    
    
    
    
    
    
    public static PacketType getPacketType(String name) {
        String _name = name.toUpperCase();
        return (packetTypesByName.containsKey(_name)) ? packetTypesByName.get(_name) : null;
    }
    
    public static String getPacketName(PacketType packettype) {
        return packetTypesToName.get(packettype);
    }
    
    public static void onPacketEvent(PacketType[] packetTypes, ListenerPriority priority, Plugin plugin) {
        for (PacketType packetType : packetTypes) {
            if (packetType.isServer()) {
                ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new PacketAdapter(plugin, priority, packetType) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority));
                    }
                }).syncStart();
            } else {
                ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new PacketAdapter(plugin, priority, packetType) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority));
                    }
                }).syncStart();
            }
        }
    }

    public static void sendPacket(PacketContainer packet, Player[] players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            Logging.exception(e);
        }
    }
    
    public static void receivePacket(PacketContainer packet, Player[] players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().recieveClientPacket(player, packet);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            Logging.exception(e);
        }
    }

    public static void setField(final PacketContainer packet, final int i, final Object[] delta) {
        StructureModifier<Object> modifier = packet.getModifier();
        Object field = modifier.readSafely(i);
        if (!( (i >= 0 ) && (i < modifier.size()) )) return;
        if (field != null) {
            Class<?> fieldClass = field.getClass();
            
            if (fieldClass.isInstance(delta[0])) {
                packet.getModifier().writeSafely(i, delta[0]);
                return;
            }
            
            if (NumberUtils.isNumber(field) && delta instanceof Number[]) {
                packet.getModifier().writeSafely(i, NumberUtils.convert(fieldClass, (Number[]) delta));
                return;
            } else if (delta.getClass().getName().equals("[Ljava.lang.Object;")) { 
                Number[] converted = NumberUtils.toNumeric(delta);
                if (converted != null) {
                    packet.getModifier().writeSafely(i, NumberUtils.convert(fieldClass, converted));
                    return;
                }   
            }
        }
        packet.getModifier().writeSafely(i, Converter.auto(delta));
    }

}
