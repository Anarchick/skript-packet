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

@Name("Receive Packet")
@Description("Makes the server simulate receiving the specified packet(s) from the specified player(s)")
@Examples({
    "function serverSideSlot(players: players, slot: number):",
        "\tset {_packet} to new play_client_held_item_slot packet",
        "\tset field 0 of packet {_packet} to {_slot}",
        "\treceive {_players::*} packet {_packet}"
})
@Since("1.1")

public class EffReceivePacket extends Effect{
    
    private Expression<PacketContainer> packets;
    private Expression<Player> players;
    private boolean bypassEvent = false;
    
    static {
        Skript.registerEffect(EffReceivePacket.class, new String[] {
                "rec(ei|ie)ve packet[s] %packets% from %players% [without calling event]",
                "rec(ei|ie)ve %players% packet[s] %packets% [without calling event]"
                });
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
            PacketManager.receivePacket(packet, players.getAll(e));
        }
    }

    @Override
    public String toString(Event e, boolean b) {
        return "receive packet " + packets.getAll(e) + " from " + players.getAll(e);
    }
    
}
