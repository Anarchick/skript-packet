package fr.anarchick.skriptpacket;

import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;

public class Logging {

    private static final String name = "[Skript-Packet] ";
    private static final Logger LOGGER = SkriptPacket.getInstance().getLogger();
    
    public static void info(String msg) {
        System.out.println(ChatColor.BLUE + name + ChatColor.RESET + msg);
    }
    
    public static void warn(String msg) {
        LOGGER.warning(msg);
    }

    public static void exception(Exception e) {
        LOGGER.severe(e.toString());
    }

}
