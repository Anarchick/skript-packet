package fr.anarchick.skriptpacket;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Logging {

    private static final String name = "&7[&bSkript-Packet&7] ";
    private static final ConsoleCommandSender LOGGER = Bukkit.getConsoleSender(); // Allow colored messages
    
    public static void info(String msg) {
        LOGGER.sendMessage(colored(name + "&7" + msg));
    }
    
    public static void warn(String msg) {
        LOGGER.sendMessage(colored(name + "&e" + msg));
    }

    public static void severe(String msg) {
        LOGGER.sendMessage(colored(name + "&c" + msg));
    }

    private static String colored(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
