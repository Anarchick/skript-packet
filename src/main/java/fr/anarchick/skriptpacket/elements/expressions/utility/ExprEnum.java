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
import org.jetbrains.annotations.NotNull;

@Name("Enum")
@Description("Get an Enum from a class")
@Examples({
        "set {_enum} to enum \"GAME_INFO\" from class \"net.minecraft.server.v1_16_R3.ChatMessageType\"",
        "set {_enum} to enum \"GAME_INFO\" from nms class \"ChatMessageType\""
})
@Since("1.2")
//!send enum "HEAD" from nms class "world.entity.EquipmentSlot"
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
        Skript.registerExpression(ExprEnum.class, Object.class, ExpressionType.COMBINED, patterns);
        try {
            final Class<?> minecraftVersionClass = MinecraftReflection.getMinecraftClass("CrashReport");
            // If you use PAPER , it returns the file from '/cache/patched_1.17.1.jar'
            final URL url = minecraftVersionClass.getProtectionDomain().getCodeSource().getLocation();
            final File file = Paths.get(url.toURI()).toFile();
            final JarFile jar = new JarFile(file);
            jar.stream()
                .map(ZipEntry::getName)
                // If you use PAPER, a lot of more packages are included
                .filter(name -> (name.startsWith("net/minecraft") && name.endsWith(".class")))
                .map(name -> name
                    .substring(0, name.lastIndexOf('/'))
                    .replace('/', '.')
                    .replace("net.minecraft.", "")
                )
                .distinct()
                .forEach(packages::add);
            jar.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("packages = " + packages);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
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
    protected Object @NotNull [] get(@NotNull Event e) {
        String targetClassName = "";

        if (pattern <= 1) {
            targetClassName = classExpr.getSingle(e);

            if (targetClassName == null || targetClassName.isEmpty()) {
                return new Object[0];
            }

        }

        Class<?> clazz = null;

        switch (pattern) {
            case 0:
                try {
                    clazz = Class.forName(targetClassName);
                } catch (ClassNotFoundException e1) {
                    Skript.error("Failed to find class '" + targetClassName + "'");
                }
                break;
            case 1:
                final String className = targetClassName.replaceFirst("net\\.minecraft", "");
                final String[] aliases = className.split("\\.");
                final String alias = aliases[aliases.length -1];

                try {
                    clazz = MinecraftReflection.getMinecraftClass(className, alias);
                } catch (RuntimeException e1) {

                    for (String str : packages) {
                        String path = str+"."+alias;

                        try {
                            clazz = MinecraftReflection.getMinecraftClass(path);

                            if (clazz != null) {
                                Skript.error("You should replace '"+targetClassName+"' by '"+path+"' for better performances");
                                break;
                            }

                        } catch (RuntimeException ignored) {}

                    }
                }

                if (clazz == null) {
                    Skript.error("Failed to find NMS class '" + className + "'");
                }

                break;
            case 2:
                final Object obj = objExpr.getSingle(e);

                if (obj != null) {
                    clazz = obj.getClass();
                }

                break;
            default:
                break;
        }

        final String enumStr = enumExpr.getSingle(e);
        return CollectionUtils.array(Utils.getEnum(clazz, enumStr));
    }
    
    @Override
    public boolean isSingle() {
        return true;
    }
    
    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }
    
    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return patterns[pattern];
    }
    
}
