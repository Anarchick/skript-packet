package fr.anarchick.skriptpacket.elements.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import fr.anarchick.skriptpacket.packets.BukkitPacketEvent;
import fr.anarchick.skriptpacket.packets.SkriptPacketEventListener;

@Name("Packet Event")
@Description("Event called when a packet is send or receive by the server")
@Examples({
	"packet event play_server_entity_equipments:",
	    "\tbroadcast \"equipment changed\""
})
@Since("1.0")

public class EvtPacket extends SkriptEvent{

	static {
	    Skript.registerEvent("Packet Event - Skript-Packet", EvtPacket.class, BukkitPacketEvent.class, "packet event %packettype%");
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
	
	private Literal<PacketType> packetType;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] literal, int matchedPattern, ParseResult parser) {
		packetType = (Literal<PacketType>) literal[0];
		SkriptPacketEventListener.addPacketTypes(packetType.getAll(), ListenerPriority.NORMAL);
		return true;
	}

	@Override
	public boolean check(Event event) {
		if (event instanceof BukkitPacketEvent) {
            return packetType.getSingle(event) == ((BukkitPacketEvent) event).getPacketType();
		}
        return false;
	}
	
	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "packet event " + packetType.toString(e, debug);
	}
	
}
