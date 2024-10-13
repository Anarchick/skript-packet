package fr.anarchick.skriptpacket.util.converters;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.util.slot.Slot;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import fr.anarchick.skriptpacket.Logging;
import fr.anarchick.skriptpacket.SkriptPacket;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public enum ConverterToNMS implements Converter {

    /**
     * Return NMS from Bukkit :
     * Entity
     * World
     */
    HANDLE {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.getHandle(single);
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Object.class;
        }
    },

    SKRIPT_SLOT_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getMinecraftItemStack(((Slot) single).getItem());
        }

        @Override
        public Class<?> getInputType() {
            return Slot.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    BUKKIT_MATERIAL_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getMinecraftItemStack(new ItemStack((Material) single));
        }

        @Override
        public Class<?> getInputType() {
            return Material.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    BUKKIT_ITEMSTACK_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getMinecraftItemStack((ItemStack) single);
        }

        @Override
        public Class<?> getInputType() {
            return ItemStack.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    BUKKIT_BLOCK_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return BUKKIT_MATERIAL_TO_NMS_ITEMSTACK.convert(((Block) single).getType());
        }

        @Override
        public Class<?> getInputType() {
            return Block.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    BUKKIT_BLOCKDATA_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return BUKKIT_MATERIAL_TO_NMS_ITEMSTACK.convert(((BlockData) single).getMaterial());
        }

        @Override
        public Class<?> getInputType() {
            return BlockData.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    RELATED_TO_NMS_ITEMSTACK {
        @Nonnull
        @Override
        public Object convert(final Object single) {

            if (single instanceof ItemStack) {
                return BUKKIT_ITEMSTACK_TO_NMS_ITEMSTACK.convert(single);
            } else if (single instanceof ItemType itemType) {
                return BUKKIT_MATERIAL_TO_NMS_ITEMSTACK.convert(itemType.getMaterial());
            } else if (single instanceof Slot) {
                return SKRIPT_SLOT_TO_NMS_ITEMSTACK.convert(single);
            } else if (single instanceof Material) {
                return BUKKIT_MATERIAL_TO_NMS_ITEMSTACK.convert(single);
            } else if (single instanceof Block) {
                return BUKKIT_BLOCK_TO_NMS_ITEMSTACK.convert(single);
            } else if (single instanceof BlockData) {
                return BUKKIT_BLOCKDATA_TO_NMS_ITEMSTACK.convert(single);
            }

            return BUKKIT_ITEMSTACK_TO_NMS_ITEMSTACK.convert(new ItemStack(Material.AIR));
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    RELATED_TO_NMS_BLOCKPOSITION {
        @Override
        public Object convert(Object single) {
            Vector v = null;

            if (single instanceof Location loc) {
                v = loc.toVector();
            } else if (single instanceof Entity entity) {
                v = entity.getLocation().toVector();
            } else if (single instanceof Vector vector) {
                v = vector;
            } else if (single instanceof Block block) {
                v = block.getLocation().toVector();
            }

            if (v == null) {
                return single;
            }

            try {
                return ConverterLogic.blockPositionConstructor.newInstance(v.getBlockX(), v.getBlockY(), v.getBlockZ());
            } catch (Exception ex) {
                throw new RuntimeException("Cannot construct BlockPosition.", ex);
            }
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.BlockPositionClass;
        }
    },

    /**
     * if the material is a block return NMS IBLOCKDATA of the material
     * otherwise return NMS IBLOCKDATA of air
     */
    SKRIPT_ITEMTYPE_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            Material material = ((ItemType) single).getMaterial();
            if (!material.isBlock()) material = Material.AIR;
            return BUKKIT_MATERIAL_TO_NMS_IBLOCKDATA.convert(material);
        }

        @Override
        public Class<?> getInputType() {
            return ItemType.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.IBlockDataClass;
        }
    },

    BUKKIT_MATERIAL_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData(((Material) single)).getHandle();
        }

        @Override
        public Class<?> getInputType() {
            return Material.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.IBlockDataClass;
        }
    },

    BUKKIT_BLOCK_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData((((Block) single)).getBlockData()).getHandle();
        }

        @Override
        public Class<?> getInputType() {
            return Block.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.IBlockDataClass;
        }
    },

    BUKKIT_BLOCKDATA_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData(single).getHandle();
        }

        @Override
        public Class<?> getInputType() {
            return BlockData.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.IBlockDataClass;
        }
    },

    BUKKIT_VECTOR_TO_NMS_VEC3D {
        @Override
        public Object convert(final Object single) {
            return BukkitConverters.getVectorConverter().getGeneric((Vector) single);
        }

        @Override
        public Class<?> getInputType() {
            return Vector.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.Vec3DClass;
        }
    },

    BUKKIT_BIOME_TO_NMS_BIOME_ID {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.getBiomeID((Biome) single);
        }

        @Override
        public Class<?> getInputType() {
            return Biome.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Integer.class;
        }
    },

    STRING_TO_NMS_ICHATBASECOMPONENT {
        @Override
        public Object convert(@Nonnull final Object single) {
            final String text = Optional.of((String) single).orElse("");
            return WrappedChatComponent.fromText(text).getHandle();
        }

        @Override
        public Class<?> getInputType() {
            return String.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.IChatBaseComponentClass;
        }
    },

    RELATED_TO_NMS_MINECRAFTKEY {
        @Override
        public Object convert(@Nonnull final Object single) {
            NamespacedKey namespacedKey = new NamespacedKey("minecraft", "air");

            if (single instanceof Keyed keyed) {
                namespacedKey = keyed.getKey();
            } else if (single instanceof String str) {

                if (str.contains(":")) {
                    String[] split = str.split(":");
                    namespacedKey = new NamespacedKey(split[0], split[1]);
                } else {
                    namespacedKey = new NamespacedKey("minecraft", str);
                }

            }

            return MinecraftKey.getConverter().getGeneric(new MinecraftKey(namespacedKey.getNamespace(), namespacedKey.getKey()));
        }

        @Override
        public Class<?> getInputType() {
            return Keyed.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.MinecraftKeyClass;
        }
    },

    // TODO
    RELATED_TO_NMS_ENTITYTYPES {
        @Override
        public Object convert(Object single) {
            if (single instanceof EntityData skriptEntityData) {
                single = skriptEntityData.toString();
                System.out.println("EntityData = " + single);
            } else if (single instanceof ch.njol.skript.entity.EntityType entityType) {
                single = entityType.toString();
                System.out.println("skript entitytype = " + single);
            }

            if (single instanceof String name) {
                single = EntityType.fromName(name.replace(" ", "_"));
            } else if (single instanceof Entity entity) {
                single = entity.getType();
            }

            if (single instanceof EntityType entityType) {
                System.out.println("BukkitConverters = " + BukkitConverters.getEntityTypeConverter().getGeneric(entityType));
                return BukkitConverters.getEntityTypeConverter().getGeneric(entityType);
            }

            return null;
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.EntityTypesClass;
        }
    },

    // TODO
    STRING_TO_NMS_NBT_COMPOUND_TAG {
        @Override
        public Object convert(@Nullable final Object single) {
            final String text = (String) single;
            Logging.warn("NBT Compound Tag conversion is not fully supported yet.");

            if (text == null || text.isEmpty() || Boolean.TRUE) {
                return NbtFactory.ofCompound("").getHandle();
            }
            
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream dataInput = new DataInputStream(byteArrayInputStream);
            NbtWrapper<Object> wrapper = NbtTextSerializer.DEFAULT.getBinarySerializer().deserialize(dataInput); // Issue here
            return wrapper.getHandle();
        }

        @Override
        public Class<?> getInputType() {
            return String.class;
        }

        @Override
        public Class<?> getOutputType() {
            return ConverterLogic.NBTTagCompoundClass;
        }
    };

    @Override
    public ConverterType getType() {
        return ConverterType.TO_NMS;
    }

}
