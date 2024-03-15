package fr.anarchick.skriptpacket.util.converters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

import com.comphenix.protocol.wrappers.*;
import fr.anarchick.skriptpacket.util.converters.Converter;
import fr.anarchick.skriptpacket.util.converters.ConverterToBukkit;
import fr.anarchick.skriptpacket.util.converters.ConverterToNMS;
import fr.anarchick.skriptpacket.util.converters.ConverterToUtility;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.btk5h.skriptmirror.ObjectWrapper;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.slot.Slot;
import fr.anarchick.skriptpacket.SkriptPacket;
import it.unimi.dsi.fastutil.ints.IntList;

public class ConverterLogic {

    protected static final Pattern regexUUID = Pattern.compile("^[\\da-f]{8}-([\\da-f]{4}-){3}[\\da-f]{12}$", Pattern.CASE_INSENSITIVE);
    protected static final Class<?> MojangsonClass = MinecraftReflection.getMinecraftClass("MojangsonParser", "nbt.MojangsonParser");
    protected static final Constructor<?> blockPositionConstructor;


    static {
        try {
            blockPositionConstructor = MinecraftReflection.getBlockPositionClass()
                    .getConstructor(int.class, int.class, int.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find block position constructor.", e);
        }
    }
    
    @SafeVarargs
    public static <T> Object toNMS(T... array) {
        if (array == null || array.length == 0) return array;
        T single = array[0];
        Converter converter = null;
        if (!MinecraftReflection.isMinecraftObject(single)) {
            if (single instanceof Entity
                    || single instanceof ItemStack
                    || single instanceof World) {
                converter = ConverterToNMS.HANDLE;
            } else if (single instanceof Location) {
                converter = ConverterToNMS.RELATED_TO_NMS_BLOCKPOSITION;
            } else if (single instanceof Block) {
                converter = ConverterToNMS.BUKKIT_BLOCK_TO_NMS_IBLOCKDATA;
            } else if (single instanceof BlockData) {
                converter = ConverterToNMS.BUKKIT_BLOCKDATA_TO_NMS_IBLOCKDATA;
            } else if (single instanceof ItemType) {
                converter = ConverterToNMS.SKRIPT_ITEMTYPE_TO_NMS_IBLOCKDATA;
            } else if (single instanceof Slot) {
                converter = ConverterToNMS.SKRIPT_SLOT_TO_NMS_ITEMSTACK;
            } else if (single instanceof WrappedWatchableObject) {
                converter = ConverterToNMS.PROTOCOLLIB_WRAPPED_WATCHABLE_OBJECT_TO_NMS;
            } else if (single instanceof WrappedDataWatcher) {
                converter = ConverterToNMS.PROTOCOLLIB_WRAPPED_DATA_WATCHER_TO_NMS;
            } else if (single instanceof Vector) {
                converter = ConverterToNMS.BUKKIT_VECTOR_TO_NMS_VEC3D;
            } else if (single instanceof Biome) {
                converter = ConverterToNMS.BUKKIT_BIOME_TO_NMS_BIOME_ID;
            } else if (single instanceof String) {
                converter = ConverterToNMS.STRING_TO_NMS_ICHATBASECOMPONENT;
            }
        }
        if (converter != null) return converter.convert(single);
        if (SkriptPacket.isReflectAddon) {
            return (array.length > 1) ? array : ObjectWrapper.unwrapIfNecessary(single);
        }
        return (array.length > 1) ? array : single;
    }
    
    @SafeVarargs
    public static <T> Object toBukkit(T... array) {
        if (array == null || array.length == 0) return array;
        Object single = array[0];
        Converter converter = null;
        if (MinecraftReflection.isMinecraftObject(single)) {
            if (MinecraftReflection.isItemStack(single) || MinecraftReflection.isCraftItemStack(single)) {
                converter = ConverterToBukkit.NMS_ITEMSTACK_TO_BUKKIT_ITEMSTACK;
            } else if (MinecraftReflection.isBlockPosition(single) ) {
                converter = ConverterToBukkit.NMS_BLOCKPOSITION_TO_BUKKIT_LOCATION;
            } else if (MinecraftReflection.isMinecraftObject(single, "IBlockData") ) {
                converter = ConverterToBukkit.NMS_IBLOCKDATA_TO_BUKKIT_MATERIAL;
            } else if (MinecraftReflection.isMinecraftObject(single, "Block") ) {
                converter = ConverterToBukkit.NMS_BLOCK_TO_BUKKIT_MATERIAL;
            } else if (MinecraftReflection.isMinecraftObject(single, "Chunk") ) {
                converter = ConverterToBukkit.NMS_CHUNK_TO_BUKKIT_CHUNK;
            } else if (MinecraftReflection.isMinecraftObject(single, "WorldServer") ) {
                converter = ConverterToBukkit.NMS_WORLD_TO_BUKKIT_WORLD;
            } else if (MinecraftReflection.isMinecraftEntity(single) ) {
                converter = ConverterToBukkit.NMS_ENTITY_TO_BUKKIT_ENTITY;
            } else if (MinecraftReflection.isMinecraftObject(single, "ChatComponentText")) {
                converter = ConverterToUtility.NMS_CHATCOMPONENTTEXT_TO_STRING;
            } else if (MinecraftReflection.isMinecraftObject(single, "Vec3D")) {
                converter = ConverterToBukkit.NMS_VEC3D_TO_BUKKIT_VECTOR;
            }
            // Vector3f // Vec2D
        } else {
            if (single instanceof IntList) {
                converter = ConverterToUtility.INTLIST_TO_INTEGER_ARRAY;
            } else if (single instanceof Vector3F) {
                converter = ConverterToBukkit.PROTOCOLLIB_VECTOR3F_TO_BUKKIT_VECTOR;
            }
        }
        if (converter != null) return converter.convert(single);
        if (SkriptPacket.isReflectAddon) {
            return (array.length > 1) ? array : ObjectWrapper.unwrapIfNecessary(single);
        }
        return (array.length > 1) ? array : single;
    }

    public static Object toObject(Object o) {
        return o;
    }

    public static Character toObject(char c) {
        return c;
    }
    
    public static Byte toObject(byte b) {
        return b;
    }
    
    public static Short toObject(short s) {
        return s;
    }
    
    public static Integer toObject(int i) {
        return i;
    }
    
    public static Long toObject(long l) {
        return l;
    }
    
    public static Float toObject(float f) {
        return f;
    }
    
    public static Double toObject(double d) {
        return d;
    }
    
    public static Boolean toObject(boolean b) {
        return b;
    }
    
    // https://github.com/dmulloy2/ProtocolLib/pull/984
    @Deprecated
    public static Object toNMSNBTTagCompound(Block block) {
        if (block == null) return null;
        NbtCompound nbt = NbtFactory.readBlockState(block);
        return (nbt != null) ? nbt.getHandle() : null;
    }
    
    public static Object getHandle(Object obj) {
        final BukkitUnwrapper unwrapper = new BukkitUnwrapper();
        return unwrapper.unwrapItem(obj);
    }
    
    public static Object callMethod(Object obj, String method) {
        try {
            return obj.getClass().getMethod(method).invoke(obj);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e1) {
            Skript.exception(e1);
            return null;
        }
    }

    /**
     * Updated for mc 1.20.4
     * https://minecraft.fandom.com/wiki/Biome/ID
     * @param biome
     * @return
     */
    public static Number getBiomeID(Biome biome) {
        return switch (biome) {
            case THE_VOID -> 0;
            case PLAINS -> 1;
            case SUNFLOWER_PLAINS -> 2;
            case SNOWY_PLAINS -> 3;
            case ICE_SPIKES -> 4;
            case DESERT -> 5;
            case SWAMP -> 6;
            case MANGROVE_SWAMP -> 7;
            case FOREST -> 8;
            case FLOWER_FOREST -> 9;
            case BIRCH_FOREST -> 10;
            case DARK_FOREST -> 11;
            case OLD_GROWTH_BIRCH_FOREST -> 12;
            case OLD_GROWTH_PINE_TAIGA -> 13;
            case OLD_GROWTH_SPRUCE_TAIGA -> 14;
            case TAIGA -> 15;
            case SNOWY_TAIGA -> 16;
            case SAVANNA -> 17;
            case SAVANNA_PLATEAU -> 18;
            case WINDSWEPT_HILLS -> 19;
            case WINDSWEPT_GRAVELLY_HILLS -> 20;
            case WINDSWEPT_FOREST -> 21;
            case WINDSWEPT_SAVANNA -> 22;
            case JUNGLE -> 23;
            case SPARSE_JUNGLE -> 24;
            case BAMBOO_JUNGLE -> 25;
            case BADLANDS -> 26;
            case ERODED_BADLANDS -> 27;
            case WOODED_BADLANDS -> 28;
            case MEADOW -> 29;
            case CHERRY_GROVE -> 30;
            case GROVE -> 31;
            case SNOWY_SLOPES -> 32;
            case FROZEN_PEAKS -> 33;
            case JAGGED_PEAKS -> 34;
            case STONY_PEAKS -> 35;
            case RIVER -> 36;
            case FROZEN_RIVER -> 37;
            case BEACH -> 38;
            case SNOWY_BEACH -> 39;
            case STONY_SHORE -> 40;
            case WARM_OCEAN -> 41;
            case LUKEWARM_OCEAN -> 42;
            case DEEP_LUKEWARM_OCEAN -> 43;
            case OCEAN -> 44;
            case DEEP_OCEAN -> 45;
            case COLD_OCEAN -> 46;
            case DEEP_COLD_OCEAN -> 47;
            case FROZEN_OCEAN -> 48;
            case DEEP_FROZEN_OCEAN -> 49;
            case MUSHROOM_FIELDS -> 50;
            case DRIPSTONE_CAVES -> 51;
            case LUSH_CAVES -> 52;
            case DEEP_DARK -> 53;
            case NETHER_WASTES -> 54;
            case WARPED_FOREST -> 55;
            case CRIMSON_FOREST -> 56;
            case SOUL_SAND_VALLEY -> 57;
            case BASALT_DELTAS -> 58;
            case THE_END -> 59;
            case END_HIGHLANDS -> 60;
            case END_MIDLANDS -> 61;
            case SMALL_END_ISLANDS -> 62;
            case END_BARRENS -> 63;
            default -> null;
        };
        //Logging.warn("Missing biome id for '" +biome+"'. You should create an issue on Github.");
    }
    
}
