package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public final class ReferenceBlock extends ReferenceLocation {
	private static final String FORMAT = "-?\\d+(@-?\\d+){2}(@\\d+){2}@(SOUTH|WEST|NORTH|EAST)";
	private static final String SECOND_FORMAT = "-?\\d+(@-?\\d+){2}(@\\d+){2}";
	private int typeId;
	private byte initialData;
	private Direction initialDirection;

	public ReferenceBlock(int l, int f, int u, int typeId, byte initialData, Direction initialDirection) {
		super(l, f, u);
		this.typeId = typeId;
		this.initialData = initialData;
		this.initialDirection = initialDirection;
	}

	public static ReferenceBlock fromString(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("Invalid format");
		String[] p = s.split("@");
		return new ReferenceBlock(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]), Integer.parseInt(p[3]), Byte.parseByte(p[4]), Direction.valueOf(p[5]));
	}

	public static ReferenceBlock fromString(String s, Direction d) throws IllegalArgumentException {
		if (!s.matches(SECOND_FORMAT))
			throw new IllegalArgumentException("Invalid format");
		String[] p = s.split("@");
		return new ReferenceBlock(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]), Integer.parseInt(p[3]), Byte.parseByte(p[4]), d);
	}

	public static ReferenceBlock fromBukkitBlock(Location c, Direction d, Block b) {
		return fromBukkitLocation(c, d, b.getLocation()).toReferenceBlock(b.getTypeId(), b.getData(), d);
	}

	public static ReferenceBlock fromBukkitBlock(Player p, Block b) {
		return fromBukkitBlock(p.getLocation(), Direction.get(p), b);
	}

	private byte rotate(Direction d) {
		byte data = initialData;
		for (int i = 1; i <= initialDirection.getRotations(d); i++)
			data = (byte) BlockData.rotate90(typeId, data);
		return data;
	}

	public void place(Location c, Direction d) {
		getBukkitBlock(c, d).setTypeIdAndData(typeId, rotate(d), false);
	}

	public void place(Player p) {
		place(p.getLocation(), Direction.get(p));
	}

	public int getTypeId() {
		return this.typeId;
	}

	public byte getInitialData() {
		return this.initialData;
	}

	public Direction getInitialDirection() {
		return this.initialDirection;
	}

	public String toString(boolean direction) {
		return super.toString() + "@" + typeId + "@" + initialData + (direction ? "@" + initialDirection : "");
	}

	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public ReferenceBlock clone() {
		return new ReferenceBlock(l, f, u, typeId, initialData, initialDirection);
	}
}