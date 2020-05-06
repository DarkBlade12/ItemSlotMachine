package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.util.SafeLocation;

public class ReferenceLocation {
    protected int l;
    protected int f;
    protected int u;

    public ReferenceLocation(int l, int f, int u) {
        this.l = l;
        this.f = f;
        this.u = u;
    }

    public static ReferenceLocation fromBukkitLocation(Location viewPoint, Direction viewDirection, Location location) {
        int vX = viewPoint.getBlockX();
        int vY = viewPoint.getBlockY();
        int vZ = viewPoint.getBlockZ();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        switch (viewDirection) {
            case NORTH:
                return new ReferenceLocation(vX - x, vZ - z, y - vY);
            case EAST:
                return new ReferenceLocation(vZ - z, x - vX, y - vY);
            case SOUTH:
                return new ReferenceLocation(x - vX, z - vZ, y - vY);
            case WEST:
                return new ReferenceLocation(z - vZ, vX - x, y - vY);
            default:
                return null;
        }
    }

    public static ReferenceLocation fromBukkitLocation(Player player, Location location) {
        return fromBukkitLocation(player.getLocation(), Direction.getViewDirection(player), location);
    }

    public ReferenceLocation add(int l, int f, int u) {
        return new ReferenceLocation(this.l + l, this.f + f, this.u + u);
    }

    public int getL() {
        return l;
    }

    public int getF() {
        return f;
    }

    public int getU() {
        return u;
    }

    public Location getBukkitLocation(Location viewPoint, Direction viewDirection) {
        World world = viewPoint.getWorld();
        int x = viewPoint.getBlockX();
        int y = viewPoint.getBlockY();
        int z = viewPoint.getBlockZ();

        switch (viewDirection) {
            case WEST:
                return new Location(world, x - f, y + u, z + l);
            case NORTH:
                return new Location(world, x - l, y + u, z - f);
            case EAST:
                return new Location(world, x + f, y + u, z - l);
            case SOUTH:
                return new Location(world, x + l, y + u, z + f);
            default:
                return null;
        }
    }

    public Location getBukkitLocation(Player viewer) {
        return getBukkitLocation(viewer.getLocation(), Direction.getViewDirection(viewer));
    }

    public SafeLocation getSafeLocation(Location viewPoint, Direction viewDirection) {
        return SafeLocation.fromBukkitLocation(getBukkitLocation(viewPoint, viewDirection));
    }

    public SafeLocation getSafeLocation(Player viewer) {
        return getSafeLocation(viewer.getLocation(), Direction.getViewDirection(viewer));
    }

    public Block getBukkitBlock(Location viewPoint, Direction viewDirection) {
        return getBukkitLocation(viewPoint, viewDirection).getBlock();
    }

    public Block getBukkitBlock(Player viewer) {
        return getBukkitBlock(viewer.getLocation(), Direction.getViewDirection(viewer));
    }

    @Override
    public String toString() {
        return l + "@" + f + "@" + u;
    }

    @Override
    public ReferenceLocation clone() {
        return new ReferenceLocation(l, f, u);
    }
}