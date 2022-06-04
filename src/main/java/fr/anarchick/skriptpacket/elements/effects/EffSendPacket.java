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

import java.util.Arrays;

@Name("Send Packet")
@Description("Sends the specified packet(s) to the specified player(s).")
@Examples({
    "function unloadChunk(x: number, z: number, players: players):",
        "\tset {_packet} to new play_server_unload_chunk packet",
        "\tset field 0 of packet {_packet} to {_x}",
        "\tset field 1 of packet {_packet} to {_z}",
        "\tsend {_players::*} packet {_packet}"
})
@Since("1.0, 1.1 (without calling event)")

public class EffSendPacket extends Effect{
    
    private Expression<PacketContainer> packets;
    private Expression<Player> players;
    private boolean bypassEvent = false;
    
    static {
        Skript.registerEffect(EffSendPacket.class, "(dispatch|send) packet[s] %packets% to %players% [without calling event]",
                "(dispatch|send) %players% packet[s] %packets% [without calling event]");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expr, int i, Kleenean kleenean, ParseResult parseResult) {
        packets = (Expression<PacketContainer>) expr[i];
        players = (Expression<Player>) expr[(i + 1) % 2];
        bypassEvent = parseResult.expr.toLowerCase().endsWith(" without calling event");
        return true;
    }

    @Override
    protected void execute(Event e) {
        PacketContainer[] _packets = packets.getAll(e); // Size of 1 in most of cases
        for (PacketContainer packet : _packets) {
            if (bypassEvent) packet.setMeta("bypassEvent", true);
            PacketManager.sendPacket(packet, players.getAll(e));
        }
    }

    @Override
    public String toString(Event e, boolean b) {
        return "send packet " + Arrays.toString(packets.getAll(e)) + " to " + Arrays.toString(players.getAll(e));
    }
    
}
