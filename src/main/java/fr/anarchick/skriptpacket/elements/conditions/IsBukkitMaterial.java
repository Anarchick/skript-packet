package fr.anarchick.skriptpacket.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Name("Is Bukkit Material")
@Description("Check if a given object is an instance of org.bukkit.Material")
@Examples("if {_something} is Bukkit Material:")
@Since("2.2.0")

public class IsBukkitMaterial extends PropertyCondition<Object> {

    static {
        register(IsBukkitMaterial.class, "Bukkit Material", "objects");
    }

    @Override
    public boolean check(Object value) {
        return value instanceof Material;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "Bukkit Material";
    }

}