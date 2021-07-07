package fr.anarchick.skriptpacket.elements.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;
import fr.anarchick.skriptpacket.packets.SkriptPacketEventListener;

public class EvtPacket extends SkriptEvent{

    private int mark;
    private Literal<PacketType> packetType;
    private ListenerPriority priority;
    
    static {
        Skript.registerEvent("Packet Event - Skript-Packet", EvtPacket.class, BukkitPacketEvent.class,
                "packet event %packettype% [with (1¦lowest|2¦low|3¦normal|4¦high|5¦highest|6¦monitor) priority]")
        .description("Called when a packet of one of the specified types is being sent or"
                + " received. You can optionally specify a priority triggers with higher"
                + " priority will be called later (so high priority will come after low"
                + " priority, and monitor priority will come last)."
                + " By default, the priority is normal.")
        .examples("packet event play_server_entity_equipments:",
                "\tbroadcast \"equipment changed\"")
        .since("1.0, 1.1 (priority)");
        // event-packet
        EventValues.registerEventValue(BukkitPacketEvent.class, PacketContainer.class, new Getter<PacketContainer, BukkitPacketEvent>() {
            @Override
            public PacketContainer get(final BukkitPacketEvent e) {
                return e.getPacket();
            }
        }, 0);
        // event-player
        EventValues.registerEventValue(BukkitPacketEvent.class, Player.class, new Getter<Player, BukkitPacketEvent>() {
            @Override
            public Player get(final BukkitPacketEvent e) {
                return e.getPlayer();
            }
        }, 0);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Literal<?>[] literal, int matchedPattern, ParseResult parser) {
        mark = parser.mark;
        packetType = (Literal<PacketType>) literal[0];
        switch (mark) {
            case 1:
                priority = ListenerPriority.LOWEST;
                break;
            case 2:
                priority = ListenerPriority.LOW;
                break;
            case 3:
                priority = ListenerPriority.NORMAL;
                break;
            case 4:
                priority = ListenerPriority.HIGH;
                break;
            case 5:
                priority = ListenerPriority.HIGHEST;
                break;
            case 6:
                priority = ListenerPriority.MONITOR;
                break;
            default:
                priority = ListenerPriority.NORMAL;
        }
        
        SkriptPacketEventListener.addPacketTypes(packetType.getAll(), priority);
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof BukkitPacketEvent) {
            BukkitPacketEvent e = (BukkitPacketEvent) event;
            if ( packetType.getSingle(event).equals(e.getPacketType()) && priority.equals(e.getPriority()) ) {
                PacketContainer packet = e.getPacket();
                return !packet.getMeta("bypassEvent").isPresent();
            }
        }
        return false;
    }
    
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "packet event " + packetType.toString(e, debug);
    }
    
}
