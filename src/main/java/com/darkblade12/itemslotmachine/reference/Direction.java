package com.darkblade12.itemslotmachine.reference;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail.Shape;
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
        for (Direction direction : values()) {
            ORDINAL_MAP.put(direction.ordinal, direction);
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
        } catch (IllegalArgumentException ex) {
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

    public static BlockFace getViewFace(Player player) {
        float pitch = player.getLocation().getPitch();
        if (pitch <= -67.5) {
            return BlockFace.UP;
        } else if (pitch >= 67.5) {
            return BlockFace.DOWN;
        }
        
        return getViewDirection(player).toBlockFace();
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

    public static Shape rotate(Shape shape, Direction initial, Direction target) {
        Shape result = shape;
        Direction current = initial;
        while (current != target) {
            result = rotate(result);
            current = current.next();
        }

        return result;
    }

    private static Shape rotate(Shape shape) {
        switch (shape) {
            case ASCENDING_EAST:
                return Shape.ASCENDING_SOUTH;
            case ASCENDING_NORTH:
                return Shape.ASCENDING_EAST;
            case ASCENDING_SOUTH:
                return Shape.ASCENDING_WEST;
            case ASCENDING_WEST:
                return Shape.ASCENDING_NORTH;
            case EAST_WEST:
                return Shape.NORTH_SOUTH;
            case NORTH_EAST:
                return Shape.SOUTH_EAST;
            case NORTH_SOUTH:
                return Shape.EAST_WEST;
            case NORTH_WEST:
                return Shape.NORTH_EAST;
            case SOUTH_EAST:
                return Shape.SOUTH_WEST;
            case SOUTH_WEST:
                return Shape.NORTH_WEST;
            default:
                return shape;
        }
    }
    
    public static Axis rotate(Axis axis, Direction initial, Direction target) {
        if(axis == Axis.Y) {
            return axis;
        }
        
        Axis result = axis;
        Direction current = initial;
        while (current != target) {
            result = result == Axis.X ? Axis.Z : Axis.X;
            current = current.next();
        }

        return result;
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