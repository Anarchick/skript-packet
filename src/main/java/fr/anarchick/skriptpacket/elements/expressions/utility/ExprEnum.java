package fr.anarchick.skriptpacket.elements.expressions.utility;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftReflection;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import fr.anarchick.skriptpacket.util.Utils;

@Name("Enum")
@Description("Get an Enum from a class")
@Examples({
        "set {_enum} to enum \"GAME_INFO\" from class \"net.minecraft.server.v1_16_R3.ChatMessageType\"",
        "set {_enum} to enum \"GAME_INFO\" from nms class \"ChatMessageType\""
})
@Since("1.2")

public class ExprEnum extends SimpleExpression<Object> {

    private Expression<String> enumExpr;
    private Expression<String> classExpr;
    private Expression<Object> objExpr;
    private int pattern;
    private static final String[] patterns = new String[] {
            "enum %string% from class %string%",
            "enum %string% from nms class %string%",
            "enum %string% from class of %object%"
    };
    private static final List<String> packages = new ArrayList<>();
    
    static {
        Skript.registerExpression(ExprEnum.class, Object.class, ExpressionType.SIMPLE, patterns);
        try {
            // MinecraftVersion.class is in root of both before/after 1.17
            Class<?> klass = MinecraftReflection.getMinecraftClass("MinecraftVersion");
            // If you use PAPER , it return the file from '/cache/patched_1.17.1.jar'
            URL url = klass.getProtectionDomain().getCodeSource().getLocation();
            File file = Paths.get(url.toURI()).toFile();
            JarFile jar = new JarFile(file);
            jar.stream()
                .map(ZipEntry::getName)
                // If you use PAPER, a lot of more packages are include
                .filter(name -> (name.startsWith("net/minecraft") && name.endsWith(".class")))
                .map(name -> name
                    .substring(0, name.lastIndexOf('/'))
                    .replace('/', '.')
                    .replace("net.minecraft.", "")
                )
                .distinct()
                .forEach(packages::add);
            jar.close();
        } catch (Exception ex) {}
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        enumExpr = (Expression<String>) exprs[0];
        if (pattern <= 1) {
            classExpr = (Expression<String>) exprs[1];
        } else {
            objExpr = (Expression<Object>) exprs[1];
        }
        return true;
    }
    
    @Override
    @Nullable
    protected Object[] get(Event e) {
        String original = "";
        if (pattern <= 1) {
            original = classExpr.getSingle(e);
            if (original == null || original.isEmpty()) return new Object[0];
        }

        Class<?> clazz = null;
        switch (pattern) {
            case 0:
                try {
                    clazz = Class.forName(original);
                } catch (ClassNotFoundException e1) {
                    Skript.error("Failed to find class '" + original + "'");
                }
                break;
            case 1:
                String className = original.replaceFirst("net\\.minecraft", "");
                String aliases[] = className.split("\\.");
                String alias = aliases[aliases.length -1];
                try {
                    clazz = MinecraftReflection.getMinecraftClass(className, alias);
                } catch (RuntimeException e1) {
                    for (String str : packages) {
                        String path = str+"."+alias;
                        try {
                            clazz = MinecraftReflection.getMinecraftClass(path);
                            if (clazz != null) {
                                Skript.error("You should replace '"+original+"' by '"+path+"' for better performances");
                                break;
                            }
                        } catch (RuntimeException e2) {}   
                    }
                }
                if (clazz == null) Skript.error("Failed to find NMS class '" + className + "'");
                break;
            case 2:
                Object obj = objExpr.getSingle(e);
                if (obj != null) clazz = obj.getClass();
                break;
            default:
                break;
        }
        String enumStr = enumExpr.getSingle(e);
        return CollectionUtils.array(Utils.getEnum(clazz, enumStr, true));
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
    }
    
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }
    
}
