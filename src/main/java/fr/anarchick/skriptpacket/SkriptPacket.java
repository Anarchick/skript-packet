package fr.anarchick.skriptpacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.config.Config;
import ch.njol.skript.events.bukkit.PreScriptLoadEvent;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.util.Version;
import fr.anarchick.skriptpacket.packets.SkriptPacketEventListener;
import fr.anarchick.skriptpacket.util.Scheduling;
import fr.anarchick.skriptpacket.util.Utils;

public class SkriptPacket extends JavaPlugin implements Listener {

    private static SkriptPacket INSTANCE;
    private static SkriptAddon ADDON;
    
    public static boolean isReflectAddon = false;
    public static final boolean old_nms = !Skript.isRunningMinecraft(1, 17);
    
    public static final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
    
    public static final Version MINIMUM_PROTOCOLLIB_VERSION = new Version(4, 6, 0);
    public static final Version PROTOCOLLIB_VERSION =
            new Version(pluginManager.getPlugin("ProtocolLib").getDescription().getVersion());
    
    public static final Version MINIMUM_SKRIPT_VERSION = new Version(2, 5, 2);
    public static Version SKRIPT_VERSION;
    
    private static final Version SKRIPT_2_6 = new Version(2, 6);
    
    // You have to fork Skript-Packet to enable this !!
    public static boolean enableDeprecated = false;
    
    @Override
    public void onEnable() {
        INSTANCE = this;
        
        PluginManager pluginManager = Bukkit.getPluginManager();
        isReflectAddon = pluginManager.isPluginEnabled("skript-reflect");
        if (isReflectAddon) Logging.info("Support of skript-reflect wrapper");
        SKRIPT_VERSION = Skript.getVersion();
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
            if (Skript.isAcceptRegistrations()) {
                ADDON = Skript.registerAddon(this);
                ADDON.loadClasses("fr.anarchick.skriptpacket", "elements");
                if (SKRIPT_VERSION.isLargerThan(SKRIPT_2_6)) {
                    ADDON.loadClasses("fr.anarchick.skriptpacket", "sections");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            pluginManager.disablePlugin(this);
            return;
        }
        
        pluginManager.registerEvents(this, this);
        
        int pluginId = 10270;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () ->
            SKRIPT_VERSION.toString()));
        metrics.addCustomChart(new Metrics.SimplePie("protocollib_version", () ->
            PROTOCOLLIB_VERSION.toString()));
        metrics.addCustomChart(new Metrics.SimplePie("skript-reflect_support", () ->
            String.valueOf(isReflectAddon)));
        
        Logging.info("is enable! Enjoy packets :D");
        checkUpdate();
    }
    
    public static SkriptPacket getInstance() {
        return INSTANCE;
    }
    
    public static SkriptAddon getAddonInstance() {
        return ADDON;
    }
    
    private static void checkUpdate() {
        Scheduling.async(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/Anarchick/skript-packet/main/src/main/resources/plugin.yml").openConnection();
                connection.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("version:")) {
                        String str = Utils.regexGroup("([\\d\\.]+)", inputLine, 1);
                        Version lastVersion = new Version(str);
                        if (lastVersion.isLargerThan(SKRIPT_VERSION))
                            Logging.warn("A new update is available ("+lastVersion+")");
                        break;
                    }
                }
                in.close();
                connection.disconnect();
            } catch (Exception ex) {}
        });
    }
    
    @EventHandler
    public void onScriptLoad(PreScriptLoadEvent e) {
        SkriptPacketEventListener.onReload(e);
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    public static boolean isCurrentEvent(String error, Class<? extends Event>... clazz) {
        boolean result;
        try {
            result = ScriptLoader.isCurrentEvent(clazz);
        } catch (Exception e) {
            result = ParserInstance.get().isCurrentEvent(clazz);
        }
        if (!result) Skript.error(error);
        return result;
    }
    
    public static Config getCurrentScript() {
        try {
            return (Config) ScriptLoader.class.getDeclaredField("currentScript").get(null);
        } catch (Exception e) {
            return  ParserInstance.get().getCurrentScript();
        }
    }
    
}
