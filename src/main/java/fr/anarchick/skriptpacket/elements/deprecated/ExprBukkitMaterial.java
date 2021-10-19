package fr.anarchick.skriptpacket.elements.deprecated;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.anarchick.skriptpacket.SkriptPacket;

@Name("Bukkit Material")
@Description("Convert an NMS (net.minecraft.server) IBlockData or ItemStack to his Bukkit Material")
@Examples("set {_material} to material from nms {_nmsIBlockData}")
@Since("1.2")

public class ExprBukkitMaterial extends SimpleExpression<Material> {

    private Expression<Object> nmsExpr;

    static {
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprBukkitMaterial.class, Material.class, ExpressionType.SIMPLE,
                "material from NMS %object%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        nmsExpr = (Expression<Object>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Material[] get(Event e) {
        Object nms = nmsExpr.getSingle(e);       
        Material material = null;
        if (MinecraftReflection.isMinecraftObject(nms)) {
            if (MinecraftReflection.isMinecraftObject(nms, "IBlockData") ) {
                try {
                    material = (Material) nms.getClass().getMethod("getBukkitMaterial").invoke(nms);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e1) {
                    Skript.exception(e1);
                }
            } else if (MinecraftReflection.isMinecraftObject(nms, "Block") ) {
                material = BukkitConverters.getBlockConverter().getSpecific(nms);
            } else if (MinecraftReflection.isItemStack(nms) ) {
                material = MinecraftReflection.getBukkitItemStack(nms).getType();
            }
        }
        return new Material[] {material};
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends Material> getReturnType() {
        return Material.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "material from NMS " + nmsExpr.toString(e, debug);
    }
    
}