package fr.anarchick.skriptpacket.elements.expressions;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import fr.anarchick.skriptpacket.util.Converter;

@Name("NMS")
@Description("Get the NMS (net.minecraft.server) from an entity/block/itemtype/itemstack/world/chunk")
@Examples({
    "set {_nms} to nms of tool of player",
    "set {_nms} to nms of event-entity"
})
@Since("1.0, 1.2 (nbt)")

public class ExprNMS extends SimpleExpression<Object> {

    private Expression<Entity> entityExpr;
    private Expression<Block> blockExpr;
    private Expression<ItemType> itemtypeExpr;
    private Expression<ItemStack> itemstackExpr;
    private Expression<World> worldExpr;
    private Expression<Chunk> chunkExpr;
    private Expression<String> stringExpr;
    private int pattern;
    private final static String[] patterns;

    static {
        patterns = new String[] {
                "NMS [entity] of %entity%",
                "NMS [block] of %block%",
                "NMS block from %itemtype%",
                "NMS [itemstack] of %itemstack%",
                "NMS [world] of %world%",
                "NMS [chunk] of %chunk%",
                "NMS [nbt] of %string%"
        };
        Skript.registerExpression(ExprNMS.class, Object.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        switch (pattern) {
            case 0:
                entityExpr = (Expression<Entity>) exprs[0];
                break;
            case 1:
                blockExpr = (Expression<Block>) exprs[0];
                break;
            case 2:
                itemtypeExpr = (Expression<ItemType>) exprs[0];
                break;
            case 3:
                itemstackExpr = (Expression<ItemStack>) exprs[0];
                break;
            case 4:
                worldExpr = (Expression<World>) exprs[0];
                break;
            case 5:
                chunkExpr = (Expression<Chunk>) exprs[0];
                break;
            case 6:
                stringExpr = (Expression<String>) exprs[0];
                break;
            default:
                return false;
        }
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        if (pattern == 6) {
            String nbt = stringExpr.getSingle(e);
            if (nbt == null || nbt.isEmpty()) return null;
            Object nms = null;
            Class<?> clazz = MinecraftReflection.getMinecraftClass("MojangsonParser");
            try {
                nms = clazz.getMethod("parse", String.class).invoke(null, nbt);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e1) {
                Skript.exception(e1);
            }
            return CollectionUtils.array(nms);
        }
        return CollectionUtils.array(Converter.auto((getExpr(e))));
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }
    
    private Object[] getExpr(Event e) {
        switch (pattern) {
            case 0:
                return entityExpr.getAll(e);
            case 1:
                return blockExpr.getAll(e);
            case 2:
                return itemtypeExpr.getAll(e);
            case 3:
                return itemstackExpr.getAll(e);
            case 4:
                return worldExpr.getAll(e);
            case 5:
                return chunkExpr.getAll(e);
            default:
                return null;
        }
    }
    
}