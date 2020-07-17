package com.darkblade12.itemslotmachine.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rail.Shape;
import org.bukkit.block.data.type.Wall;
import org.bukkit.block.data.type.Wall.Height;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

public class ReferenceBlock extends ReferenceLocation {
    private String data;
    private Direction initialDirection;

    public ReferenceBlock(int l, int f, int u, String data, Direction initialDirection) {
        super(l, f, u);
        this.data = data;
        this.initialDirection = initialDirection;
    }

    public ReferenceBlock(int l, int f, int u, BlockData data, Direction initialDirection) {
        this(l, f, u, data.getAsString(), initialDirection);
    }

    public ReferenceBlock(ReferenceLocation location, BlockData data, Direction initialDirection) {
        this(location.l, location.f, location.u, data, initialDirection);
    }

    public static ReferenceBlock fromBukkitBlock(Location viewPoint, Direction viewDirection, Block block) {
        ReferenceLocation location = fromBukkitLocation(viewPoint, viewDirection, block.getLocation());
        return new ReferenceBlock(location, block.getBlockData(), viewDirection);
    }

    public static ReferenceBlock fromBukkitBlock(Player viewer, Block block) {
        return fromBukkitBlock(viewer.getLocation(), Direction.getViewDirection(viewer), block);
    }

    private BlockData rotate(Direction viewDirection) {
        BlockData blockData = Bukkit.createBlockData(data);
        if (blockData instanceof Directional) {
            Directional directional = (Directional) blockData;
            BlockFace newFacing = Direction.rotate(directional.getFacing(), initialDirection, viewDirection);
            directional.setFacing(newFacing);
        } else if (blockData instanceof Rotatable) {
            Rotatable rotatable = (Rotatable) blockData;
            BlockFace newRotation = Direction.rotate(rotatable.getRotation(), initialDirection, viewDirection);
            rotatable.setRotation(newRotation);
        } else if (blockData instanceof MultipleFacing) {
            MultipleFacing multiple = (MultipleFacing) blockData;
            List<BlockFace> faces = new ArrayList<BlockFace>(multiple.getFaces());
            for (int i = 0; i < faces.size(); i++) {
                faces.set(i, Direction.rotate(faces.get(i), initialDirection, viewDirection));
            }

            for (BlockFace face : multiple.getAllowedFaces()) {
                multiple.setFace(face, faces.contains(face));
            }
        } else if (blockData instanceof Rail) {
            Rail rail = (Rail) blockData;
            Shape newShape = Direction.rotate(rail.getShape(), initialDirection, viewDirection);
            rail.setShape(newShape);
        } else if (blockData instanceof Orientable) {
            Orientable orientable = (Orientable) blockData;
            Axis newAxis = Direction.rotate(orientable.getAxis(), initialDirection, viewDirection);
            orientable.setAxis(newAxis);
        } else if (blockData instanceof Wall) {
            Wall wall = (Wall) blockData;
            BlockFace[] wallFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
            Map<BlockFace, Height> faceMap = new HashMap<>();
            for (BlockFace face : wallFaces) {
                BlockFace newFace = Direction.rotate(face, initialDirection, viewDirection);
                faceMap.put(newFace, wall.getHeight(face));
            }

            for (Entry<BlockFace, Height> entry : faceMap.entrySet()) {
                wall.setHeight(entry.getKey(), entry.getValue());
            }
        }

        return blockData;
    }

    public void place(Location viewPoint, Direction viewDirection) {
        getBukkitBlock(viewPoint, viewDirection).setBlockData(rotate(viewDirection));
    }

    public void place(Player viewer) {
        place(viewer.getLocation(), Direction.getViewDirection(viewer));
    }

    @Override
    public ReferenceBlock clone() {
        return new ReferenceBlock(l, f, u, data, initialDirection);
    }

    public String getData() {
        return data;
    }

    public Direction getInitialDirection() {
        return initialDirection;
    }
}
