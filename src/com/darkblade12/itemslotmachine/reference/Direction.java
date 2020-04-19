package com.darkblade12.itemslotmachine.reference;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public enum Direction {
    SOUTH(0),
    WEST(1),
    NORTH(2),
    EAST(3);

    private static final Map<Integer, Direction> ORDINAL_MAP = new HashMap<Integer, Direction>();
    private static final BlockFace[] FACE_ORDER;
    private int ordinal;

    static {
        for (Direction d : values()) {
            ORDINAL_MAP.put(d.ordinal, d);
        }

        FACE_ORDER = new BlockFace[] { BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST,
                                       BlockFace.WEST_SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST,
                                       BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST,
                                       BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST,
                                       BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST };
    }

    private Direction(int ordinal) {
        this.ordinal = ordinal;
    }

    public static Direction fromOrdinal(int ordinal) {
        return ORDINAL_MAP.get(ordinal % values().length);
    }

    public static Direction fromBlockFace(BlockFace face) {
        try {
            return valueOf(face.name());
        } catch (Exception e) {
            return null;
        }
    }

    public static Direction getViewDirection(Player player) {
        float yaw = player.getLocation().getYaw();

        if (yaw < 0.0F) {
            yaw += 360.0F;
        }

        return yaw > 45 && yaw < 135 ? WEST : yaw > 135 && yaw < 225 ? NORTH : yaw > 225 && yaw < 315 ? EAST : SOUTH;
    }

    public static BlockFace rotate(BlockFace face, Direction initial, Direction target) {
        if (face == BlockFace.UP || face == BlockFace.DOWN || face == BlockFace.SELF) {
            return face;
        }

        int faceIndex = ArrayUtils.indexOf(FACE_ORDER, face);
        Direction current = initial;

        while (current != target) {
            faceIndex = (faceIndex + 4) % FACE_ORDER.length;
            current = current.next();
        }

        return FACE_ORDER[faceIndex];
    }

    public Direction next() {
        return fromOrdinal(ordinal + 1);
    }

    public Direction previous() {
        return fromOrdinal(ordinal + (values().length - 1));
    }

    public Direction opposite() {
        return fromOrdinal(ordinal + (values().length / 2));
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public BlockFace toBlockFace() {
        return BlockFace.valueOf(name());
    }
}