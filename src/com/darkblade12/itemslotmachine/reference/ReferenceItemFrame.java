package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.util.ReflectionUtil;
import com.darkblade12.itemslotmachine.util.ReflectionUtil.DynamicPackage;

public final class ReferenceItemFrame extends ReferenceLocation {
	private static final String FORMAT = "-?\\d+(@-?\\d+){2}@(SOUTH|WEST|NORTH|EAST)@(SOUTH|WEST|NORTH|EAST)";
	private static final String SECOND_FORMAT = "-?\\d+(@-?\\d+){2}@(SOUTH|WEST|NORTH|EAST)";
	private Direction initialFacing;
	private Direction initialDirection;

	public ReferenceItemFrame(int l, int f, int u, Direction initialFacing, Direction initialDirection) {
		super(l, f, u);
		this.initialFacing = initialFacing;
		this.initialDirection = initialDirection;
	}

	public static ReferenceItemFrame fromString(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("Invalid format");
		String[] p = s.split("@");
		return new ReferenceItemFrame(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]), Direction.valueOf(p[3]), Direction.valueOf(p[4]));
	}

	public static ReferenceItemFrame fromString(String s, Direction d) throws IllegalArgumentException {
		if (!s.matches(SECOND_FORMAT))
			throw new IllegalArgumentException("Invalid format");
		String[] p = s.split("@");
		return new ReferenceItemFrame(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]), Direction.valueOf(p[3]), d);
	}

	public static ReferenceItemFrame fromBukkitItemFrame(Location c, Direction d, ItemFrame i) {
		return fromBukkitLocation(c, d, i.getLocation()).toReferenceItemFrame(Direction.fromBlockFace(i.getFacing()), d);
	}

	public static ReferenceItemFrame fromBukkitItemFrame(Player p, ItemFrame i) {
		return fromBukkitItemFrame(p.getLocation(), Direction.get(p), i);
	}

	private Direction rotate(Direction d) {
		Direction facing = initialFacing;
		for (int i = 1; i <= initialDirection.getRotations(d); i++)
			facing = facing.getNextDirection();
		return facing;
	}

	public void place(Location c, Direction d) {
		Block b = getAttachedBlock(c, d);
		World w = b.getWorld();
		try {
			Object world = ReflectionUtil.invokeMethod("getHandle", w.getClass(), w);
			ReflectionUtil.invokeMethod("addEntity", world.getClass(), world,
					ReflectionUtil.newInstance("EntityItemFrame", DynamicPackage.MINECRAFT_SERVER, world, b.getX(), b.getY(), b.getZ(), rotate(d).getRotation()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void place(Player p) {
		place(p.getLocation(), Direction.get(p));
	}

	public Direction getInitialFacing() {
		return this.initialFacing;
	}

	public Direction getInitialDirection() {
		return this.initialDirection;
	}

	private Block getAttachedBlock(Location c, Direction d) {
		return getBukkitBlock(c, d).getRelative(rotate(d).getOppositeDirection().toBlockFace());
	}

	public ItemFrame getBukkitItemFrame(Location c, Direction d) {
		return ItemFrameFinder.find(getBukkitLocation(c, d));
	}

	public ItemFrame getBukkitItemFrame(Player p) {
		return getBukkitItemFrame(p.getLocation(), Direction.get(p));
	}

	public String toString(boolean direction) {
		return super.toString() + "@" + initialFacing.name() + (direction ? "@" + initialDirection : "");
	}

	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public ReferenceItemFrame clone() {
		return new ReferenceItemFrame(l, f, u, initialFacing, initialDirection);
	}
}