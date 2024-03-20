package fr.anarchick.skriptpacket.util.converters;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.BlockUtils;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.Vector3F;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Optional;

public enum ConverterToBukkit implements Converter {

    NMS_WORLD_TO_BUKKIT_WORLD {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.callMethod(single, "getWorld");
        }

        @Override
        public Class<?> getInputType() {
            return ConverterLogic.WorldServerClass;
        }

        @Override
        public Class<?> getOutputType() {
            return World.class;
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

        @Override
        public Class<?> getInputType() {
            return ConverterLogic.EntityClass;
        }

        @Override
        public Class<?> getOutputType() {
            return Entity.class;
        }
    },

    /**
     * NMS Chunk to Bukkit chunk
     * Not availaible anymore
     */
    @Deprecated
    NMS_CHUNK_TO_BUKKIT_CHUNK {
        @Override
        public Object convert(final Object single) {
            return ConverterLogic.callMethod(single, "getBukkitChunk");
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Chunk.class;
        }
    },

    NMS_BLOCKPOSITION_TO_BUKKIT_LOCATION {
        @Override
        public Object convert(final Object single) {
            final World world = Bukkit.getWorlds().get(0);
            return BlockPosition.getConverter().getSpecific(single).toLocation(world);
        }

        @Override
        public Class<?> getInputType() {
            return ConverterLogic.BlockPositionClass;
        }

        @Override
        public Class<?> getOutputType() {
            return Location.class;
        }
    },

    NMS_VEC3D_TO_BUKKIT_VECTOR {
        @Override
        public Object convert(final Object single) {
            return BukkitConverters.getVectorConverter().getSpecific(single);
        }

        @Override
        public Class<?> getInputType() {
            return ConverterLogic.Vec3DClass;
        }

        @Override
        public Class<?> getOutputType() {
            return Vector.class;
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

        @Override
        public Class<?> getInputType() {
            return Vector3F.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Vector.class;
        }
    },

    NMS_ITEMSTACK_TO_BUKKIT_ITEMSTACK {
        @Override
        public Object convert(final Object single) {
            return MinecraftReflection.getBukkitItemStack(single);
        }

        @Override
        public Class<?> getInputType() {
            return ConverterLogic.ItemStackClass;
        }

        @Override
        public Class<?> getOutputType() {
            return ItemStack.class;
        }
    },

    // TODO to Skript ItemType ?

    NMS_BLOCK_TO_BUKKIT_MATERIAL {
        @Override
        public Object convert(final Object single) {
            return BukkitConverters.getBlockConverter().getSpecific(single);
        }

        @Override
        public Class<?> getInputType() {
            return ConverterLogic.BlockClass;
        }

        @Override
        public Class<?> getOutputType() {
            return Material.class;
        }
    },

    NMS_IBLOCKDATA_TO_BUKKIT_BLOCKDATA {
        @Override
        public Object convert(final Object single) {
            String blockDataString = single.toString();
            blockDataString = blockDataString.replace("Block{", "");
            blockDataString = blockDataString.replace("}", "");
            blockDataString = blockDataString.replace(",", ";");
            return BlockUtils.createBlockData(blockDataString);
        }

        @Override
        public Class<?> getInputType() {
            return ConverterLogic.IBlockDataClass;
        }

        @Override
        public Class<?> getOutputType() {
            return BlockData.class;
        }
    },

    // TODO to Skript ItemType ?
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
                } catch ( IllegalArgumentException ignored) {}

            }

            return Material.AIR;
        }

        @Override
        public Class<?> getInputType() {
            return Object.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Material.class;
        }
    },

    STRING_TO_PAPER_COMPONENT {
        @Override
        public Object convert(final Object single) {
            final String text = Optional.ofNullable((String)single).orElse("");
            return ConverterLogic.MINI_MESSAGE.deserialize(text);
        }

        @Override
        public Class<?> getInputType() {
            return String.class;
        }

        @Override
        public Class<?> getOutputType() {
            return Component.class;
        }
    };


    @Override
    public ConverterType getType() {
        return ConverterType.TO_BUKKIT;
    }

}
