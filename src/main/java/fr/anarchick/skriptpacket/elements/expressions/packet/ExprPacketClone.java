package fr.anarchick.skriptpacket.elements.expressions.packet;

import ch.njol.skript.util.LiteralUtils;
import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.NotNull;

@Name("Clone of Packet")
@Description("Get a full copy (=deep) or a fast copy (=shallow) of a packet")
@Examples("set {_copy} to deep clone of event-packet")
@Since("1.1")

public class ExprPacketClone extends SimplePropertyExpression<PacketContainer, PacketContainer>{
    
    private int mark;
    private String pattern;
    
    static {
        register(ExprPacketClone.class, PacketContainer.class,
                "[packet] (0¦deep|1¦shallow) (clone|copy)", "packet");
    }
    
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parser) {
        mark = parser.mark;
        pattern = parser.expr;

        if (LiteralUtils.hasUnparsedLiteral(exprs[0])) {
            setExpr(LiteralUtils.defendExpression(exprs[0]));
            return LiteralUtils.canInitSafely(getExpr());
        }

        setExpr((Expression<? extends PacketContainer>) exprs[0]);
        return true;
    }
    
    @Override
    public PacketContainer convert(PacketContainer packet) {
        return (mark == 0) ? packet.deepClone() : packet.shallowClone();
    }
    
    @Override
    public @NotNull Class<? extends PacketContainer> getReturnType() {
        return PacketContainer.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return pattern;
    }
    
}
