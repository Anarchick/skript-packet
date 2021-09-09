package fr.anarchick.skriptpacket.packets;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;

import ch.njol.skript.Skript;
import fr.anarchick.skriptpacket.Logging;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.util.Converter;
import fr.anarchick.skriptpacket.util.Converter.Auto;
import fr.anarchick.skriptpacket.util.NumberUtils;
import fr.anarchick.skriptpacket.util.Scheduling;
import fr.anarchick.skriptpacket.util.Utils;
import net.md_5.bungee.api.chat.BaseComponent;

public class PacketManager {
    
    public enum Mode {
        SYNC, ASYNC, DEFAULT;
    }
    
    private static Map<String, PacketType> packetTypesByName = new HashMap<>();
    private static Map<PacketType, String> packetTypesToName = new HashMap<>();
    public static final PacketType[] PACKETTYPES;
    public static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();
    
    private  final static Class<?> NBTTagCompoundClass = MinecraftReflection.getMinecraftClass("NBTTagCompound", "nbt.NBTTagCompound");
    private  final static Class<?> IChatBaseComponentClass = MinecraftReflection.getMinecraftClass("IChatBaseComponent", "network.chat.IChatBaseComponent");
    
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
    
    public static void onPacketEvent(PacketType packetType, ListenerPriority priority, Mode mode, Plugin plugin) {
        switch (mode) {
            case ASYNC:
                PROTOCOL_MANAGER.getAsynchronousManager().registerAsyncHandler(new PacketAdapter(plugin, priority, packetType) {
                    private final boolean isServer = packetType.isServer();
                    
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType().equals(packetType) && isServer) SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, true));
                    }
                    
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType().equals(packetType) && !isServer) SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, true));
                    }
                    
                }).start();
                return;
            case SYNC:
                PROTOCOL_MANAGER.addPacketListener(new PacketAdapter(plugin, priority, packetType) {
                    private final boolean isServer = packetType.isServer();
                    
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType().equals(packetType) && isServer) SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, false));
                    }
                    
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType().equals(packetType) && !isServer)
                            Scheduling.sync(() -> SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, false)));
                    }
                    
                });
                return;
            default:
                PROTOCOL_MANAGER.getAsynchronousManager().registerAsyncHandler(new PacketAdapter(plugin, priority, packetType) {
                    private final boolean isServer = packetType.isServer();
                    
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType().equals(packetType) && isServer) SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, false));
                    }
                    
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType().equals(packetType) && !isServer) SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, false));
                    }
                    
                }).syncStart();
                return;
        }
    }
    
    public static boolean removeListeners(Plugin plugin) {
        for (PacketListener listener : PROTOCOL_MANAGER.getPacketListeners()) {
            if (listener.getPlugin().equals(plugin)) {
                PROTOCOL_MANAGER.removePacketListeners(plugin);
                return true;
            }
        }
        return false;
    }

    public static void sendPacket(PacketContainer packet, Player[] players) {
        try {
            for (Player player : players) {
                PROTOCOL_MANAGER.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            Logging.exception(e);
        }
    }
    
    public static void receivePacket(PacketContainer packet, Player[] players) {
        try {
            for (Player player : players) {
                PROTOCOL_MANAGER.recieveClientPacket(player, packet);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            Logging.exception(e);
        }
    }

    public static StructureModifier<Object> setField(final PacketContainer packet, final int i, Object[] delta) {
        StructureModifier<Object> modifier = packet.getModifier();
        if (!( (i >= 0 ) && (i < modifier.size()) )) {
            Skript.error("Available indexes for the packketype '"+PacketManager.getPacketName(packet.getType())+"' are from 0 to "+(modifier.size() -1));
            return null;
        }
        Class<?> fieldClass = modifier.getField(i).getType();
            
        if (fieldClass.isInstance(delta[0])) {
            return modifier.writeSafely(i, delta[0]);
        }

        if (NumberUtils.isNumber(fieldClass)) {
            
            if (delta instanceof Number[]) {
                return modifier.writeSafely(i, NumberUtils.convert(fieldClass, (Number[]) delta));
            }
            if (delta[0] instanceof Entity) {
                final Entity ent = (Entity) delta[0];
                return modifier.writeSafely(i, ent.getEntityId());
            }
        }
        
        if (MinecraftReflection.getBlockPositionClass().equals(fieldClass)) {
            return modifier.writeSafely(i, Auto.LOCATION.convert(delta[0]));
        }
        
        if (fieldClass.equals(UUID.class)) {
            return modifier.writeSafely(i, Auto.TO_UUID.convert(delta[0]));
        }
        
        if (delta instanceof String[]) {
            String str = Optional.ofNullable((String)delta[0]).orElse("");
            if (fieldClass.equals(BaseComponent[].class)) {
                return modifier.writeSafely(i, Auto.STRING_TO_BASECOMPONENT.convert(str));
            } else if (fieldClass.isEnum()) {
                return modifier.writeSafely(i, Utils.getEnum(fieldClass, str, true));
            } else if (fieldClass.equals(NBTTagCompoundClass)) {
                return modifier.writeSafely(i, Auto.STRING_TO_MOJANGSON.convert(str));
            } else if (fieldClass.equals(IChatBaseComponentClass)) {
                return modifier.writeSafely(i, Auto.STRING_TO_ICHATBASECOMPONENT.convert(str));
            }
        }
        
        Object unwrap = Converter.unwrap(delta);
        if (fieldClass.equals(List.class)) {
            return modifier.writeSafely(i, Auto.ARRAYLIST.convert(unwrap));
        }
        return modifier.writeSafely(i, unwrap);
    }

}
