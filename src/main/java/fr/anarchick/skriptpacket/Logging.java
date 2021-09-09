package fr.anarchick.skriptpacket;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class Logging {

    private static final String name = ChatColor.BLUE + "[Skript-Packet] ";
    private static final Logger LOGGER = Bukkit.getLogger(); // Allow colored name
    
    public static void info(String msg) {
        LOGGER.info(name +ChatColor.GRAY + msg);
    }
    
    public static void warn(String msg) {
        LOGGER.warning(name + ChatColor.YELLOW + msg);
    }

    public static void severe(String msg) {
        LOGGER.severe(name + ChatColor.RED + msg);
    }
    
    public static void exception(Exception e) {
        LOGGER.severe(name + ChatColor.RED + e.toString());
    }

}
