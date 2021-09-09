package fr.anarchick.skriptpacket.elements.deprecated;

import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import fr.anarchick.skriptpacket.SkriptPacket;

public class ExprWatchableObjectIndex extends SimplePropertyExpression<WrappedWatchableObject, Number>{
    
    static {
        if (SkriptPacket.enableDeprecated) register(ExprWatchableObjectIndex.class, Number.class, "watched index", "watcheritem");
    }

    @Override
    public Number convert(WrappedWatchableObject watcher) {
        return watcher.getIndex();
    }
    
    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return "watched index";
    }
    
}
