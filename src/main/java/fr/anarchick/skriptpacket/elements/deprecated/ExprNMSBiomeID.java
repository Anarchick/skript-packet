package fr.anarchick.skriptpacket.elements.deprecated;

import org.bukkit.block.Biome;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.anarchick.skriptpacket.SkriptPacket;

@Name("NMS Biome ID")
@Description({"Get the NMS ID of biome. This method return a number",
        "Not guaranted to be up to date. Refer to https://minecraft.gamepedia.com/Biome/ID"
})
@Examples("broadcast \"%nms biome id of crimson forest%\"")
@Since("1.1 (mc1.16)")

public class ExprNMSBiomeID extends SimplePropertyExpression<Biome, Number>{
    
    static {
        if (SkriptPacket.enableDeprecated) register(ExprNMSBiomeID.class, Number.class, "nms biome id", "biome");
    }

    @Override
    public Number convert(Biome biome) {
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
                return biome.ordinal();
        }
    }
    
    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "nms biome id of %biome%";
    }
    
}
