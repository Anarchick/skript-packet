package fr.anarchick.skriptpacket.elements.deprecated;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

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

@Name("Bukkit ChatComponent")
@Description("Convert an NMS (net.minecraft.server) ChatComponentText to a Json string")
@Examples("set {_json} to chatcomponent from nms {_nmsChatComponent}")
@Since("1.2")

public class ExprBukkitChatComponent extends SimpleExpression<String> {

    private Expression<Object> nmsExpr;

    static {
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprBukkitChatComponent.class, String.class, ExpressionType.SIMPLE, "chatcomponent from NMS %object%");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        nmsExpr = (Expression<Object>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected String[] get(Event e) {
        Object nms = nmsExpr.getSingle(e);
        if (MinecraftReflection.isMinecraftObject(nms, "ChatComponentText")) {
            return new String[] {WrappedChatComponent.fromHandle(nms).getJson()};
        }
        return new String[0];
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "chatcomponent from NMS " + nmsExpr.toString(e, debug);
    }
    
}