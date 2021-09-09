package fr.anarchick.skriptpacket.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.btk5h.skriptmirror.ObjectWrapper;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.slot.Slot;
import fr.anarchick.skriptpacket.Logging;
import fr.anarchick.skriptpacket.SkriptPacket;

public class Converter {
    
    public enum Auto {
        
        NMS_ENTITY{ // Bukkit Entity
            @Override
            public Object convert(final Object single) {
                return (Entity) MinecraftReflection.getBukkitEntity(single);
            }
        },
        
        NMS_CHUNK{ // -> Bukkit Chunk
            @Override
            public Object convert(final Object single) {
                return (Chunk) callMethod(single, "getBukkitChunk");
            }
        },
        
        NMS_WORLD{ // -> Bukkit World
            @Override
            public Object convert(final Object single) {
                return (World) callMethod(single, "getWorld");
            }
        },
        
        NMS_BLOCKPOSITION{ // -> Bukkit Location
            @Override
            public Object convert(final Object single) {
                final World world = Bukkit.getWorlds().get(0);
                return BlockPosition.getConverter().getSpecific(single).toLocation(world);
            }
        },
        
        NMS_MATERIAL{ // Bukkit Material
            @Override
            public Object convert(final Object single) {
                Material material = null;
                if (MinecraftReflection.isMinecraftObject(single, "IBlockData") ) {
                    material = (Material) callMethod(single, "getBukkitMaterial");
                } else if (MinecraftReflection.isMinecraftObject(single, "Block") ) {
                    material = BukkitConverters.getBlockConverter().getSpecific(single);
                } else if (MinecraftReflection.isItemStack(single) ) {
                    material = MinecraftReflection.getBukkitItemStack(single).getType();
                }
                return material;
            }
        },
        
        NMS_ITEMSTACK{ // -> Bukkit ItemStack
            @Override
            public Object convert(final Object single) {
                return MinecraftReflection.getBukkitItemStack(single);
            }
        },
        
        NMS_BLOCK{ // -> Bukkit ItemStack
            @Override
            public Object convert(final Object single) {
                final Material material = BukkitConverters.getBlockConverter().getSpecific(single);
                return (material.isItem()) ? new ItemStack(material) : null;
            }
        },
        
        NMS_IBLOCKDATA{ // -> Bukkit ItemStack
            @Override
            public Object convert(final Object single) {
                final Material material = (Material) callMethod(single, "getBukkitMaterial");
                return (material.isItem()) ? new ItemStack(material) : null;
            }
        },

        NMS_CHATCOMPONENTTEXT{ // -> String
            @Override
            public Object convert(final Object single) {
                return WrappedChatComponent.fromHandle(single).getJson();
            }
        },
        
        SLOT{ // -> NMS ItemStack
            @Override
            public Object convert(final Object single) {
                return MinecraftReflection.getMinecraftItemStack(((Slot) single).getItem());
            }
        },
        
        ITEMTYPE{ // -> NMS IBlockData
            @Override
            public Object convert(final Object single) {
                Material material = Material.AIR;
                if ( single instanceof ItemStack) {
                    material = ((ItemStack) single).getType();
                } else if (single instanceof ItemType) {
                    material = ((ItemType) single).getMaterial();
                }
                return (material.isBlock()) ? WrappedBlockData.createData(material).getHandle() : null;
            }
        },
        
        BLOCK{ // -> IBlockData
            @Override
            public Object convert(final Object single) {
                return WrappedBlockData.createData(((Block) single).getType()).getHandle();
            }
        },
        
        LOCATION{ // -> BlockPosition
            @Override
            public Object convert(Object single) {
                Vector v = null;
                if (single instanceof Location) {
                    v = ((Location)single).toVector();
                } else if (single instanceof Entity) {
                    v = ((Entity)single).getLocation().toVector();
                } else if (single instanceof Vector) {
                    v = (Vector)single;
                } else if (single instanceof Block) {
                    v = ((Block)single).getLocation().toVector();
                }
                if (v == null) return single;
                try {
                    return blockPositionConstructor.newInstance(v.getBlockX(), v.getBlockY(), v.getBlockZ());
                } catch (Exception ex) {
                    throw new RuntimeException("Cannot construct BlockPosition.", ex);
                }
            }
        },
        
