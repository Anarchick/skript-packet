package fr.anarchick.skriptpacket;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Version;

public class SkriptPacket extends JavaPlugin {

	private static SkriptPacket instance;
	private static SkriptAddon addon;
	
	public static boolean isReflectAddon = false;
	
	public static final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
	
	public static final Version MINIMUM_PROTOCOLLIB_VERSION = new Version(4, 4);
	public static final Version PROTOCOLLIB_VERSION =
            new Version(pluginManager.getPlugin("ProtocolLib").getDescription().getVersion());
	
	@Override
	public void onEnable() {
		if(instance != null)
            throw new IllegalStateException("Plugin initialized twice.");
		instance = this;
		addon = Skript.registerAddon(this);
		
		PluginManager pluginManager = Bukkit.getPluginManager();
		isReflectAddon = pluginManager.isPluginEnabled("skript-reflect");
		if (isReflectAddon) Logging.info("Support of skript-reflect wrapper");
		
		if (PROTOCOLLIB_VERSION.isSmallerThan(MINIMUM_PROTOCOLLIB_VERSION)) {
            Logging.info("Your version of ProtocolLib is " + PROTOCOLLIB_VERSION);
            Logging.info("Skript-Packet requires that you run at least version 4.4 of ProtocolLib");
        }
		
		try {
			addon.loadClasses("fr.anarchick.skriptpacket", "elements");
		} catch (IOException e) {
			e.printStackTrace();
			pluginManager.disablePlugin(this);
            return;
		}
		
		Logging.info("is enable!");
	}
	
	static public SkriptPacket getInstance( ) {
		return instance;
	}
	
	static public SkriptAddon getAddonInstance() {
		return addon;
	}
	
}
