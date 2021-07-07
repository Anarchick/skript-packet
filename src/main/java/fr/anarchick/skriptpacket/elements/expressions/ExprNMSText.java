package fr.anarchick.skriptpacket.elements.expressions;

import java.util.Arrays;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

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

@Name("NMS TextComponent")
@Description("Get the NMS (net.minecraft.server) textcomponent from a string")
@Examples({
    "set {_textcomponent} to nms textcomponent from text \"<green>Connected\"",
    "set {_textcomponent} to nms textcomponent from json \"{text:'test',color:'red'}\""
})
@Since("1.2")

public class ExprNMSText extends SimpleExpression<Object> {

    private Expression<String> stringExpr;
    private int pattern;
    private final static String[] patterns;

    static {
        patterns = new String[] {
                "NMS TextComponent from text %string%",
                "NMS TextComponent from json %string%",
                "NMS TextComponent from chatmessage %string%"
        };
        Skript.registerExpression(ExprNMSText.class, Object.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        stringExpr = (Expression<String>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        final String text = stringExpr.getSingle(e);
        if (text.isEmpty() || text == null) return CollectionUtils.array(WrappedChatComponent.fromText("").getHandle());
        switch (pattern) {
            case 0:
                return CollectionUtils.array(WrappedChatComponent.fromText(text).getHandle());
            case 1:
                return CollectionUtils.array(WrappedChatComponent.fromJson(text).getHandle());
            case 2:
                Object[] handle = Arrays.stream(WrappedChatComponent.fromChatMessage(text))
                        .map(WrappedChatComponent::getHandle)
                        .toArray(Object[]::new);
                return CollectionUtils.array(handle);
            default:
                return CollectionUtils.array(WrappedChatComponent.fromText("").getHandle());
        }
        
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
    
}