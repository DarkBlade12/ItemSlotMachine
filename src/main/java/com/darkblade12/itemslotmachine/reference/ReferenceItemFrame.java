package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

public final class ReferenceItemFrame extends ReferenceLocation {
    private final BlockFace facing;
    private final Direction initialDirection;

    public ReferenceItemFrame(int l, int f, int u, BlockFace facing, Direction initialDirection) {
        super(l, f, u);
        this.facing = facing;
        this.initialDirection = initialDirection;
    }

    public ReferenceItemFrame(ReferenceLocation location, BlockFace facing, Direction initialDirection) {
        this(location.l, location.f, location.u, facing, initialDirection);
    }

    public static ReferenceItemFrame fromBukkitItemFrame(Location viewPoint, Direction viewDirection, ItemFrame frame) {
        ReferenceLocation location = fromBukkitLocation(viewPoint, viewDirection, frame.getLocation());
        return new ReferenceItemFrame(location, frame.getFacing(), viewDirection);
    }

    public static ReferenceItemFrame fromBukkitItemFrame(Player viewer, ItemFrame frame) {
        return fromBukkitItemFrame(viewer.getLocation(), Direction.getViewDirection(viewer), frame);
    }

    public static ItemFrame findItemFrame(Location location) {
        for (Entity entity : location.getChunk().getEntities()) {
            Location entityLocation = entity.getLocation().getBlock().getLocation();
            if (entity instanceof ItemFrame && location.equals(entityLocation)) {
                return (ItemFrame) entity;
            }
        }

        return null;
    }

    private BlockFace rotate(Direction viewDirection) {
        return Direction.rotate(facing, initialDirection, viewDirection);
    }

    public void place(Location viewPoint, Direction viewDirection) {
        Location loc = toBukkitLocation(viewPoint, viewDirection);
        World world = loc.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("World of viewPoint cannot be null.");
        }

        ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.ITEM_FRAME);
        frame.setFacingDirection(rotate(viewDirection), true);
    }

    public void place(Player viewer) {
        place(viewer.getLocation(), Direction.getViewDirection(viewer));
    }

    public ItemFrame toBukkitItemFrame(Location viewPoint, Direction viewDirection) {
        return findItemFrame(toBukkitLocation(viewPoint, viewDirection));
    }
}
