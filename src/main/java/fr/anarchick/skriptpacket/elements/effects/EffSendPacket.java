package fr.anarchick.skriptpacket.elements.effects;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.comphenix.protocol.events.PacketContainer;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import fr.anarchick.skriptpacket.packets.PacketManager;

@Name("Send Packet")
@Description("Send a packet to specific players")
@Examples({
	"function unloadChunk(x: number, z: number, players: players):",
    	"\tset {_packet} to new play_server_unload_chunk packet",
    	"\tset field 0 of packet {_packet} to {_x}",
    	"\tset field 1 of packet {_packet} to {_z}",
    	"\tsend {_players::*} packet {_packet}"
})
@Since("1.0")

public class EffSendPacket extends Effect{
	
	static {
		Skript.registerEffect(EffSendPacket.class, new String[] {
				"send packet[s] %packets% to %players%",
				"send %players% packet[s] %packets%"});
    }
	
    private Expression<PacketContainer> packets;
    private Expression<Player> players;

    @Override
    protected void execute(Event event) {
        for (PacketContainer packet : packets.getArray(event)) {
            PacketManager.sendPacket(packet, this, players.getArray(event));
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "send packet " + packets.getArray(event).toString() + " to " + players.getArray(event).toString();
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        packets = (Expression<PacketContainer>) expressions[i];
        players = (Expression<Player>) expressions[(i + 1) % 2];
        return true;
    }
}
