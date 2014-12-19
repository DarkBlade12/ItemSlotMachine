package com.darkblade12.itemslotmachine.safe;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class SafeLocation implements Cloneable {
	private static final String FORMAT = ".+(@-?\\d+\\.\\d+){3}";
	private String worldName;
	private double x, y, z;

	private SafeLocation(String worldName, double x, double y, double z) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static SafeLocation fromString(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("Invalid format");
		String[] p = s.split("@");
		return new SafeLocation(p[0], Double.parseDouble(p[1]), Double.parseDouble(p[2]), Double.parseDouble(p[3]));
	}

	public static SafeLocation fromBukkitLocation(Location l) {
		return new SafeLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
	}

	public static SafeLocation fromBukkitBlock(Block b) {
		return fromBukkitLocation(b.getLocation());
	}

	public double distanceSquared(Location l) {
		return getBukkitLocation().distanceSquared(l);
	}

	public double distanceSquared(SafeLocation s) {
		return distanceSquared(s.getBukkitLocation());
	}

	public double distance(Location l) {
		return Math.sqrt(distanceSquared(l));
	}

	public double distance(SafeLocation s) {
		return distance(s.getBukkitLocation());
	}

	public static boolean noDistance(Location l1, Location l2) {
		return l1 != null && l2 != null && l1.getWorld().getName().equals(l2.getWorld().getName()) && l1.distanceSquared(l2) == 0;
	}

	public boolean noDistance(Location l) {
		return worldName.equals(l.getWorld().getName()) && distance(l) == 0;
	}

	public boolean noDistance(SafeLocation s) {
		return noDistance(s.getBukkitLocation());
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public String getWorldName() {
		return this.worldName;
	}

	public World getWorld() {
		World w = Bukkit.getWorld(worldName);
		if (w == null)
			throw new IllegalStateException("World '" + worldName + "' is not loaded");
		return w;
	}

	public Location getBukkitLocation() {
		return new Location(getWorld(), x, y, z);
	}

	public Block getBukkitBlock() {
		return getBukkitLocation().getBlock();
	}

	@Override
	public String toString() {
		return worldName + "@" + x + "@" + y + "@" + z;
	}

	@Override
	public SafeLocation clone() {
		return new SafeLocation(worldName, x, y, z);
	}
}