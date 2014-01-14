package com.darkblade12.itemslotmachine.reference;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public enum Direction {
	SOUTH(0),
	WEST(1),
	NORTH(2),
	EAST(3);

	private static final Map<Integer, Direction> ROTATION_MAP = new HashMap<Integer, Direction>();
	private int rotation;

	static {
		for (Direction d : values())
			ROTATION_MAP.put(d.rotation, d);
	}

	private Direction(int rotation) {
		this.rotation = rotation;
	}

	public static Direction fromRotation(int i) {
		return ROTATION_MAP.get(i % 4);
	}

	public static Direction fromBlockFace(BlockFace b) {
		try {
			return valueOf(b.name());
		} catch (Exception e) {
			return null;
		}
	}

	public static Direction get(Player p) {
		float yaw = p.getLocation().getYaw();
		if (yaw < 0.0F)
			yaw += 360.0F;
		return yaw > 45 && yaw < 135 ? WEST : yaw > 135 && yaw < 225 ? NORTH : yaw > 225 && yaw < 315 ? EAST : SOUTH;
	}

	public int getRotation() {
		return this.rotation;
	}

	public Direction getOppositeDirection() {
		return fromRotation(rotation + 2);
	}

	public Direction getNextDirection() {
		return fromRotation(rotation + 1);
	}

	public int getRotations(Direction d) {
		int rotations = 0;
		Direction o = this;
		while (o != d) {
			o = o.getNextDirection();
			rotations++;
		}
		return rotations;
	}

	public BlockFace toBlockFace() {
		return BlockFace.valueOf(name());
	}
}