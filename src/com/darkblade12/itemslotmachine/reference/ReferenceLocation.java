package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.safe.SafeLocation;

public class ReferenceLocation {
    protected int l, f, u;

    public ReferenceLocation(int l, int f, int u) {
        this.l = l;
        this.f = f;
        this.u = u;
    }

    public static ReferenceLocation fromBukkitLocation(Location c, Direction d, Location l) {
        int cX = c.getBlockX();
        int cY = c.getBlockY();
        int cZ = c.getBlockZ();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        switch (d) {
            case NORTH:
                return new ReferenceLocation(x - cX, cZ - z, y - cY);
            case EAST:
                return new ReferenceLocation(cZ - z, x - cX, y - cY);
            case SOUTH:
                return new ReferenceLocation(x - cX, z - cZ, y - cY);
            case WEST:
                return new ReferenceLocation(z - cZ, cX - x, y - cY);
        }

        return null;
    }

    public static ReferenceLocation fromBukkitLocation(Player p, Location l) {
        return fromBukkitLocation(p.getLocation(), Direction.getViewDirection(p), l);
    }

    public ReferenceLocation add(int l, int f, int u) {
        this.l += l;
        this.f += f;
        this.u += u;
        return this;
    }

    public int getL() {
        return this.l;
    }

    public int getF() {
        return this.f;
    }

    public int getU() {
        return this.u;
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