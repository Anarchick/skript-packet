package fr.anarchick.skriptpacket.elements.deprecated;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;

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

@Name("Bukkit ItemStack")
@Description("Convert an NMS (net.minecraft.server) Entity to his Bukkit equivalent")
@Examples("set {_item} to itemstack from nms {_nmsItemStack}")
@Since("1.2")

public class ExprBukkitItemStack extends SimpleExpression<ItemStack> {

    private Expression<Object> nmsExpr;

    static {
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprBukkitItemStack.class, ItemStack.class, ExpressionType.SIMPLE, "itemstack from NMS %object%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        nmsExpr = (Expression<Object>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected ItemStack[] get(Event e) {
        Object nms = nmsExpr.getSingle(e);
        if (MinecraftReflection.isItemStack(nms) ) {
            return new ItemStack[] {MinecraftReflection.getBukkitItemStack(nms)};
        }
        return new ItemStack[0];
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "itemstack from NMS " + nmsExpr.toString(e, debug);
    }
    
}