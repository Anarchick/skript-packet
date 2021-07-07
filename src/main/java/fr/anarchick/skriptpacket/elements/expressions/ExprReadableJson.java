package fr.anarchick.skriptpacket.elements.expressions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

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
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

@Name("Readable Json")
@Description("get a readable text from a Json string")
@Examples("set {_text} to json \"{Text:Hello,Color:red}\" as readable text")
@Since("1.2")

public class ExprReadableJson extends SimpleExpression<String> {

    private Expression<String> jsonExpr;

    static {
        Skript.registerExpression(ExprReadableJson.class, String.class, ExpressionType.SIMPLE, "json %string% as readable text");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        jsonExpr = (Expression<String>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected String[] get(Event e) {
        String json = jsonExpr.getSingle(e);
        return new String[] {BaseComponent.toLegacyText(ComponentSerializer.parse(json))};
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
        return "json '" + jsonExpr.toString(e, debug) + "' as readable text";
    }
    
}