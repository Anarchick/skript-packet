package fr.anarchick.skriptpacket.elements.expressions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.wrappers.ComponentConverter;
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

@Name("BaseComponent")
@Description("Get net.md_5.bungee.api.chat.BaseComponent from a string")
@Examples({
    "set {_basecomponent} to basecomponent from text \"<green>Connected\"",
    "set {_textcomponent} to basecomponent from json \"{text:'test',color:'red'}\""
})
@Since("1.2")

public class ExprBaseComponent extends SimpleExpression<Object> {

    private Expression<String> stringExpr;
    private int pattern;
    private final static String[] patterns;

    static {
        patterns = new String[] {
                "BaseComponent from text %string%",
                "BaseComponent from json %string%"
        };
        Skript.registerExpression(ExprBaseComponent.class, Object.class, ExpressionType.SIMPLE, patterns);
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
        WrappedChatComponent wrapper = WrappedChatComponent.fromText("");
        if (!text.isEmpty() || text != null) {
            switch (pattern) {
                case 0:
                    wrapper = WrappedChatComponent.fromText(text);
                    break;
                case 1:
                    wrapper = WrappedChatComponent.fromJson(text);
                    break;
                default:
                    break;
            }
        }
        return new Object[] {ComponentConverter.fromWrapper(wrapper)};
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