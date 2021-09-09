//package fr.anarchick.skriptpacket.elements.deprecated;
//
//import org.bukkit.event.Event;
//import org.eclipse.jdt.annotation.Nullable;
//
//import ch.njol.skript.Skript;
//import ch.njol.skript.lang.Expression;
//import ch.njol.skript.lang.ExpressionType;
//import ch.njol.skript.lang.SkriptParser.ParseResult;
//import ch.njol.skript.lang.util.SimpleExpression;
//import ch.njol.util.Kleenean;
//import ch.njol.util.coll.CollectionUtils;
//import fr.anarchick.skriptpacket.SkriptPacket;
//
//public class ExprNewPJSON extends SimpleExpression<PJSON> {
//    
//    private Expression<?> objExpr;
//    private int pattern;
//    private static final String[] patterns = new String[] {
//            "new pjson [from %-object%]"
//    };
//
//    static {
//        if (SkriptPacket.enableDeprecated) Skript.registerExpression(ExprNewPJSON.class, PJSON.class, ExpressionType.SIMPLE, patterns);
//    }
//    
//    @Override
//    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
//        pattern = matchedPattern;
//        objExpr =(Expression<?>) exprs[0];
//        return true;
//    }
//    
//    @Override
//    protected PJSON[] get(Event e) {
//        if (objExpr == null) return CollectionUtils.array(new PJSON());
//        Object obj = objExpr.getSingle(e);
//        return CollectionUtils.array(PJSON.create(obj));
//    }
//    
//    @Override
//    public boolean isSingle() {
//        return true;
//    }
//
//    @Override
//    public Class<? extends PJSON> getReturnType() {
//        return PJSON.class;
//    }
//
//    @Override
//    public String toString(@Nullable Event e, boolean debug) {
//        return patterns[pattern];
//    }
//    
//}
