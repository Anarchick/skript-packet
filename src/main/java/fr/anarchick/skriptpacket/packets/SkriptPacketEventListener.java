package fr.anarchick.skriptpacket.packets;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;

import ch.njol.skript.config.Config;
import ch.njol.skript.events.bukkit.PreScriptLoadEvent;
import fr.anarchick.skriptpacket.SkriptPacket;
import fr.anarchick.skriptpacket.packets.PacketManager.Mode;

public class SkriptPacketEventListener {
    
    static class Manager {
        
        private String scriptName;
        private Mode mode;
        private PacketType packetType;
        private ListenerPriority priority;
        
        private Manager(final String scriptName, final Mode mode, final PacketType packetType, ListenerPriority priority) {
            this.scriptName = scriptName;
            this.mode = mode;
            this.packetType = packetType;
            this.priority = priority;
        }
        
        private String getScriptName() {
            return this.scriptName;
        }
        
        private Mode getMode() {
            return this.mode;
        }
        
        private PacketType getPacketType() {
            return this.packetType;
        }
        
        private ListenerPriority getPriority() {
            return this.priority;
        }
        
        private boolean equals(final Manager manager) {
            return (manager.getScriptName().equals(getScriptName()) &&
                    manager.getMode().equals(getMode()) &&
                    manager.getPacketType().equals(getPacketType()) &&
                    manager.getPriority().equals(getPriority()));
        }
    }
    
    private static final List<Manager> listeners = new ArrayList<>();
    private static final List<Manager> simpleListeners = new ArrayList<>();
    
    public static void addPacketTypes(PacketType[] packetTypes, ListenerPriority priority, Mode mode, String scriptName) {
        for (PacketType packetType : packetTypes) {
            Manager manager = new Manager(scriptName, mode, packetType, priority);
            add(listeners, manager);
        }
        update();
    }
    
    public static void update() {
        simpleListeners.clear();
        // Create simpleListeners
        for (Manager listener : listeners) {
            Mode mode = listener.getMode();
            PacketType packetType = listener.getPacketType();
            ListenerPriority priority = listener.getPriority();
            Manager manager = new Manager("", mode, packetType, priority);
            add(simpleListeners, manager);
        }
        
        for (Manager listener : simpleListeners) {
            Mode mode = listener.getMode();
            PacketType packetType = listener.getPacketType();
            ListenerPriority priority = listener.getPriority();
            PacketManager.onPacketEvent(packetType, priority, mode, SkriptPacket.getInstance());
        }
        
    }
    
    private static boolean contains(List<Manager> list, Manager manager) {
        for (Manager listener : list) {
            if (manager.equals(listener)) return true;
        }
        return false;
    }
    
    private static boolean add(List<Manager> list, Manager manager) {
        return !contains(list, manager) ? list.add(manager) : false;
    }
    
    public static void onReload(PreScriptLoadEvent e) {
        PacketManager.removeListeners(SkriptPacket.getInstance());
        List<Manager> removes = new ArrayList<>();
        for (Config config : e.getScripts()) {
            String scriptName = config.getFileName();
            for (Manager listener : listeners) {
                if (listener.getScriptName().equals(scriptName)) removes.add(listener);
            }
        }
        listeners.removeAll(removes);
    }
    
}