        VECTOR{
            @Override
            public Object convert(final Object single) {
                return LOCATION.convert(single);
            }
        },
        
        ITEMSTACK{ // -> NMS ItemStack
            @Override
            public Object convert(final Object single) {
                return MinecraftReflection.getMinecraftItemStack((ItemStack) single);
            }
        },
        
        HANDLE{
            @Override
            public Object convert(final Object single) {
                return getHandle(single);
            }
        },
        
        WRAPPED_WATCHABLE_OBJECT{
            @Override
            public Object convert(final Object single) {
                return ((WrappedWatchableObject) single).getHandle();
            }
        },
        
        WRAPPED_DATA_WATCHER{
            @Override
            public Object convert(final Object single) {
                WrappedDataWatcher dw = (WrappedDataWatcher) single;
                List<Object> nmsList = new ArrayList<>();
                for (WrappedWatchableObject wwo : dw.getWatchableObjects()) {
                    nmsList.add(wwo.getHandle());
                }
                return nmsList;
            }
        },
        
        BIOME{ // -> Biome ID
            @Override
            public Object convert(final Object single) {
                return getBiomeID((Biome) single);
            }
        },
        
        STRING_TO_BASECOMPONENT{ // -> MD5 BaseComponent[]
            @Override
            public Object convert(@Nonnull final Object single) {
                String json = (String)single;
                WrappedChatComponent wrapper;;
                if (json.startsWith("{") && json.endsWith("}")) {
                    wrapper = WrappedChatComponent.fromJson(json);
                } else {
                    wrapper = WrappedChatComponent.fromText(json);
                }
                return ComponentConverter.fromWrapper(wrapper);
            }
        },
        
        STRING_TO_ICHATBASECOMPONENT{ // -> IChatBaseComponent
            @Override
            public Object convert(@Nonnull final Object single) {
                String text = (String)single;
                if (text.startsWith("{") && text.endsWith("}"))
                        return WrappedChatComponent.fromJson(text).getHandle();
                return WrappedChatComponent.fromText(text).getHandle();
            }
        },
        
        TO_UUID{ // -> UUID
            @Override
            public Object convert(final Object single) {
                if (single instanceof String) {
                    String uuid = (String)single;
                    Matcher matcher = regexUUID.matcher(uuid);
                    if (matcher.find()) return UUID.fromString(uuid);
                } else if (single instanceof Entity) {
                    return ((Entity)single).getUniqueId();
                }
                return single;
            }
        },
        
        STRING_TO_MOJANGSON{ // -> Mojangson
            @Override
            public Object convert(@Nonnull final Object single) {
                String nbt = (String) single;
                Object nms = null;
                try {
                    nms = MojangsonClass.getMethod("parse", String.class).invoke(null, nbt);
                } catch (Exception ex) {
                    Skript.exception(ex);
                }
                return nms;
            }
        },
        
        ARRAYLIST{ // -> ArrayList if it's not
            @Override
            public Object convert(final Object array) {
                return (array instanceof ArrayList) ? array : Arrays.asList(array);
            }
        };
        abstract public Object convert(final Object single);
    }
    
    private final static Constructor<?> blockPositionConstructor;
    private final static Class<?> MojangsonClass = MinecraftReflection.getMinecraftClass("MojangsonParser", "nbt.MojangsonParser");
    private final static Pattern regexUUID = Pattern.compile("^[\\da-f]{8}-([\\da-f]{4}-){3}[\\da-f]{12}$", Pattern.CASE_INSENSITIVE);
    
