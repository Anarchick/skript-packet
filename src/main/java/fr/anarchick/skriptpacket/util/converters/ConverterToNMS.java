package fr.anarchick.skriptpacket.util.converters;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.util.slot.Slot;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
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
        public Class<?> getReturnType() {
            return Object.class;
        }
    },

    SKRIPT_SLOT_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getMinecraftItemStack(((Slot) single).getItem());
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    BUKKIT_MATERIAL_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getMinecraftItemStack(new ItemStack((Material) single));
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    BUKKIT_ITEMSTACK_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getMinecraftItemStack((ItemStack) single);
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    BUKKIT_BLOCK_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return BUKKIT_MATERIAL_TO_NMS_ITEMSTACK.convert(((Block) single).getType());
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.ItemStackClass;
        }
    },

    BUKKIT_BLOCKDATA_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return BUKKIT_MATERIAL_TO_NMS_ITEMSTACK.convert(((BlockData) single).getMaterial());
        }

        @Override
        public Class<?> getReturnType() {
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
        public Class<?> getReturnType() {
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
            if (v == null) return single;
            try {
                return ConverterLogic.blockPositionConstructor.newInstance(v.getBlockX(), v.getBlockY(), v.getBlockZ());
            } catch (Exception ex) {
                throw new RuntimeException("Cannot construct BlockPosition.", ex);
            }
        }

        @Override
        public Class<?> getReturnType() {
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
        public Class<?> getReturnType() {
            return ConverterLogic.IBlockDataClass;
        }
    },

    BUKKIT_MATERIAL_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData(((Material) single)).getHandle();
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.IBlockDataClass;
        }
    },

    BUKKIT_BLOCK_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData((((Block) single)).getBlockData()).getHandle();
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.IBlockDataClass;
        }
    },

    BUKKIT_BLOCKDATA_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData(single).getHandle();
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.IBlockDataClass;
        }
    },

    BUKKIT_VECTOR_TO_NMS_VEC3D {
        @Override
        public Object convert(final Object single) {
            return BukkitConverters.getVectorConverter().getGeneric((Vector) single);
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.Vec3DClass;
        }
    },

    BUKKIT_BIOME_TO_NMS_BIOME_ID {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.getBiomeID((Biome) single);
        }

        @Override
        public Class<?> getReturnType() {
            return Integer.class;
        }
    },

    STRING_TO_NMS_ICHATBASECOMPONENT {
        @Override
        public Object convert(@Nonnull final Object single) {
            final String text = Optional.ofNullable((String)single).orElse("");
            if (text.startsWith("{") && text.endsWith("}"))
                return WrappedChatComponent.fromJson(text).getHandle();
            return WrappedChatComponent.fromText(text).getHandle();
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.IChatBaseComponentClass;
        }
    },

    RELATED_TO_NMS_MINECRAFTKEY {
        @Override
        public Object convert(@Nonnull final Object single) {
            NamespacedKey namespacedKey = new NamespacedKey("minecraft", "air");

            if (single instanceof Keyed keyed) {
                namespacedKey = keyed.getKey();
            }

            return MinecraftKey.getConverter().getGeneric(new MinecraftKey(namespacedKey.getNamespace(), namespacedKey.getKey()));
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.MinecraftKeyClass;
        }
    },

    // TODO
    PROTOCOLLIB_WRAPPED_DATA_WATCHER_TO_NMS {
        @Override
        public Object convert(final Object single) {
            WrappedDataWatcher dw = (WrappedDataWatcher) single;
            List<Object> nmsList = new ArrayList<>();
            for (WrappedWatchableObject wwo : dw.getWatchableObjects()) {
                nmsList.add(wwo.getHandle());
            }
            return nmsList;
        }

        @Override
        public Class<?> getReturnType() {
            return Object.class;
        }
    },

    // TODO
    PROTOCOLLIB_WRAPPED_WATCHABLE_OBJECT_TO_NMS {
        @Override
        public Object convert(final Object single) {
            return ((WrappedWatchableObject) single).getHandle();
        }

        @Override
        public Class<?> getReturnType() {
            return Object.class;
        }
    },

    // TODO
    RELATED_TO_NMS_ENTITYTYPE {
        @Override
        public Object convert(Object single) {
            if (single instanceof EntityData skriptEntityData) {
                single = skriptEntityData.toString();
            }

            if (single instanceof String name) {
                try {
                    net.minecraft.world.entity.EntityType.byString(name);
                } catch ( Exception ignored ) {}
            } else if (single instanceof Entity entity) {
                single = entity.getType();
            }

            if (single instanceof EntityType entityType) {
                return BukkitConverters.getEntityTypeConverter().getGeneric(entityType);
            }

            return null;
        }

        @Override
        public Class<?> getReturnType() {
            return ConverterLogic.EntityTypesClass;
        }
    }

}
