package com.darkblade12.itemslotmachine.util;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;

import java.util.Objects;

public class SafeLocation implements Cloneable {
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;

    private SafeLocation(String worldName, double x, double y, double z) {
        if (worldName == null) {
            throw new NullArgumentException("worldName");
        }

        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SafeLocation fromBukkitLocation(Location location) {
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("World of location cannot be null.");
        }

        return new SafeLocation(world.getName(), location.getX(), location.getY(), location.getZ());
    }

    private double distanceSquared(double x, double y, double z) {
        return NumberConversions.square(this.x - x) + NumberConversions.square(this.y - y) + NumberConversions.square(this.z - z);
    }

    public double distanceSquared(Location location) {
        return distanceSquared(location.getX(), location.getY(), location.getZ());
    }

    public double distanceSquared(SafeLocation other) {
        return distanceSquared(other.x, other.y, other.z);
    }

    public boolean equals(Location other) {
        World world = other.getWorld();
        if (world == null || !worldName.equals(world.getName())) {
            return false;
        }

        return Double.compare(x, other.getX()) == 0 && Double.compare(y, other.getY()) == 0 && Double.compare(z, other.getZ()) == 0;
    }

    public boolean equals(SafeLocation other) {
        if (this == other) {
            return true;
        } else if (!worldName.equals(other.worldName)) {
            return false;
        }

        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0 && Double.compare(z, other.z) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SafeLocation && equals((SafeLocation) obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z);
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("World " + worldName + " is not loaded.");
        }

        return world;
    }

    public Location toBukkitLocation() {
        return new Location(getWorld(), x, y, z);
    }

    @Override
    public SafeLocation clone() {
        try {
            return (SafeLocation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException();
        }
    }
}
