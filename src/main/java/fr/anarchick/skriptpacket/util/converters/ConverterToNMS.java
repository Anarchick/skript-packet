package fr.anarchick.skriptpacket.util.converters;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.slot.Slot;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public enum ConverterToNMS implements Converter {

    /**
     * Return NMS from Bukkit :
     * Entity
     * ItemStack
     * World
     */
    HANDLE {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.getHandle(single);
        }
    },

    SKRIPT_SLOT_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getMinecraftItemStack(((Slot) single).getItem());
        }
    },

    BUKKIT_MATERIAL_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.getHandle(new ItemStack((Material) single));
        }
    },

    BUKKIT_ITEMSTACK_TO_NMS_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getMinecraftItemStack((ItemStack) single);
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
            }
            return BUKKIT_ITEMSTACK_TO_NMS_ITEMSTACK.convert(new ItemStack(Material.AIR));
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
    },

    BUKKIT_MATERIAL_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData(((Material) single)).getHandle();
        }
    },

    BUKKIT_BLOCK_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData((((Block) single)).getBlockData()).getHandle();
        }
    },

    BUKKIT_BLOCKDATA_TO_NMS_IBLOCKDATA {
        @Override
        public Object convert(final Object single) {
            return WrappedBlockData.createData(single).getHandle();
        }
    },

    BUKKIT_VECTOR_TO_NMS_VEC3D {
        @Override
        public Object convert(final Object single) {
            return BukkitConverters.getVectorConverter().getGeneric((Vector) single);
        }
    },

    BUKKIT_BIOME_TO_NMS_BIOME_ID {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.getBiomeID((Biome) single);
        }
    },

    STRING_TO_NMS_ICHATBASECOMPONENT {
        @Override
        public Object convert(@Nonnull final Object single) {
            final String text = (String)single;
            if (text.startsWith("{") && text.endsWith("}"))
                return WrappedChatComponent.fromJson(text).getHandle();
            return WrappedChatComponent.fromText(text).getHandle();
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
    },

    // TODO
    PROTOCOLLIB_WRAPPED_WATCHABLE_OBJECT_TO_NMS{
        @Override
        public Object convert(final Object single) {
            return ((WrappedWatchableObject) single).getHandle();
        }
    }

}
