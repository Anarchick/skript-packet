package fr.anarchick.skriptpacket.elements.expressions.packet;

import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@Name("Clone of Packet")
@Description("Get a full copy (=deep) or a fast copy (=shallow) of a packet")
@Examples("set {_copy} to deep clone of event-packet")
@Since("1.1")

public class ExprPacketClone extends SimplePropertyExpression<PacketContainer, PacketContainer>{
    
    private int mark;
    private String pattern;
    
    static {
        register(ExprPacketClone.class, PacketContainer.class, "[packet] (0¦deep|1¦shallow) (clone|copy)", "packet");
    }
    
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        mark = parser.mark;
        pattern = parser.expr;
        return true;
    }
    
    @Override
    public PacketContainer convert(PacketContainer packet) {
        return (mark == 0) ? packet.deepClone() : packet.shallowClone();
    }
    
    @Override
    public Class<? extends PacketContainer> getReturnType() {
        return PacketContainer.class;
    }

    @Override
    protected String getPropertyName() {
        return pattern;
    }
    
}
