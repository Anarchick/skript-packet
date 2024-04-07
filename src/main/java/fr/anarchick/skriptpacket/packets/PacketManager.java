package fr.anarchick.skriptpacket.packets;

import ch.njol.skript.Skript;
import ch.njol.util.StringUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.elements.expressions.datawatcher.DataWatcher;
import fr.anarchick.skriptpacket.util.NumberEnums;
import fr.anarchick.skriptpacket.util.Utils;
import fr.anarchick.skriptpacket.util.converters.*;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.*;

public class PacketManager extends StructureModifier<Object> {
    
    public enum Mode {
        SYNC, ASYNC, DEFAULT
    }

    private static final List<String> allPacketTypesNames = new ArrayList<>(); // Contains unsuported packetTypes
    private static final Map<String, PacketType> packetTypesByName;
    private static final Map<PacketType, String> packetTypesToName = new HashMap<>();
    public static final PacketType[] PACKET_TYPES;
    public static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();
    public static final Map<Class<?>, Converter> FIELD_CONVERTERS = new HashMap<>();



    // Init
    static {
        packetTypesByName = createNameToPacketTypeMap();

        for (Map.Entry<String, PacketType> entry : packetTypesByName.entrySet()) {
            packetTypesToName.put(entry.getValue(), entry.getKey());
        }

        PACKET_TYPES = packetTypesByName.values().toArray(new PacketType[0]);

        FIELD_CONVERTERS.put(UUID.class, ConverterToUtility.RELATED_TO_UUID);
        FIELD_CONVERTERS.put(Optional.class, ConverterToUtility.OBJECT_TO_OPTIONAL);
        FIELD_CONVERTERS.put(List.class, ConverterToUtility.OBJECT_TO_LIST);
        FIELD_CONVERTERS.put(Set.class, ConverterToUtility.OBJECT_TO_SET);
        FIELD_CONVERTERS.put(ConverterLogic.BlockPositionClass, ConverterToNMS.RELATED_TO_NMS_BLOCKPOSITION);
        FIELD_CONVERTERS.put(ConverterLogic.ItemStackClass, ConverterToNMS.RELATED_TO_NMS_ITEMSTACK);
        FIELD_CONVERTERS.put(ConverterLogic.EntityTypesClass, ConverterToNMS.RELATED_TO_NMS_ENTITYTYPES);
        FIELD_CONVERTERS.put(ConverterLogic.MinecraftKeyClass, ConverterToNMS.RELATED_TO_NMS_MINECRAFTKEY);
        FIELD_CONVERTERS.put(ConverterLogic.NBTTagCompoundClass, ConverterToUtility.STRING_TO_MOJANGSON);
        FIELD_CONVERTERS.put(ConverterLogic.IChatBaseComponentClass, ConverterToNMS.STRING_TO_NMS_ICHATBASECOMPONENT);
        FIELD_CONVERTERS.put(BaseComponent[].class, ConverterToUtility.STRING_TO_MD5_BASECOMPONENT);
        FIELD_CONVERTERS.put(Component.class, ConverterToBukkit.STRING_TO_PAPER_COMPONENT);
    }

