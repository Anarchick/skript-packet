
package fr.anarchick.skriptpacket.elements.events;


import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
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

public class EvtPacket extends SkriptEvent {

    private Mode mode = Mode.DEFAULT;
    private Literal<PacketType> packetTypeLit;
    private ListenerPriority priority;
    
    static {
        Skript.registerEvent("Packet Event - Skript-Packet", EvtPacket.class, BukkitPacketEvent.class,
                "[(sync|async)] packet event %packettype%")
        .description("Called when a packet of one of the specified types is being sent or received.")
        .examples("packet event play_server_entity_equipments:",
                "\tbroadcast \"equipment changed\"")
        .since("1.0, 2.0 (sync/async)");
        
        // event-packet
        EventValues.registerEventValue(BukkitPacketEvent.class, PacketContainer.class, BukkitPacketEvent::getPacket);
        // event-player
        EventValues.registerEventValue(BukkitPacketEvent.class, Player.class, BukkitPacketEvent::getPlayer);
        // event-world
        EventValues.registerEventValue(BukkitPacketEvent.class, World.class, (e) -> e.getPlayer().getWorld());
    }
    
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

        if (parser.expr.startsWith("async")) {
            mode = Mode.ASYNC;
        } else if (parser.expr.startsWith("sync")) {
            mode = Mode.SYNC;

            if (packetType.isAsyncForced()) {
                Skript.error("The packettype '"+PacketManager.getPacketName(packetType)+"' can't be use in SYNC");
                return false;
            }

        }

        if (!packetType.isSupported()) {
            Skript.error("The packettype '"+PacketManager.getPacketName(packetType)+"' is not supported by your server");
            return false;
        }

        final String scriptName = getScriptName();
        SkriptPacketEventListener.register(packetTypeLit.getAll(), priority, mode, scriptName);
        return true;
    }

    private String getScriptName() {
        return getParser().getCurrentScript().getConfig().getFileName();
    }

    @Override
    public boolean check(@NotNull Event event) {

        if (event instanceof BukkitPacketEvent e) {

            if (Objects.equals(packetTypeLit.getSingle(event), e.getPacketType())
                    && priority.equals(e.getPriority())
                    && mode.equals(e.getMode()) ) {
                return e.getPacket().getMeta("bypassEvent").isEmpty();
            }

        }

        return false;
    }

    @Override
    public boolean canExecuteAsynchronously() {
        return mode == Mode.ASYNC;
    }
    
    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return mode.name() + " packet event " + packetTypeLit.toString(e, debug) + " with " + priority.name() + " priority";
    }
    
}