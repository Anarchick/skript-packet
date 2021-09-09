//package fr.anarchick.skriptpacket.elements.deprecated;
//
//import org.bukkit.event.Event;
//import org.eclipse.jdt.annotation.Nullable;
//
//import ch.njol.skript.Skript;
//import ch.njol.skript.classes.Changer.ChangeMode;
//import ch.njol.skript.lang.Expression;
//import ch.njol.skript.lang.ExpressionType;
//import ch.njol.skript.lang.SkriptParser.ParseResult;
//import ch.njol.skript.lang.util.SimpleExpression;
//import ch.njol.util.Kleenean;
//import ch.njol.util.coll.CollectionUtils;
//import fr.anarchick.skriptpacket.SkriptPacket;
//
//public class ExprPJSON extends SimpleExpression<Object> {
//    
//    private Expression<String> keyExpr;
//    private Expression<PJSON> pjsonExpr;
//
//    static {
//        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprPJSON.class, Object.class, ExpressionType.SIMPLE, "[pjson] key %string% of %pjson%");
//    }
//    
//    @Override
//    @SuppressWarnings("unchecked")
//    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
//        keyExpr = (Expression<String>) exprs[0];
//        pjsonExpr = (Expression<PJSON>) exprs[1];
//        return true;
//    }
//    
//    @Override
//    protected Object[] get(Event event) {
//        String key = keyExpr.getSingle(event);
//        PJSON pjson = pjsonExpr.getSingle(event);
//        if (key == null || pjson == null) return null;
//        return new Object[] {pjson.get(key)};
//    }
//
//    @Override
//    public Class<?>[] acceptChange(ChangeMode mode) {
//        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
//            return CollectionUtils.array(Object.class);
//        }
//        return null;
//    }
//    
//    @Override
//    public void change(Event e, Object[] delta, ChangeMode mode) {
//        String key = keyExpr.getSingle(e);
//        PJSON pjson = pjsonExpr.getSingle(e);
//        if (key == null || pjson == null || delta[0] == null) return;
//        if (mode == ChangeMode.SET) {
//            pjson.put(key, delta[0]);
//        } else if (mode == ChangeMode.DELETE) {
//            pjson.remove(key);
//        }
//    }
//    
//    @Override
//    public boolean isSingle() {
//        return true;
//    }
//
//    @Override
//    public Class<? extends Object> getReturnType() {
//        return Object.class;
//    }
//
//    @Override
//    public String toString(@Nullable Event e, boolean debug) {
//        return "pjson key " + keyExpr.toString(e, debug) + " of " + pjsonExpr.toString(e, debug);
//    }
//    
//}
