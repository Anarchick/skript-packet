package fr.anarchick.skriptpacket.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.comphenix.protocol.utility.MinecraftReflection;
import org.jetbrains.annotations.NotNull;

@Name("Is MMS")
@Description("Check if a given object can be found within the package net.minecraft.server")
@Examples("if {_something} is NMS:")
@Since("2.2.0")

public class IsNMS extends PropertyCondition<Object> {

    static {
        register(IsNMS.class, "NMS", "objects");
    }

    @Override
    public boolean check(Object value) {
        return MinecraftReflection.isMinecraftObject(value);
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "NMS";
    }

}
