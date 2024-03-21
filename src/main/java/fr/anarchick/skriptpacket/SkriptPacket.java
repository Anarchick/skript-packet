package fr.anarchick.skriptpacket;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.events.bukkit.PreScriptLoadEvent;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.util.Version;
import fr.anarchick.skriptpacket.elements.Types;
import fr.anarchick.skriptpacket.packets.SkriptPacketEventListener;
import fr.anarchick.skriptpacket.util.Scheduling;
import fr.anarchick.skriptpacket.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkriptPacket extends JavaPlugin implements Listener {

    private static SkriptPacket INSTANCE;

    public static boolean isReflectAddon = false;
    
    public static final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
    
    public static final Version MINIMUM_PROTOCOLLIB_VERSION = new Version(5, 0, 0);
    public static final Version PROTOCOLLIB_VERSION =
            new Version(pluginManager.getPlugin("ProtocolLib").getDescription().getVersion());
    
    public static final Version MINIMUM_SKRIPT_VERSION = new Version(2, 7, 0);
    public static Version SKRIPT_VERSION;
    public static Version VERSION;
    
    // You have to fork Skript-Packet to enable this !!
    public static boolean enableDeprecated = false;
    
    @Override
    public void onEnable() {
        INSTANCE = this;
        VERSION = new Version(getDescription().getVersion());

        final PluginManager pluginManager = Bukkit.getPluginManager();
        isReflectAddon = pluginManager.isPluginEnabled("skript-reflect");

        if (isReflectAddon) {
            Logging.info("Support of skript-reflect wrapper enabled");
        }

        SKRIPT_VERSION = Skript.getVersion();

        if (SKRIPT_VERSION.isSmallerThan(MINIMUM_SKRIPT_VERSION)) {
            Logging.info("Your version of Skript is " + SKRIPT_VERSION);
            Logging.info("Skript-Packet requires that you run at least version " + MINIMUM_SKRIPT_VERSION + " of Skript");
            // Does not disable the plugin, cause some syntaxes can still works
        }

        if (PROTOCOLLIB_VERSION.isSmallerThan(MINIMUM_PROTOCOLLIB_VERSION)) {
            Logging.info("Your version of ProtocolLib is " + PROTOCOLLIB_VERSION);
            Logging.info("Skript-Packet requires that you run at least version " + MINIMUM_PROTOCOLLIB_VERSION + " of ProtocolLib");
            // Does not disable the plugin, cause some syntaxes can still works
        }

        try {

            if (Skript.isAcceptRegistrations()) {
                final SkriptAddon ADDON = Skript.registerAddon(this);
                Class.forName(Types.class.getName()); // Load first
                ADDON.loadClasses("fr.anarchick.skriptpacket", "elements");
                //ADDON.loadClasses("fr.anarchick.skriptpacket", "sections");
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            pluginManager.disablePlugin(this);
            return;
        }

        pluginManager.registerEvents(this, this);

        int pluginId = 10270;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () ->
            SKRIPT_VERSION.toString()));
        metrics.addCustomChart(new Metrics.SimplePie("protocollib_version",
                () -> Utils.regexGroup("^((\\d+\\.?)+(-\\w+)?)", PROTOCOLLIB_VERSION.toString(), 1)));
        metrics.addCustomChart(new Metrics.SimplePie("skript-reflect_support", () ->
            String.valueOf(isReflectAddon)));
        
        Logging.info("is enable! Enjoy packets :D");
        checkUpdate();
    }
    
    public static SkriptPacket getInstance() {
        return INSTANCE;
    }
    
    private static void checkUpdate() {
        Scheduling.async(() -> {

            try {
                final HttpURLConnection connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/Anarchick/skript-packet/main/build.gradle").openConnection();
                connection.connect();
                final BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {

                    if (inputLine.startsWith("version = ")) {
                        String str = Utils.regexGroup("((\\d+\\.?)+)", inputLine, 1);
                        final Version lastVersion = new Version(str);

                        if (lastVersion.isLargerThan(VERSION)) {
                            Logging.warn("A new update is available ("+lastVersion+")");
                        }

                        break;
                    }
                }

                in.close();
                connection.disconnect();
            } catch (Exception ignored) {}
        });
    }
    
    @EventHandler
    public void onScriptLoad(PreScriptLoadEvent e) {
        SkriptPacketEventListener.onReload(e);
    }
    
    @SuppressWarnings({"unchecked" })
    public static boolean isCurrentEvent(Expression<?> expr, String error, Class<? extends Event>... clazz) {
        boolean result = expr.getParser().isCurrentEvent(clazz);

        if (!result) {
            Skript.error(error);
        }

        return result;
    }
    
}
