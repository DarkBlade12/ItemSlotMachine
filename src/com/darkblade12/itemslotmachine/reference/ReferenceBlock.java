package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;

public class ReferenceBlock extends ReferenceLocation {
    private String initialData;
    private Direction initialDirection;

    public ReferenceBlock(int l, int f, int u, String initialData, Direction initialDirection) {
        super(l, f, u);
        this.initialData = initialData;
        this.initialDirection = initialDirection;
    }

    public ReferenceBlock(int l, int f, int u, BlockData initialData, Direction initialDirection) {
        this(l, f, u, initialData.getAsString(), initialDirection);
    }

    public ReferenceBlock(ReferenceLocation location, BlockData initialData, Direction initialDirection) {
        this(location.l, location.f, location.u, initialData, initialDirection);
    }

    public static ReferenceBlock fromBukkitBlock(Location viewPoint, Direction viewDirection, Block block) {
        ReferenceLocation location = fromBukkitLocation(viewPoint, viewDirection, block.getLocation());

        return new ReferenceBlock(location, block.getBlockData(), viewDirection);
    }

    public static ReferenceBlock fromBukkitBlock(Player viewer, Block block) {
        return fromBukkitBlock(viewer.getLocation(), Direction.getViewDirection(viewer), block);
    }

    private BlockData rotate(Direction viewDirection) {
        BlockData data = Bukkit.createBlockData(initialData);

        if (!(data instanceof Directional)) {
            return data;
        }

        Directional directional = (Directional) data;
        BlockFace newFacing = Direction.rotate(directional.getFacing(), initialDirection, viewDirection);
        directional.setFacing(newFacing);

        return directional;
    }

    public void place(Location viewPoint, Direction viewDirection) {
        getBukkitBlock(viewPoint, viewDirection).setBlockData(rotate(viewDirection));
    }

    public void place(Player viewer) {
        place(viewer.getLocation(), Direction.getViewDirection(viewer));
    }

    @Override
    public ReferenceBlock clone() {
        return new ReferenceBlock(l, f, u, initialData, initialDirection);
    }

    public String getInitialData() {
        return initialData;
    }

    public Direction getInitialDirection() {
        return initialDirection;
    }
}
