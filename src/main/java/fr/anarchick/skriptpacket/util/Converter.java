package fr.anarchick.skriptpacket.util;

import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.btk5h.skriptmirror.ObjectWrapper;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.utility.MinecraftReflection;

import fr.anarchick.skriptpacket.SkriptPacket;

public class Converter {
	
	private final static Constructor<?> blockPositionConstructor;
	
	static {
		try {
			blockPositionConstructor = MinecraftReflection.getBlockPositionClass()
				.getConstructor(int.class, int.class, int.class);
		} catch (Exception e) {
			throw new RuntimeException("Cannot find block position constructor.", e);
		}
	}
	
	public static Object auto(boolean isArray, Object[] array) {
		if (array == null) return null;
		BukkitUnwrapper unwrapper = new BukkitUnwrapper();
		if (array instanceof Player[]) {
			return unwrapper.unwrapItem(((Player[]) array)[0]);
		} else if (array instanceof World[]) {
			return unwrapper.unwrapItem(((World[]) array)[0]);
		} else if (array instanceof ItemStack[]) {
			return unwrapper.unwrapItem(((ItemStack[]) array)[0]);
		} else {
			if (SkriptPacket.isReflectAddon) {
				return (isArray) ? array : ObjectWrapper.unwrapIfNecessary(array[0]);
			} else {
				return (isArray) ? array : array[0]; // default
			}
		}
	}
	
	public static Object toObject(Object o) {
		return (o == null) ? null : o;
	}
	
	public static Character toObject(char c) {
		return Character.valueOf(c);
	}
	
	public static Byte toObject(byte b) {
		return Byte.valueOf(b);
	}
	
	public static Short toObject(short s) {
		return Short.valueOf(s);
	}
	
	public static Integer toObject(int i) {
		return Integer.valueOf(i);
	}
	
	public static Long toObject(long l) {
		return Long.valueOf(l);
	}
	
	public static Float toObject(float f) {
		return Float.valueOf(f);
	}
	
	public static Double toObject(double d) {
		return Double.valueOf(d);
	}
	
	public static Boolean toObject(boolean b) {
		return Boolean.valueOf(b);
	}
	
	public static Object toNMSBlockPosition(Location loc) {
		if (loc == null) return null;
		try {
			return blockPositionConstructor.newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		} catch (Exception e) {
			throw new RuntimeException("Cannot construct BlockPosition.", e);
		}
	}
	
	public static Object toNMSBlockPosition(Vector vec) {
		if (vec == null) return null;
		try {
			return blockPositionConstructor.newInstance(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
		} catch (Exception e) {
			throw new RuntimeException("Cannot construct BlockPosition.", e);
		}
	}
}
