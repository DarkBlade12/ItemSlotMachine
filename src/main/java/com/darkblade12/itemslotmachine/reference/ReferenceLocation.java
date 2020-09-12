package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ReferenceLocation {
    protected final int l;
    protected final int f;
    protected final int u;

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
                throw new IllegalArgumentException("Value of viewDirection is not supported.");
        }
    }

    public ReferenceLocation add(int l, int f, int u) {
        return new ReferenceLocation(this.l + l, this.f + f, this.u + u);
    }

    public Location toBukkitLocation(Location viewPoint, Direction viewDirection) {
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

    public Block toBukkitBlock(Location viewPoint, Direction viewDirection) {
        return toBukkitLocation(viewPoint, viewDirection).getBlock();
    }
}
