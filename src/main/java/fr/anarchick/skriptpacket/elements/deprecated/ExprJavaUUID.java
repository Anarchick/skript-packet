package fr.anarchick.skriptpacket.elements.deprecated;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import fr.anarchick.skriptpacket.SkriptPacket;

@Name("Java UUID")
@Description("Get a java.util.UUID from a String UUID or create new one")
@Examples({
    "set {_uuid} to java uuid of (uuid of player)",
    "set {_nms} to a new java uuid"
})
@Since("1.2")

public class ExprJavaUUID extends SimpleExpression<UUID> {

    
    private final static Pattern regex = Pattern.compile("^[\\da-f]{8}-([\\da-f]{4}-){3}[\\da-f]{12}$", Pattern.CASE_INSENSITIVE); 
    private Expression<String> uuidExpr;
    private int pattern;
    private final static String[] patterns = new String[] {
            "[the] java uuid (from|of) %string%",
            "[a] new java uuid"
    };
    
    static {
        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprJavaUUID.class, UUID.class, ExpressionType.SIMPLE, patterns);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        if (pattern == 0) uuidExpr = (Expression<String>) exprs[0];
        return true;
    }
    
    @Override
    @Nullable
    protected UUID[] get(Event e) {
        if (pattern == 0) {
            String uuid = uuidExpr.getSingle(e);
            if (uuid != null) {
                Matcher matcher = regex.matcher(uuid);
                if (matcher.find()) return new UUID[] {UUID.fromString(uuid)};
            }
            return new UUID[0];
        }
        return new UUID[] {UUID.randomUUID()};
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends UUID> getReturnType() {
        return UUID.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }
    
}