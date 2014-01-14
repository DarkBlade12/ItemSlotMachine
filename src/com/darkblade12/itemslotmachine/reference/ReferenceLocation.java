package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.safe.SafeLocation;

public class ReferenceLocation {
	private static final String FORMAT = "-?\\d+(@-?\\d+){2}";
	protected int l, f, u;

	public ReferenceLocation(int l, int f, int u) {
		this.l = l;
		this.f = f;
		this.u = u;
	}

	public static ReferenceLocation fromString(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("Invalid format");
		String[] p = s.split("@");
		return new ReferenceLocation(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
	}

	public static ReferenceLocation fromBukkitLocation(Location c, Direction d, Location l) {
		int cX = c.getBlockX();
		int cY = c.getBlockY();
		int cZ = c.getBlockZ();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		switch (d) {
			case NORTH:
				return new ReferenceLocation(x - cX, cZ - z, y - cY);
			case EAST:
				return new ReferenceLocation(cZ - z, x - cX, y - cY);
			case SOUTH:
				return new ReferenceLocation(x - cX, z - cZ, y - cY);
			case WEST:
				return new ReferenceLocation(z - cZ, cX - x, y - cY);
		}
		return null;
	}

	public static ReferenceLocation fromBukkitLocation(Player p, Location l) {
		return fromBukkitLocation(p.getLocation(), Direction.get(p), l);
	}

	public ReferenceLocation add(int l, int f, int u) {
		this.l += l;
		this.f += f;
		this.u += u;
		return this;
	}

	public int getL() {
		return this.l;
	}

	public int getF() {
		return this.f;
	}

	public int getU() {
		return this.u;
	}

	public Location getBukkitLocation(Location c, Direction d) {
		int x = c.getBlockX();
		int y = c.getBlockY();
		int z = c.getBlockZ();
		switch (d) {
			case WEST:
				return new Location(c.getWorld(), x - f, y + u, z + l);
			case NORTH:
				return new Location(c.getWorld(), x - l, y + u, z - f);
			case EAST:
				return new Location(c.getWorld(), x + f, y + u, z - l);
			case SOUTH:
				return new Location(c.getWorld(), x + l, y + u, z + f);
		}
		return null;
	}

	public Location getBukkitLocation(Player p) {
		return getBukkitLocation(p.getLocation(), Direction.get(p));
	}

	public SafeLocation getSafeLocation(Location c, Direction d) {
		return SafeLocation.fromBukkitLocation(getBukkitLocation(c, d));
	}

	public SafeLocation getSafeLocation(Player p) {
		return getSafeLocation(p.getLocation(), Direction.get(p));
	}

	public Block getBukkitBlock(Location c, Direction d) {
		return getBukkitLocation(c, d).getBlock();
	}

	public Block getBukkitBlock(Player p) {
		return getBukkitBlock(p.getLocation(), Direction.get(p));
	}

	@Override
	public String toString() {
		return l + "@" + f + "@" + u;
	}

	public ReferenceBlock toReferenceBlock(int typeId, byte initialData, Direction initialDirection) {
		return new ReferenceBlock(l, f, u, typeId, initialData, initialDirection);
	}

	public ReferenceItemFrame toReferenceItemFrame(Direction initialFacing, Direction initialDirection) {
		return new ReferenceItemFrame(l, f, u, initialFacing, initialDirection);
	}

	@Override
	public ReferenceLocation clone() {
		return new ReferenceLocation(l, f, u);
	}
}