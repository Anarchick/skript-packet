package fr.anarchick.skriptpacket.elements.deprecated;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.json.JSONObject;

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
import ch.njol.util.coll.CollectionUtils;
import fr.anarchick.skriptpacket.SkriptPacket;

@Name("NMS TextComponent")
@Description("Get the NMS (net.minecraft.server) textcomponent from a string")
@Examples({
    "set {_textcomponent} to nms textcomponent from text \"<green>Connected\"",
    "set {_textcomponent} to nms textcomponent from json \"{text:'test',color:'red'}\""
})
@Since("1.2")

public class ExprNMSText extends SimpleExpression<Object> {

    private static final Object[] empty = CollectionUtils.array(WrappedChatComponent.fromText("").getHandle());
    private Expression<String> stringExpr;
    private final static String[] patterns = new String[] {
            "NMS TextComponent from text %string%"
    };

    static { 
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprNMSText.class, Object.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        stringExpr = (Expression<String>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        String text = stringExpr.getSingle(e);
        if (text == null ||text.isEmpty()) return empty;
        if (text.startsWith("{") && text.endsWith("}")) {
            try {
                @SuppressWarnings("unused")
                JSONObject json = new JSONObject(text);
                return CollectionUtils.array(WrappedChatComponent.fromJson(text).getHandle());
            } catch (Exception ex) {
                return null;
            }
        }
        return CollectionUtils.array(WrappedChatComponent.fromText(text).getHandle());
//        Object[] handle = Arrays.stream(WrappedChatComponent.fromChatMessage(text))
//                .map(WrappedChatComponent::getHandle)
//                .toArray(Object[]::new);
//        return CollectionUtils.array(handle);
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
        return "NMS TextComponent from text " + stringExpr.toString(e, debug);
    }
    
}