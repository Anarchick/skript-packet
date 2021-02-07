package fr.anarchick.skriptpacket;

import java.lang.reflect.InvocationTargetException;
import net.md_5.bungee.api.ChatColor;

public class Logging {

	private static final String name = "[Skript-Packet] ";
	
	public static void info(String msg) {
		System.out.println(ChatColor.BLUE + name + ChatColor.RESET + msg);
	}

	public static void exception(Object exceptLoc, InvocationTargetException e) {
		System.out.println(ChatColor.RED + name + e.toString());
	}

}
