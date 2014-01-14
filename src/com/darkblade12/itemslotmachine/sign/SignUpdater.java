package com.darkblade12.itemslotmachine.sign;

import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.util.ReflectionUtil;
import com.darkblade12.itemslotmachine.util.ReflectionUtil.DynamicPackage;

public final class SignUpdater {
	private static Constructor<?> SIGN_UPDATE_PACKET_CONSTRUCTOR;

	static {
		try {
			SIGN_UPDATE_PACKET_CONSTRUCTOR = ReflectionUtil.getConstructor(ReflectionUtil.getClass("PacketPlayOutUpdateSign", DynamicPackage.MINECRAFT_SERVER), int.class, int.class, int.class, String[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SignUpdater() {}

	public static void updateSign(Player p, int x, int y, int z, String[] lines, int... splittable) {
		try {
			if (p.getWorld().getBlockAt(x, y, z).getState() instanceof Sign) {
				Object playerConnection = ReflectionUtil.getValue("playerConnection", ReflectionUtil.invokeMethod("getHandle", p.getClass(), p));
				ReflectionUtil.invokeMethod("sendPacket", playerConnection.getClass(), playerConnection, SIGN_UPDATE_PACKET_CONSTRUCTOR.newInstance(x, y, z, validateLines(lines, splittable)));
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