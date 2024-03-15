package fr.anarchick.skriptpacket.util.converters;

import ch.njol.skript.aliases.ItemType;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.Vector3F;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public enum ConverterToBukkit implements Converter {

    NMS_WORLD_TO_BUKKIT_WORLD {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.callMethod(single, "getWorld");
        }
    },

    /**
     * NMS entity to Bukkit entity
     */
    NMS_ENTITY_TO_BUKKIT_ENTITY {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getBukkitEntity(single);
        }
    },

    /**
     * NMS Chunk to Bukkit chunk
     *
     */
    NMS_CHUNK_TO_BUKKIT_CHUNK {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.callMethod(single, "getBukkitChunk");
        }
    },

    NMS_BLOCKPOSITION_TO_BUKKIT_LOCATION {
        @Override
        public Object convert(final Object single) {
            final World world = Bukkit.getWorlds().get(0);
            return BlockPosition.getConverter().getSpecific(single).toLocation(world);
        }
    },

    NMS_VEC3D_TO_BUKKIT_VECTOR {
        @Override
        public Object convert(final Object single) {
            return BukkitConverters.getVectorConverter().getSpecific(single);
        }
    },

    NMS_ITEMSTACK_TO_BUKKIT_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getBukkitItemStack(single);
        }
    },

    NMS_BLOCK_TO_BUKKIT_MATERIAL {
        @Override
        public Object convert(final Object single) {
            return BukkitConverters.getBlockConverter().getSpecific(single);
        }
    },

    NMS_IBLOCKDATA_TO_BUKKIT_MATERIAL {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.callMethod(single, "getBukkitMaterial");
        }
    },

    PROTOCOLLIB_VECTOR3F_TO_BUKKIT_VECTOR {
        @Override
        public Object convert(final Object single) {
            if (single instanceof Vector3F vec) {
                return new Vector(vec.getX(), vec.getY(), vec.getZ());
            }
            return single;
        }
    },

    RELATED_TO_BUKKIT_MATERIAL {
        @Nonnull
        @Override
        public Object convert(final Object obj) {

            if ( obj == null ) {
                return Material.AIR;
            }

            if ( obj instanceof ItemType item ) {
                return item.getMaterial();
            }

            if ( obj instanceof ItemStack item ) {
                return item.getType();
            }

            if ( obj instanceof Block block ) {
                return block.getType();
            }

            if ( obj instanceof BlockData blockData ) {
                return blockData.getMaterial();
            }

            if ( obj instanceof String str ) {

                try {
                    return Material.valueOf(str.toUpperCase());
                } catch ( IllegalArgumentException ex ) {}

            }

            return Material.AIR;
        }
    }

}
