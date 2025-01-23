package fr.anarchick.skriptpacket.util.converters;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.util.slot.Slot;
import com.btk5h.skriptmirror.ObjectWrapper;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import fr.anarchick.skriptpacket.Logging;
import fr.anarchick.skriptpacket.SkriptPacket;
import io.papermc.paper.registry.PaperRegistryAccess;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public class ConverterLogic {

    // remove from SP 2.2.2
    //public static final Class<?> MojangsonClass = MinecraftReflection.getMinecraftClass("MojangsonParser", "nbt.MojangsonParser");
    public static final Class<?> NBTTagCompoundClass = MinecraftReflection.getNBTCompoundClass();
    public static final Class<?> IChatBaseComponentClass = MinecraftReflection.getIChatBaseComponentClass();
    public static final Class<?> ItemStackClass = MinecraftReflection.getItemStackClass();
    public static final Class<?> BlockPositionClass = MinecraftReflection.getBlockPositionClass();
    public static final Class<?> MinecraftKeyClass = MinecraftReflection.getMinecraftKeyClass();
    public static final Class<?> EntityTypesClass = MinecraftReflection.getEntityTypes();
    public static final Class<?> IBlockDataClass = MinecraftReflection.getIBlockDataClass();
    public static final Class<?> BlockClass = MinecraftReflection.getBlockClass();
    public static final Class<?> Vec3DClass = MinecraftReflection.getVec3DClass();
    public static final Class<?> WorldServerClass = MinecraftReflection.getWorldServerClass();
    public static final Class<?> EntityClass = MinecraftReflection.getEntityClass();

    protected static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final List<Converter> CONVERTERS = new LinkedList<>();
    private static final Map<Class<?>, Converter> TO_BUKKIT = new HashMap<>();
    private static final ClassLoader classLoader = SkriptPacket.getInstance().getClass().getClassLoader();
    public static final Constructor<?> blockPositionConstructor;
    public static final Pattern regexUUID = Pattern.compile("^[\\da-f]{8}-([\\da-f]{4}-){3}[\\da-f]{12}$", Pattern.CASE_INSENSITIVE);

    static {

        try {
            blockPositionConstructor = MinecraftReflection.getBlockPositionClass()
                    .getConstructor(int.class, int.class, int.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find block position constructor.", e);
        }

        CONVERTERS.addAll(Arrays.stream(ConverterToUtility.values()).toList());
        CONVERTERS.addAll(Arrays.stream(ConverterToBukkit.values()).toList());
        CONVERTERS.addAll(Arrays.stream(ConverterToNMS.values()).toList());

        // If an Exception happens, just replace by Strings
        registerToBukkitConverter(ConverterToUtility.SKRIPTMIRROR_UNWRAPPER, "com.btk5h.skriptmirror.ObjectWrapper");
        registerToBukkitConverter(ConverterToBukkit.NMS_ITEMSTACK_TO_BUKKIT_ITEMSTACK, ItemStackClass);
        registerToBukkitConverter(ConverterToBukkit.NMS_BLOCKPOSITION_TO_BUKKIT_LOCATION, BlockPositionClass);
        registerToBukkitConverter(ConverterToBukkit.NMS_IBLOCKDATA_TO_BUKKIT_BLOCKDATA,IBlockDataClass);
        // registerToBukkitConverter("MovingObjectPositionBlock", ConverterToBukkit.TODO);
        registerToBukkitConverter(ConverterToBukkit.NMS_BLOCK_TO_BUKKIT_MATERIAL, BlockClass);
        registerToBukkitConverter(ConverterToBukkit.NMS_CHUNK_TO_BUKKIT_CHUNK, "Chunk");
        registerToBukkitConverter(ConverterToBukkit.NMS_WORLD_TO_BUKKIT_WORLD, WorldServerClass);
        registerToBukkitConverter(ConverterToBukkit.NMS_ENTITY_TO_BUKKIT_ENTITY, EntityClass);
        registerToBukkitConverter(ConverterToUtility.NMS_CHATCOMPONENTTEXT_TO_STRING, "network.chat.ChatComponentText", "network.chat.TextComponent", "ChatComponentText");
        registerToBukkitConverter(ConverterToBukkit.NMS_VEC3D_TO_BUKKIT_VECTOR, Vec3DClass);
        registerToBukkitConverter(ConverterToUtility.INTLIST_TO_INTEGER_ARRAY, IntList.class);
        registerToBukkitConverter(ConverterToBukkit.PROTOCOLLIB_VECTOR3F_TO_BUKKIT_VECTOR, Vector3F.class);
        registerToBukkitConverter(ConverterToUtility.NMS_MINECRAFTKEY_TO_STRING, MinecraftKeyClass);
    }

    public static void registerToBukkitConverter(@Nonnull Converter converter, String... classNames) {
        Class<?> clazz = null;

        for (String className : classNames) {

            try {
                clazz = MinecraftReflection.getMinecraftClass(className);
                break;
            } catch (Exception ignored) {

                try {
                    clazz = Class.forName(className, false, classLoader);
                    break;
                } catch (ClassNotFoundException ignored2) {}

            }
        }

        registerToBukkitConverter(converter, clazz);
    }

    public static void registerToBukkitConverter(@Nonnull Converter converter, Class<?> clazz) {
        if (clazz != null) {
            TO_BUKKIT.put(clazz, converter);
        }
    }

    @Nonnull
    public static List<Converter> getConverters(@Nonnull Class<?> toClass) {
        final List<Converter> list = new ArrayList<>();

        for (Converter converter : CONVERTERS) {

            if ( converter.getOutputType().isAssignableFrom(toClass)) {
                list.add(converter);
            }

        }

        return list;
    }

    @Nonnull
    public static Converter getConverter(@Nonnull Class<?> fromClass, @Nonnull Class<?> toClass) {
        for (Converter converter : CONVERTERS) {
            final Class<?> outputClass = converter.getOutputType();
            final Class<?> inputClass = converter.getInputType();

            if (outputClass.isAssignableFrom(toClass)
                    && inputClass.isAssignableFrom(fromClass)
                    && inputClass != Object.class
                    && outputClass != Object.class) {
                return converter;
            }

        }
        return ConverterToUtility.HIMSELF;
    }

    
    @SafeVarargs
    public static <T> Object toNMS(T... array) {

        if (array == null || array.length == 0) {
            return array;
        }

        T single = array[0];
        Converter converter = null;

        if (!MinecraftReflection.isMinecraftObject(single)) {

            if (single instanceof Entity
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
            } else if (single instanceof ItemStack) {
                converter = ConverterToNMS.BUKKIT_ITEMSTACK_TO_NMS_ITEMSTACK;
            } else if (single instanceof Material) {
                converter = ConverterToNMS.BUKKIT_MATERIAL_TO_NMS_ITEMSTACK;
            } else if (single instanceof Slot) {
                converter = ConverterToNMS.SKRIPT_SLOT_TO_NMS_ITEMSTACK;
            } else if (single instanceof Vector) {
                converter = ConverterToNMS.BUKKIT_VECTOR_TO_NMS_VEC3D;
            } else if (single instanceof Biome) {
                converter = ConverterToNMS.BUKKIT_BIOME_TO_NMS_BIOME_ID;
            } else if (single instanceof String text) {
                if (text.startsWith("{") && text.endsWith("}")) {
                    converter = ConverterToNMS.STRING_TO_NMS_NBT_COMPOUND_TAG;
                } else {
                    converter = ConverterToNMS.STRING_TO_NMS_ICHATBASECOMPONENT;
                }
            } else if (single instanceof EntityType || single instanceof org.bukkit.entity.EntityType) {
                converter = ConverterToNMS.RELATED_TO_NMS_ENTITYTYPES;
            }

        }
        if (converter != null) {
            return converter.convert(single);
        }

        if (SkriptPacket.isReflectAddon) {
            return (array.length > 1) ? array : ObjectWrapper.unwrapIfNecessary(single);
        }

        return (array.length > 1) ? array : single;
    }

    @Nonnull
    public static Converter getConverterToBukkit(Class<?> nmsClass) {
        Converter converter = TO_BUKKIT.get(nmsClass);

        if (converter == null) {

            for (Class<?> aClass : TO_BUKKIT.keySet()) {

                if (aClass.isAssignableFrom(nmsClass)) {
                    converter = TO_BUKKIT.get(aClass);
                    break;
                }

            }

        }

        return (converter == null) ? ConverterToUtility.HIMSELF : converter;
    }

    @SafeVarargs
    public static <T> Object toBukkit(T... array) {

        if (array == null || array.length == 0 || array[0] == null) {
            return array;
        }

        @Nonnull final Object single = array[0];
        final Class<?> nmsClass = single.getClass();
        final Converter converter = getConverterToBukkit(nmsClass);

        if (converter.isArrayInput()) {
            return converter.convert(array);
        } else {
            return converter.convert(single);
        }

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

        if (block == null) {
            return null;
        }

        final NbtCompound nbt = NbtFactory.readBlockState(block);
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

    private static final Map<NamespacedKey, Integer> BIOME_ID_MAP = new HashMap<>();

    public static void loadBiomeID() {
        Logging.info("Loading biome ids from config");
        BIOME_ID_MAP.clear();
        SkriptPacket sp = SkriptPacket.getInstance();
        sp.saveDefaultConfig();
        FileConfiguration config = sp.getConfig();

        for (String ymlKey : config.getConfigurationSection("biome-ids").getKeys(false)) {
            String key = ymlKey.replace('-', ':');
            NamespacedKey namespacedKey = NamespacedKey.fromString(key);

            if (namespacedKey == null) {
                Logging.warn("Invalid key '" + ymlKey + "' in biome-ids section.");
                continue;
            }

            int id = config.getInt("biome-ids."+ymlKey);
            BIOME_ID_MAP.put(namespacedKey, id);
        }

        RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).forEach((key) -> {
            NamespacedKey namespacedKey = key.getKey();

            if (!BIOME_ID_MAP.containsKey(namespacedKey)) {
                Logging.warn("Missing biome id for '" + namespacedKey + "'. You should create an entry in config file. (If it's a vanilla biome you should refer to https://minecraft.fandom.com/wiki/Biome/ID)");
            }

        });

    }
    /**
     * https://minecraft.fandom.com/wiki/Biome/ID
     * @param biome Bukkit biome
     * @return ID of the NMS biome
     */
    public static Number getBiomeID(Biome biome) {
        NamespacedKey key = biome.getKey();
        int id = BIOME_ID_MAP.getOrDefault(key, Integer.MAX_VALUE);

        if (id == Integer.MAX_VALUE) {
            Logging.warn("Missing biome id for '"+key+"'. You should create an entry in config file. (If it's a vanilla biome you should refer to https://minecraft.fandom.com/wiki/Biome/ID)");
            return 0;
        }

        return id;
    }
    
}
