package fr.anarchick.skriptpacket.packets;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;

import ch.njol.skript.config.Config;
import ch.njol.skript.events.bukkit.PreScriptLoadEvent;
import fr.anarchick.skriptpacket.packets.PacketManager.Mode;

public class SkriptPacketEventListener {

    record Manager(String scriptName, Mode mode,
                   PacketType packetType, ListenerPriority priority) {

        private boolean equals(final Manager manager) {
            return (manager.scriptName().equals(scriptName()) &&
                    manager.mode().equals(mode()) &&
                    manager.packetType().equals(packetType()) &&
                    manager.priority().equals(priority()));
        }
    }
    
    private static final List<Manager> LISTENERS = new ArrayList<>();
    private static final List<Manager> SIMPLE_LISTENERS = new ArrayList<>();
    
    public static void addPacketTypes(PacketType[] packetTypes, ListenerPriority priority, Mode mode, String scriptName) {
        for (PacketType packetType : packetTypes) {
            Manager manager = new Manager(scriptName, mode, packetType, priority);
            add(LISTENERS, manager);
        }
        update();
    }
    
    public static void update() {
        SIMPLE_LISTENERS.clear();
        // Create simpleListeners
        for (Manager listener : LISTENERS) {
            Mode mode = listener.mode();
            PacketType packetType = listener.packetType();
            ListenerPriority priority = listener.priority();
            Manager manager = new Manager("", mode, packetType, priority);
            add(SIMPLE_LISTENERS, manager);
        }
        
        for (Manager listener : SIMPLE_LISTENERS) {
            Mode mode = listener.mode();
            PacketType packetType = listener.packetType();
            ListenerPriority priority = listener.priority();
            PacketManager.onPacketEvent(packetType, priority, mode);
        }
    }
    
    private static boolean contains(List<Manager> list, Manager manager) {
        for (Manager listener : list) {
            if (manager.equals(listener)) return true;
        }
        return false;
    }
    
    private static boolean add(List<Manager> list, Manager manager) {
        if (contains(list, manager)) return false;
        return list.add(manager);
    }
    
    // Called once time for /sk reload scripts or /reload confirm
    public static void onReload(PreScriptLoadEvent e) {
        PacketManager.removeListeners();
        List<Manager> removes = new ArrayList<>();
        for (Config config : e.getScripts()) {
            String scriptName = config.getFileName();
            for (Manager listener : LISTENERS) {
                if (listener.scriptName().equals(scriptName))
                        removes.add(listener);
            }
        }
        LISTENERS.removeAll(removes);
        update();
    }
    
}
