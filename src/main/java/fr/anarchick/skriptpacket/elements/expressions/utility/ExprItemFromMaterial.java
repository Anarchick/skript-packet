package fr.anarchick.skriptpacket.elements.expressions.utility;

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
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Item from Material")
@Description("Get the ItemStack from a Material")
@Examples({
        "give 1 of item from {_material} to player"
})
@Since("2.2.0")

public class ExprItemFromMaterial extends SimpleExpression<ItemStack> {

    private Expression<Material> exprMaterial;
    private Expression<Number> exprAmount;
    
    static {
        Skript.registerExpression(ExprItemFromMaterial.class, ItemStack.class, ExpressionType.COMBINED,
                "%integer% of item[s] from %material%");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        exprAmount = (Expression<Number>) exprs[0];
        exprMaterial = (Expression<Material>) exprs[1];
        return true;
    }

    @Override
    @Nullable
    protected ItemStack @NotNull [] get(@NotNull Event e) {
        if (exprMaterial == null || exprAmount == null) {
            return new ItemStack[0];
        }

        final Material material = exprMaterial.getSingle(e);
        final int amount = exprAmount.getSingle(e).intValue();
        return new ItemStack[] { new ItemStack(material, amount) };
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public @NotNull Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
            return exprAmount.getSingle(e) + " of item from " + exprMaterial.getSingle(e);
    }
    
}
