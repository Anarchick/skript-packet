package fr.anarchick.skriptpacket.elements.deprecated;

import fr.anarchick.skriptpacket.util.Converter;
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
        return Converter.getBiomeID(biome);
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
