package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
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
