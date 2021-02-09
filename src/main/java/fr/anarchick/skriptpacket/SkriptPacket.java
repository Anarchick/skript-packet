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
	
	public static final Version MINIMUM_PROTOCOLLIB_VERSION = new Version(4, 6, 0);
	public static final Version PROTOCOLLIB_VERSION =
            new Version(pluginManager.getPlugin("ProtocolLib").getDescription().getVersion());
	
	public static final Version MINIMUM_SKRIPT_VERSION = new Version(2, 5, 2);
	public static final Version SKRIPT_VERSION =
            new Version(pluginManager.getPlugin("Skript").getDescription().getVersion());
	
	@Override
	public void onEnable() {
		instance = this;
		addon = Skript.registerAddon(this);
		
		PluginManager pluginManager = Bukkit.getPluginManager();
		isReflectAddon = pluginManager.isPluginEnabled("skript-reflect");
		if (isReflectAddon) Logging.info("Support of skript-reflect wrapper");
		
		if (SKRIPT_VERSION.isSmallerThan(MINIMUM_SKRIPT_VERSION)) {
            Logging.info("Your version of Skript is " + SKRIPT_VERSION);
            Logging.info("Skript-Packet requires that you run at least version " + MINIMUM_SKRIPT_VERSION.toString() + " of Skript");
            // Does not disable the plugin, cause some syntaxes can still works
		}
		
		if (PROTOCOLLIB_VERSION.isSmallerThan(MINIMUM_PROTOCOLLIB_VERSION)) {
            Logging.info("Your version of ProtocolLib is " + PROTOCOLLIB_VERSION);
            Logging.info("Skript-Packet requires that you run at least version " + MINIMUM_PROTOCOLLIB_VERSION.toString() + " of ProtocolLib");
            // Does not disable the plugin, cause some syntaxes can still works
		}
		
		try {
			addon.loadClasses("fr.anarchick.skriptpacket", "elements");
		} catch (IOException e) {
			e.printStackTrace();
			pluginManager.disablePlugin(this);
            return;
		}
		
		int pluginId = 10270; // Gave by bstat
		Metrics metrics = new Metrics(this, pluginId);
		metrics.addCustomChart(new Metrics.SimplePie("skript_version", () ->
			SKRIPT_VERSION.toString()));
		metrics.addCustomChart(new Metrics.SimplePie("protocollib_version", () ->
			PROTOCOLLIB_VERSION.toString()));
		metrics.addCustomChart(new Metrics.SimplePie("skript-reflect_support", () ->
			String.valueOf(isReflectAddon)));
		
		Logging.info("is enable!");
	}
	
	static public SkriptPacket getInstance( ) {
		return instance;
	}
	
	static public SkriptAddon getAddonInstance() {
		return addon;
	}
	
}
