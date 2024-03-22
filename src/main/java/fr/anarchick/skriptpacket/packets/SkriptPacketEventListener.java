package fr.anarchick.skriptpacket.packets;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.Config;
import ch.njol.skript.events.bukkit.PreScriptLoadEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import fr.anarchick.skriptpacket.packets.PacketManager.Mode;

import java.util.*;

public class SkriptPacketEventListener {

    record Manager(String scriptName, Mode mode,
                   PacketType packetType, ListenerPriority priority) {

        @Override
        public int hashCode() {
            return mode.hashCode() + packetType.hashCode() + priority.hashCode();
        }

        @Override
        public String toString() {
            return String.format("[%s;%s;%s;%s]", scriptName, packetType.name(), mode.name(), priority.name());
        }

    }
    
    private static final Map<String, Set<Manager>> MAP = new HashMap<>();
    
    public static void register(PacketType[] packetTypes, ListenerPriority priority, Mode mode, String scriptName) {

        for (PacketType packetType : packetTypes) {
            final Manager manager = new Manager(scriptName, mode, packetType, priority);
            final Set<Manager> managers = MAP.getOrDefault(scriptName, new HashSet<>());
            managers.add(manager);
            MAP.put(scriptName, managers); // TODO Check if needed
        }

        update(scriptName);
    }

    public static List<String> getAllScriptsNames() {
        return ScriptLoader
                .getLoadedScripts()
                .stream()
                .map(script -> script.getConfig().getFileName())
                .toList();
    }

    /**
     * Register only 1 time each combo of packettype + priority + mode
     * @param currentScriptName The name of the script which is currently reloading
     */
    private static void update(final String currentScriptName) {
        final HashSet<Manager> toRegister = new HashSet<>();
        final List<String> allScriptsNames = new ArrayList<>(getAllScriptsNames());
        final Set<String> registeredScriptsNames = new HashSet<>(MAP.keySet());
        allScriptsNames.add(currentScriptName);

        for (String scriptName : registeredScriptsNames) {

            if (!allScriptsNames.contains(scriptName)) {
                MAP.remove(scriptName);
            }

        }

        for (Set<Manager> managerSet : MAP.values()) {

            for (Manager managerToAdd : managerSet) {

                boolean canAdd = true;

                for (Manager registeredManager : toRegister) {

                    if (managerToAdd.hashCode() == registeredManager.hashCode()) {
                        canAdd = false;
                        break;
                    }

                }

                if (canAdd) {
                    toRegister.add(managerToAdd);
                }

            }

        }

        PacketManager.removeListeners();

        for (Manager manager : toRegister) {
            PacketManager.onPacketEvent(manager.packetType(), manager.priority, manager.mode);
        }

    }

    /**
     * Called once time berfore reload.
     * called for /sk reload scripts or /reload confirm
     * @param e
     */
    public static void beforeReload(PreScriptLoadEvent e) {
        for (Config config : e.getScripts()) {
            final String scriptName = config.getFileName();
            MAP.remove(scriptName);
        }
    }
    
}
