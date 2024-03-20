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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
    
    private Expression<PacketContainer> packetsExpr;
    private Expression<Player> playersExpr;
    private boolean bypassEvent = false;
    
    static {
        Skript.registerEffect(EffReceivePacket.class, "rec(ei|ie)ve packet[s] %packets% from %players% [without calling event]",
                "rec(ei|ie)ve %players% packet[s] %packets% [without calling event]");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expr, int i, @NotNull Kleenean kleenean, ParseResult parseResult) {
        packetsExpr = (Expression<PacketContainer>) expr[i];
        playersExpr = (Expression<Player>) expr[(i + 1) % 2];
        bypassEvent = parseResult.expr.toLowerCase().endsWith(" without calling event");
        return true;
    }

    @Override
    protected void execute(@NotNull Event e) {
        final PacketContainer[] packets = packetsExpr.getAll(e); // Size of 1 in most of the cases

        for (PacketContainer packet : packets) {

            if (bypassEvent) {
                packet.setMeta("bypassEvent", true);
            }

            PacketManager.receivePacket(packet, playersExpr.getAll(e));
        }

    }

    @Override
    public @NotNull String toString(Event e, boolean b) {
        return "receive packet " + Arrays.toString(packetsExpr.getAll(e)) + " from " + Arrays.toString(playersExpr.getAll(e));
    }
    
}
