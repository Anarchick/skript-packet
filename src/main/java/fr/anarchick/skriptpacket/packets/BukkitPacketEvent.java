package fr.anarchick.skriptpacket.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPacketEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private final PacketEvent packetEvent;
    private final ListenerPriority priority;
    private final PacketManager.Mode mode;
    
    public BukkitPacketEvent(PacketEvent event, ListenerPriority priority, PacketManager.Mode mode, boolean async) {
        super(async);
        this.packetEvent = event;
        this.priority = priority;
        this.mode = mode;
    }

    public PacketType getPacketType() {
        return this.packetEvent.getPacketType();
    }
    
    public ListenerPriority getPriority() {
        return this.priority;
    }

    public PacketContainer getPacket() {
        return this.packetEvent.getPacket();
    }
    
    public PacketManager.Mode getMode() {
        return this.mode;
    }

    public Player getPlayer() {
        return this.packetEvent.getPlayer();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.packetEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean b) {
        this.packetEvent.setCancelled(b);
    }

}
