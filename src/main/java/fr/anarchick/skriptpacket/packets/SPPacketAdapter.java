package fr.anarchick.skriptpacket.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.util.Scheduling;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SPPacketAdapter extends PacketAdapter {

    public static final JavaPlugin PLUGIN = SkriptPacket.getInstance();
    public final ListenerPriority priority;
    public final PacketType packetType;
    public final PacketManager.Mode mode;
    private final boolean isServer, isAsync;

    public SPPacketAdapter(ListenerPriority priority, PacketType packetType, PacketManager.Mode mode) {
        super(PLUGIN, priority, packetType);
        this.priority = priority;
        this.packetType = packetType;
        this.mode = mode;
        this.isServer = packetType.isServer();
        this.isAsync = PacketManager.Mode.ASYNC.equals(mode);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType().equals(packetType) && isServer) {
            SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, isAsync));
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType().equals(packetType) && !isServer) {
            /*
            if (event.getPacket().getMeta("uuid").isEmpty()) {
                event.getPacket().setMeta("uuid", UUID.randomUUID());
            }
            */

            if (PacketManager.Mode.SYNC.equals(mode)) {
                Scheduling.sync(() -> SkriptPacket.pluginManager
                        .callEvent(new BukkitPacketEvent(event, priority, mode, isAsync)));
            } else {
                SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, isAsync));
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o == this) {
            return true;
        } else {
            return o.hashCode() == hashCode();
        }
    }

    @Override
    public int hashCode() {
        return mode.hashCode() + packetType.hashCode() + priority.hashCode();
    }

    @Override
    public String toString() {
        return String.format("SPPacketAdapter[%s;%s;%s]", packetType.name(), mode.name(), priority.name());
    }

}