    private static Map<String, PacketType> createNameToPacketTypeMap() {
        final Map<String, PacketType> packetTypesByName = new HashMap<>();
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
            final PacketType current = packetTypeIterator.next();
            final String fullName = prefix + "_" + (isServer ? "SERVER" : "CLIENT") + "_" + current.name().toUpperCase();
            allPacketTypesNames.add(fullName);

            if (current.isSupported()) {
                map.put(fullName, current);
            }

        }

    }
    
    
    
    
    private static final JavaPlugin PLUGIN = SkriptPacket.getInstance();

    public static PacketType getPacketType(@Nonnull String name) {
        return packetTypesByName.getOrDefault(name.toUpperCase(), null);
    }
    
    public static String getPacketName(PacketType packetType) {
        return packetTypesToName.get(packetType);
    }

    /**
     * Used for the doc generation
     * Contains unsupported packetTypes.
     * @return a single string of name of packetTypes separated by commas
     */
    public static String getAllPacketTypeNames() {
        final List<String> names = allPacketTypesNames.stream().sorted().map(String::toLowerCase).toList();
        return StringUtils.join(names, ", ");
    }
    
    public static void onPacketEvent(PacketType packetType, ListenerPriority priority, Mode mode) {
        final SPPacketAdapter SPPacketAdapter = new SPPacketAdapter(priority, packetType, mode);

        switch (mode) {
            case ASYNC -> PROTOCOL_MANAGER.getAsynchronousManager().registerAsyncHandler(SPPacketAdapter).start();
            case SYNC -> PROTOCOL_MANAGER.addPacketListener(SPPacketAdapter);
            default -> PROTOCOL_MANAGER.getAsynchronousManager().registerAsyncHandler(SPPacketAdapter).syncStart();
        }

    }
    
    public static void removeListeners() {
        PROTOCOL_MANAGER.removePacketListeners(PLUGIN);
        // Thrown a warning telling to create new listeners instead of removed
        // PROTOCOL_MANAGER.getAsynchronousManager().unregisterAsyncHandlers(PLUGIN);
    }

    public static void removeAsyncListeners() {
        PROTOCOL_MANAGER.getAsynchronousManager().unregisterAsyncHandlers(PLUGIN);
    }

    public static void sendPacket(PacketContainer packet, Player[] players) {
        try {

            for (Player player : players) {
                PROTOCOL_MANAGER.sendServerPacket(player, packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void receivePacket(PacketContainer packet, Player[] players) {
        try {

            for (Player player : players) {
                PROTOCOL_MANAGER.receiveClientPacket(player, packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static StructureModifier<?> setField(final PacketContainer packet, final int i, Object[] data) {
        final StructureModifier<Object> modifier = packet.getModifier();

        if (!( (i >= 0 ) && (i < modifier.size()) )) {
            Skript.error("Available indexes for the packketype '"+PacketManager.getPacketName(packet.getType())+"' are from 0 to "+(modifier.size() -1));
            return null;
        }

        final Class<?> fieldClass = modifier.getField(i).getType();

        if (fieldClass.isInstance(data[0])) {
            return modifier.writeSafely(i, data[0]);
        }

        if (NumberEnums.isNumber(fieldClass)) {

            if (data instanceof Number[] numbers) {
                return modifier.writeSafely(i, NumberEnums.convert(fieldClass, numbers));
            } else if (data instanceof Entity[] entities) {
                final Number[] ids = Utils.EntitiesIDs(entities);
                return modifier.writeSafely(i, NumberEnums.convert(fieldClass, ids));
            } else if (data instanceof Biome[] biome) {
                return modifier.writeSafely(i, NumberEnums.convert(fieldClass, (Number) ConverterToNMS.BUKKIT_BIOME_TO_NMS_BIOME_ID.convert(biome)));
            }

        }

        // Must be called before auto converter
        if (data[0] instanceof DataWatcher dataWatcher) {
            return packet.getDataValueCollectionModifier().writeSafely(0, dataWatcher.toList());
        }

        if (FIELD_CONVERTERS.containsKey(fieldClass)) {
            final Converter converter = FIELD_CONVERTERS.get(fieldClass);

            if (converter.isArrayInput()) {
                return modifier.writeSafely(i, converter.convert(data));
            } else {
                return modifier.writeSafely(i, converter.convert(data[0]));
            }

        }

        if (data instanceof String[] strings) {
            if (fieldClass.isEnum()) {
                return modifier.writeSafely(i, Utils.getEnum(fieldClass, strings[0]));
            }
        }

        final Object unwrap = ConverterLogic.toNMS(data);

        return modifier.writeSafely(i, unwrap);
    }

}
