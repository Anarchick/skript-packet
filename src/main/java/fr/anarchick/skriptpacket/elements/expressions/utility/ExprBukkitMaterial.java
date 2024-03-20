package fr.anarchick.skriptpacket.elements.expressions.utility;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.anarchick.skriptpacket.util.converters.ConverterToBukkit;
import org.bukkit.Material;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Bukkit Material")
@Description("Get the Material of an item or block")
@Examples({
        "broadcast material of tool",
        "broadcast material of (1 of stone)",
        "broadcast {_block}'s material",
        "broadcast material of \"STONE\"",
})
@Since("2.2.0")

public class ExprBukkitMaterial extends SimplePropertyExpression<Object, Material> {
    
    static {
        register(ExprBukkitMaterial.class, Material.class,
                "[Bukkit] Material", "itemstack/itemtype/block/blockdata/string"
        );
    }

    @Override
    public @Nullable Material convert(Object from) {
        return (Material) ConverterToBukkit.RELATED_TO_BUKKIT_MATERIAL.convert(from);
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "Bukkit Material";
    }

    @Override
    public @NotNull Class<? extends Material> getReturnType() {
        return Material.class;
    }
    
}
