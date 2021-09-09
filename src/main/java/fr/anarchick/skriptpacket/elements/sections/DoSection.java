package fr.anarchick.skriptpacket.elements.sections;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.variables.Variables;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.util.Kleenean;
import fr.anarchick.skriptpacket.util.Scheduling;
import fr.anarchick.skriptpacket.util.SkriptReflection;

// This is experimental

@NoDoc
public class DoSection extends Section {
    
    private enum Type {
        ASYNC, SYNC, PARALLEL;
    }
    
    private boolean shouldWait = false;
    private Type type;
    private TriggerSection trigger;
    private int pattern;
    private static final String[] patterns = new String[] {
            "(async|do in background) [(1¦and wait)]",
            "(sync|do) [(1¦and wait)]",
            "(parallel|do in parallel) [(1¦and wait)]"
    };
    private static final Type[] types = Type.values();
    
    static {
        Skript.registerSection(DoSection.class, patterns);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser,
                SectionNode sectionNode, List<TriggerItem> triggerItems) {
        shouldWait = parser.mark == 1;
        pattern = matchedPattern;
        type = types[pattern];
        trigger = new TriggerSection(sectionNode) {
            @Override
            public String toString(@Nullable Event e, boolean debug) {
                return this.toString(e, debug);
            }
            
            @Override
            protected @Nullable TriggerItem walk(Event e) {
                return walk(e, true);
            }
        };
        SkriptReflection.setHasDelayBefore(Kleenean.TRUE);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event e) {
        shouldWait = shouldWait && getNext() != null;
        Event event = (!type.equals(Type.PARALLEL)) ? e : new Event(e.isAsynchronous()) {
            private final HandlerList handlers = new HandlerList();
            
            @Override
            public HandlerList getHandlers() {
                return this.handlers;
            }
            
            };
        Object localVars = (shouldWait) ? Variables.removeLocals(e) : SkriptReflection.copyLocals(SkriptReflection.getLocals(e));
        final Runnable runSection = () -> {
            if (localVars != null) Variables.setLocalVariables(event, localVars);
            TriggerItem.walk(trigger, event);
            if (shouldWait) {
                Runnable continuation = () -> {
                    Object _localVars = SkriptReflection.copyLocals(SkriptReflection.getLocals(e));
                    Variables.setLocalVariables(e, _localVars);
                    TriggerItem.walk(getNext(), e);
                };
                
                runTask(continuation, e.isAsynchronous() ? Type.ASYNC : Type.SYNC);
              }
        };
        if (shouldWait)
            Delay.addDelayedEvent(e);
        runTask(runSection, type);
        return shouldWait ? null : getNext();
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }

    private void runTask(Runnable runnable, Type type) {
        switch (type) {
        case ASYNC:
            Scheduling.async(runnable);
            break;
        case SYNC:
            Scheduling.sync(runnable);
            break;
        case PARALLEL:
            runnable.run();
            break;
        default:
            break;
        }
    }
}
