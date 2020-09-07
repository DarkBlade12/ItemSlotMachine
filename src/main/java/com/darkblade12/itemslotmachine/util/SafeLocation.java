package com.darkblade12.itemslotmachine.util;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

public class SafeLocation implements Cloneable {
    private String worldName;
    private double x;
    private double y;
    private double z;

    private SafeLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SafeLocation fromBukkitLocation(Location location) {
        return new SafeLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static SafeLocation fromBukkitBlock(Block block) {
        return fromBukkitLocation(block.getLocation());
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

    public double distance(Location other) {
        return Math.sqrt(distanceSquared(other));
    }

    public double distance(SafeLocation other) {
        return Math.sqrt(distanceSquared(other));
    }

    public boolean equals(Location other) {
        if (!worldName.equals(other.getWorld().getName())) {
            return false;
        }
        return Double.compare(x, other.getX()) == 0 && Double.compare(y, other.getY()) == 0
                && Double.compare(z, other.getZ()) == 0;
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
        if (obj == null || !(obj instanceof SafeLocation)) {
            return false;
        }
        return equals((SafeLocation) obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() throws IllegalStateException {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("World " + worldName + " is not loaded");
        }

        return world;
    }

    public Location getBukkitLocation() {
        return new Location(getWorld(), x, y, z);
    }

    public Block getBukkitBlock() {
        return getBukkitLocation().getBlock();
    }

    @Override
    public SafeLocation clone() {
        return new SafeLocation(worldName, x, y, z);
    }
}