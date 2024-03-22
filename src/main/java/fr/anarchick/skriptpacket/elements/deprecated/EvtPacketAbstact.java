package fr.anarchick.skriptpacket.elements.deprecated;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;
import fr.anarchick.skriptpacket.packets.PacketManager;
import fr.anarchick.skriptpacket.packets.PacketManager.Mode;
import fr.anarchick.skriptpacket.packets.SkriptPacketEventListener;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class EvtPacketAbstact extends SkriptEvent {

    private Literal<PacketType> packetTypeLit;
    private ListenerPriority priority;

    static {
        // event-packet
        EventValues.registerEventValue(BukkitPacketEvent.class, PacketContainer.class, new Getter<>() {
            @Override
            public PacketContainer get(final BukkitPacketEvent e) {
                return e.getPacket();
            }
        }, 0);
        // event-player
        EventValues.registerEventValue(BukkitPacketEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(final BukkitPacketEvent e) {
                return e.getPlayer();
            }
        }, 0);
        // event-world
        EventValues.registerEventValue(BukkitPacketEvent.class, World.class, new Getter<>() {
            @Override
            public World get(final BukkitPacketEvent e) {
                return e.getPlayer().getWorld();
            }
        }, 0);
    }

    abstract Mode getMode();

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Literal<?>[] literal, int matchedPattern, ParseResult parser) {
        packetTypeLit = (Literal<PacketType>) literal[0];
        switch (getEventPriority()) {
            case LOWEST -> priority = ListenerPriority.LOWEST;
            case LOW -> priority = ListenerPriority.LOW;
            case HIGH -> priority = ListenerPriority.HIGH;
            case HIGHEST -> priority = ListenerPriority.HIGHEST;
            case MONITOR -> priority = ListenerPriority.MONITOR;
            default -> priority = ListenerPriority.NORMAL;
        }

        final PacketType packetType = packetTypeLit.getSingle();

        if (!packetType.isSupported()) {
            Skript.error("The packettype '"+PacketManager.getPacketName(packetType)+"' is not supported by your server");
            return false;
        }

        final String scriptName = getScriptName();
        SkriptPacketEventListener.register(packetTypeLit.getAll(), priority, getMode(), scriptName);
        return true;
    }

    /**
     * Since Skript 2.7.0-beta1 , getCurrentScript return Script instead of Config
     * https://github.com/SkriptLang/Skript/pull/4108
     */
    private String getScriptName() {
        return getParser().getCurrentScript().getConfig().getFileName();
    }

    @Override
    public boolean check(@NotNull Event event) {
        if (event instanceof BukkitPacketEvent e) {

            System.out.println("mode = " + getMode());
            System.out.println("e.getMode() = " + e.getMode());

            if (Objects.equals(packetTypeLit.getSingle(event), e.getPacketType())
                    && priority.equals(e.getPriority())
                    && getMode().equals(e.getMode()) ) {
                return e.getPacket().getMeta("bypassEvent").isEmpty();
            }

        }
        return false;
    }
    
    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return getMode().name() + " packet event " + packetTypeLit.toString(e, debug) + " with " + priority.name() + " priority";
    }
    
}
