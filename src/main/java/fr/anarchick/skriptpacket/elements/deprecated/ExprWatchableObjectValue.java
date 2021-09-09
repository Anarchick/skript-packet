package fr.anarchick.skriptpacket.elements.deprecated;

import org.bukkit.event.Event;

import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.anarchick.skriptpacket.SkriptPacket;

public class ExprWatchableObjectValue extends SimplePropertyExpression<WrappedWatchableObject, Object>{
    
    static {
        if (SkriptPacket.enableDeprecated) register(ExprWatchableObjectValue.class, Object.class, "watched value", "watcheritem");
    }

    @Override
    public Object convert(WrappedWatchableObject watcher) {
        return watcher.getValue();
    }

    @Override
    public Class<?>[] acceptChange(final ChangeMode mode) {
        return (mode == ChangeMode.SET) ? CollectionUtils.array(Object.class) :  null;
    }
    
    @Override
    public void change(Event e, Object[] delta, ChangeMode mode){
        if (delta != null && mode == ChangeMode.SET) {
            WrappedWatchableObject watcher = getExpr().getSingle(e);
            watcher.setValue(delta[0]);
        }
    }
    
    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
    }

    @Override
    protected String getPropertyName() {
        return "watched value";
    }
    
}
