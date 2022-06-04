package fr.anarchick.skriptpacket.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.util.Scheduling;
import org.bukkit.plugin.java.JavaPlugin;

public class SPPacketAdapter extends PacketAdapter {

    private static final JavaPlugin PLUGIN = SkriptPacket.getInstance();
    private final ListenerPriority priority;
    private final PacketType packetType;
    private final PacketManager.Mode mode;
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
        if (event.getPacketType().equals(packetType) && isServer)
            SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, isAsync));
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType().equals(packetType) && !isServer)
            if (PacketManager.Mode.SYNC.equals(mode)) {
                Scheduling.sync(() -> SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, isAsync)));
            } else {
                SkriptPacket.pluginManager.callEvent(new BukkitPacketEvent(event, priority, mode, isAsync));
            }
    }
}
