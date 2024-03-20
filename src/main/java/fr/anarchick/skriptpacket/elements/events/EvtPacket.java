package fr.anarchick.skriptpacket.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Version;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;
import fr.anarchick.skriptpacket.packets.PacketManager;
import fr.anarchick.skriptpacket.packets.PacketManager.Mode;
import fr.anarchick.skriptpacket.packets.SkriptPacketEventListener;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Objects;

public class EvtPacket extends SkriptEvent {

    private Mode mode = Mode.DEFAULT;
    private Literal<PacketType> packetTypeLit;
    private ListenerPriority priority;
    
    static {
        Skript.registerEvent("Packet Event - Skript-Packet", EvtPacket.class, BukkitPacketEvent.class,
                "[(sync|async)] packet event %packettype% [with (1¦lowest|2¦low|3¦normal|4¦high|5¦highest|6¦monitor) priority]")
        .description("Called when a packet of one of the specified types is being sent or"
                + " received. You can optionally specify a priority triggers with higher"
                + " priority will be called later (so high priority will come after low"
                + " priority, and monitor priority will come last)."
                + " By default, the priority is normal.")
        .examples("packet event play_server_entity_equipments:",
                "\tbroadcast \"equipment changed\"")
        .since("1.0, 1.1 (priority), 2.0 (sync/async)");
        
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
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Literal<?>[] literal, int matchedPattern, ParseResult parser) {
        packetTypeLit = (Literal<PacketType>) literal[0];

        switch (parser.mark) {
            case 1 -> priority = ListenerPriority.LOWEST;
            case 2 -> priority = ListenerPriority.LOW;
            case 4 -> priority = ListenerPriority.HIGH;
            case 5 -> priority = ListenerPriority.HIGHEST;
            case 6 -> priority = ListenerPriority.MONITOR;
            // include case 3
            default -> priority = ListenerPriority.NORMAL;
        }

        final PacketType packetType = packetTypeLit.getSingle();

        if (parser.expr.startsWith("async")) {
            mode = Mode.ASYNC;
        } else if (parser.expr.startsWith("sync")) {
            mode = Mode.SYNC;
            
//            if (packetType.isClient()) {
//                Skript.error("The packettype '"+PacketManager.getPacketName(packetType)+"' is sent by the client so it can't be use in SYNC");
//                return false;
//            } else if (packetType.isAsyncForced()) {
//                Skript.error("The packettype '"+PacketManager.getPacketName(packetType)+"' can't be use in SYNC");
//                return false;
//            }
        }

        if (!packetType.isSupported()) {
            Skript.error("The packettype '"+PacketManager.getPacketName(packetType)+"' is not supported by your server");
            return false;
        }

        final String scriptName = getScriptName();
        SkriptPacketEventListener.addPacketTypes(packetTypeLit.getAll(), priority, mode, scriptName);
        return true;
    }

    /**
     * Since Skript 2.7.0-beta1 , getCurrentScript return Script instead of Config
     * https://github.com/SkriptLang/Skript/pull/4108
     */
    private String getScriptName() {
        if (SkriptPacket.SKRIPT_VERSION.isSmallerThan(new Version("2.6.5"))) {
            Config config;

            try {
                final Method configMethod = ParserInstance.class.getDeclaredMethod("getCurrentScript");
                config = (Config) configMethod.invoke(getParser());
                return config.getFileName();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "UNKNOWN";
        } else {
            return getParser().getCurrentScript().getConfig().getFileName();
        }
    }

    @Override
    public boolean check(@NotNull Event event) {
        if (event instanceof BukkitPacketEvent e) {

            if (Objects.equals(packetTypeLit.getSingle(event), e.getPacketType())
                    && priority.equals(e.getPriority())
                    && mode.equals(e.getMode()) ) {
                final PacketContainer packet = e.getPacket();
                return packet.getMeta("bypassEvent").isEmpty();
            }

        }
        return false;
    }
    
    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return mode.name() + " packet event " + packetTypeLit.toString(e, debug) + " with " + priority.name() + " priority";
    }
    
}