    static {
        try {
            blockPositionConstructor = MinecraftReflection.getBlockPositionClass()
                .getConstructor(int.class, int.class, int.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find block position constructor.", e);
        }
    }
    
    @SafeVarargs
    public static <T> Object unwrap(T... array) {
        if (array == null || array.length == 0) return array;
        T single = array[0];
        Auto converter = null;
        if (!MinecraftReflection.isMinecraftObject(single)) {
            if (single instanceof Location) {
                converter = Auto.LOCATION;
            } else if (single instanceof Block) {
                converter = Auto.BLOCK;
            } else if (single instanceof ItemStack) {
                converter = Auto.ITEMSTACK;
            } else if (single instanceof Player ||
                    single instanceof Entity ||
                    single instanceof World ||
                    single instanceof Chunk) {
                converter = Auto.HANDLE;
            } else if (single instanceof Slot) {
                converter = Auto.SLOT;
            } else if (single instanceof ItemType) {
                converter = Auto.ITEMTYPE;
            } else if (single instanceof WrappedWatchableObject) {
                converter = Auto.WRAPPED_WATCHABLE_OBJECT;
            } else if (single instanceof WrappedDataWatcher) {
                converter = Auto.WRAPPED_DATA_WATCHER;
            } else if (single instanceof Biome) {
              converter = Auto.BIOME;  
            } else if (single instanceof Vector) {
                converter = Auto.VECTOR;
            }
        }
        if (converter != null) return converter.convert(single);
        if (SkriptPacket.isReflectAddon) {
            return (array.length > 1) ? array : ObjectWrapper.unwrapIfNecessary(single);
        }
        return (array.length > 1) ? array : single;
    }
    
    @SafeVarargs
    public static <T> Object wrap(T... array) {
        if (array == null || array.length == 0) return array;
        Object single = array[0];
        Auto converter = null;
        if (MinecraftReflection.isMinecraftObject(single)) {
            if (MinecraftReflection.isItemStack(single) || MinecraftReflection.isCraftItemStack(single)) {
                converter = Auto.NMS_ITEMSTACK;
            } else if (MinecraftReflection.isBlockPosition(single) ) {
                converter = Auto.NMS_BLOCKPOSITION;
            } else if (MinecraftReflection.isMinecraftObject(single, "IBlockData") ) {
                converter = Auto.NMS_IBLOCKDATA;
            } else if (MinecraftReflection.isMinecraftObject(single, "Block") ) {
                converter = Auto.NMS_BLOCK;
            } else if (MinecraftReflection.isMinecraftObject(single, "Chunk") ) {
                converter = Auto.NMS_CHUNK;
            } else if (MinecraftReflection.isMinecraftObject(single, "WorldServer") ) {
                converter = Auto.NMS_WORLD;
            } else if (MinecraftReflection.isMinecraftEntity(single) ) {
                converter = Auto.NMS_ENTITY;
            } else if (MinecraftReflection.isMinecraftObject(single, "ChatComponentText")) {
                converter = Auto.NMS_CHATCOMPONENTTEXT;
            }
        }
        if (converter != null) return converter.convert(single);
        if (SkriptPacket.isReflectAddon) {
            return (array.length > 1) ? array : ObjectWrapper.unwrapIfNecessary(single);
        }
        return (array.length > 1) ? array : single;
    }
    
    public static Object toObject(Object o) {
        return (o == null) ? null : o;
    }
    
    public static Character toObject(char c) {
        return Character.valueOf(c);
    }
    
    public static Byte toObject(byte b) {
        return Byte.valueOf(b);
    }
    
    public static Short toObject(short s) {
        return Short.valueOf(s);
    }
    
    public static Integer toObject(int i) {
        return Integer.valueOf(i);
    }
    
    public static Long toObject(long l) {
        return Long.valueOf(l);
    }
    
    public static Float toObject(float f) {
        return Float.valueOf(f);
    }
    
    public static Double toObject(double d) {
        return Double.valueOf(d);
    }
    
    public static Boolean toObject(boolean b) {
        return Boolean.valueOf(b);
    }
    
    // https://github.com/dmulloy2/ProtocolLib/pull/984
    @Deprecated
    public static Object toNMSNBTTagCompound(Block block) {
        if (block == null) return null;
        NbtCompound nbt = NbtFactory.readBlockState(block);
        return (nbt != null) ? nbt.getHandle() : null;
    }
    
    public static Object getHandle(Object obj) {
        BukkitUnwrapper unwrapper = new BukkitUnwrapper();
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
    
    private static Number getBiomeID(Biome biome) {
        switch(biome) {
            case OCEAN:
                return 0;
            case PLAINS:
                return 1;
            case DESERT:
                return 2;
            case MOUNTAINS:
                return 3;
            case FOREST:
                return 4;
            case TAIGA:
                return 5;
            case SWAMP:
                return 6;
            case RIVER:
                return 7;
            case NETHER_WASTES:
                return 8;
            case THE_END:
                return 9;
            case FROZEN_OCEAN:
                return 10;
            case FROZEN_RIVER:
                return 11;
            case SNOWY_TUNDRA:
                return 12;
            case SNOWY_MOUNTAINS:
                return 13;
            case MUSHROOM_FIELDS:
                return 14;
            case MUSHROOM_FIELD_SHORE:
                return 15;
            case BEACH:
                return 16;
            case DESERT_HILLS:
                return 17;
            case WOODED_HILLS:
                return 18;
            case TAIGA_HILLS:
                return 19;
            case MOUNTAIN_EDGE:
                return 20;
            case JUNGLE:
                return 21;
            case JUNGLE_HILLS:
                return 22;
            case JUNGLE_EDGE:
                return 23;
            case DEEP_OCEAN:
                return 24;
            case STONE_SHORE:
                return 25;
            case SNOWY_BEACH:
                return 26;
            case BIRCH_FOREST:
                return 27;
            case BIRCH_FOREST_HILLS:
                return 28;
            case DARK_FOREST:
                return 29;
            case SNOWY_TAIGA:
                return 30;
            case SNOWY_TAIGA_HILLS:
                return 31;
            case GIANT_TREE_TAIGA:
                return 32;
            case GIANT_TREE_TAIGA_HILLS:
                return 33;
            case WOODED_MOUNTAINS:
                return 34;
            case SAVANNA:
                return 35;
            case SAVANNA_PLATEAU:
                return 36;
            case BADLANDS:
                return 37;
            case WOODED_BADLANDS_PLATEAU:
                return 38;
            case BADLANDS_PLATEAU:
                return 39;
            case SMALL_END_ISLANDS:
                return 40;
            case END_MIDLANDS:
                return 41;
            case END_HIGHLANDS:
                return 42;
            case END_BARRENS:
                return 43;
            case WARM_OCEAN:
                return 44;
            case LUKEWARM_OCEAN:
                return 45;
            case COLD_OCEAN:
                return 46;
            case DEEP_WARM_OCEAN:
                return 47;
            case DEEP_LUKEWARM_OCEAN:
                return 48;
            case DEEP_COLD_OCEAN:
                return 49;
            case DEEP_FROZEN_OCEAN:
                return 50;
            case THE_VOID:
                return 127;
            case SUNFLOWER_PLAINS:
                return 129;
            case DESERT_LAKES:
                return 130;
            case GRAVELLY_MOUNTAINS:
                return 131;
            case FLOWER_FOREST:
                return 132;
            case TAIGA_MOUNTAINS:
                return 133;
            case SWAMP_HILLS:
                return 134;
            case ICE_SPIKES:
                return 140;
            case MODIFIED_JUNGLE:
                return 149;
            case MODIFIED_JUNGLE_EDGE:
                return 151;
            case TALL_BIRCH_FOREST:
                return 155;
            case TALL_BIRCH_HILLS:
                return 156;
            case DARK_FOREST_HILLS:
                return 157;
            case SNOWY_TAIGA_MOUNTAINS:
                return 158;
            case GIANT_SPRUCE_TAIGA:
                return 160;
            case GIANT_SPRUCE_TAIGA_HILLS:
                return 161;
            case MODIFIED_GRAVELLY_MOUNTAINS:
                return 162;
            case SHATTERED_SAVANNA:
                return 163;
            case SHATTERED_SAVANNA_PLATEAU:
                return 164;
            case ERODED_BADLANDS:
                return 165;
            case MODIFIED_WOODED_BADLANDS_PLATEAU:
                return 166;
            case MODIFIED_BADLANDS_PLATEAU:
                return 167;
            case BAMBOO_JUNGLE:
                return 168;
            case BAMBOO_JUNGLE_HILLS:
                return 169;
            case SOUL_SAND_VALLEY:
                return 170;
            case CRIMSON_FOREST:
                return 171;
            case WARPED_FOREST:
                return 172;
            case BASALT_DELTAS:
                return 173;
            case DRIPSTONE_CAVES:
                return 174;
            case LUSH_CAVES:
                return 175;
            default:
                Logging.warn("Missing biome id for '" +biome+"'. You should create an issue on Github.");
                return null;
        }
    }
    
}
