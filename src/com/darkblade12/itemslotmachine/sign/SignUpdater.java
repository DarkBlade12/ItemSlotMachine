package com.darkblade12.itemslotmachine.sign;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.util.ReflectionUtil;
import com.darkblade12.itemslotmachine.util.ReflectionUtil.DynamicPackage;

public final class SignUpdater {
	private static Constructor<?> blockPosition;
	private static Constructor<?> chatComponentText;
	private static Class<?> iChatBaseComponent;
	private static Constructor<?> packetPlayOutUpdateSign;

	static {
		try {
			Class<?> blockPositionClass = ReflectionUtil.getClass("BlockPosition", DynamicPackage.MINECRAFT_SERVER);
			blockPosition = ReflectionUtil.getConstructor(blockPositionClass, int.class, int.class, int.class);
			chatComponentText = ReflectionUtil.getConstructor(ReflectionUtil.getClass("ChatComponentText", DynamicPackage.MINECRAFT_SERVER), String.class);
			iChatBaseComponent = ReflectionUtil.getClass("IChatBaseComponent", DynamicPackage.MINECRAFT_SERVER);
			Class<?> packetPlayOutUpdateSignClass = ReflectionUtil.getClass("PacketPlayOutUpdateSign", DynamicPackage.MINECRAFT_SERVER);
			Class<?> worldClass = ReflectionUtil.getClass("World", DynamicPackage.MINECRAFT_SERVER);
			Class<?> arrayClass = Array.newInstance(iChatBaseComponent, 0).getClass();
			packetPlayOutUpdateSign = ReflectionUtil.getConstructor(packetPlayOutUpdateSignClass, worldClass, blockPositionClass, arrayClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SignUpdater() {}

	public static void updateSign(Player p, int x, int y, int z, String[] lines, int... splittable) {
		try {
			World w = p.getWorld();
			if (w.getBlockAt(x, y, z).getState() instanceof Sign) {
				Object playerConnection = ReflectionUtil.getValue("playerConnection", ReflectionUtil.invokeMethod("getHandle", p.getClass(), p));
				Object world = ReflectionUtil.invokeMethod("getHandle", w.getClass(), w);
				String[] validated = validateLines(lines, splittable);
				Object[] text = (Object[]) Array.newInstance(iChatBaseComponent, 4);
				text[0] = chatComponentText.newInstance(validated[0]);
				text[1] = chatComponentText.newInstance(validated[1]);
				text[2] = chatComponentText.newInstance(validated[2]);
				text[3] = chatComponentText.newInstance(validated[3]);
				ReflectionUtil.invokeMethod("sendPacket", playerConnection.getClass(), playerConnection, packetPlayOutUpdateSign.newInstance(world, blockPosition.newInstance(x, y, z), text));
			}
		} catch (Exception e) {
			if (Settings.isDebugModeEnabled())
				e.printStackTrace();
		}
	}

	public static void updateSign(Player p, Location l, String[] lines, int... splittable) {
		updateSign(p, l.getBlockX(), l.getBlockY(), l.getBlockZ(), lines, splittable);
	}

	public static String[] validateLines(String[] lines, int... splittable) {
		if (lines.length > 4)
			throw new IllegalArgumentException("The lines array has an invalid length");
		for (int s : splittable) {
			if (s >= 0 && s < lines.length - 1) {
				String a = lines[s];
				if (a.length() > 15) {
					String[] p = a.split(" ");
					lines[s] = p[0];
					lines[s + 1] = p[1];
				}
			}
		}
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			lines[i] = line.length() > 15 ? line.substring(0, 15) : line;
		}
		return lines;
	}
}