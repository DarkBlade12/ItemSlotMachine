package com.darkblade12.itemslotmachine.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class SafeLocation implements Cloneable {
    private String worldName;
    private double x, y, z;

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

    public double distanceSquared(Location location) {
        return getBukkitLocation().distanceSquared(location);
    }

    public double distanceSquared(SafeLocation location) {
        return distanceSquared(location.getBukkitLocation());
    }

    public double distance(Location location) {
        return Math.sqrt(distanceSquared(location));
    }

    public double distance(SafeLocation location) {
        return distance(location.getBukkitLocation());
    }

    public static boolean noDistance(Location l1, Location l2) {
        return l1 != null && l2 != null && l1.getWorld().getName().equals(l2.getWorld().getName()) && l1.distanceSquared(l2) == 0;
    }

    public boolean noDistance(Location location) {
        return worldName.equals(location.getWorld().getName()) && distance(location) == 0;
    }

    public boolean noDistance(SafeLocation location) {
        return noDistance(location.getBukkitLocation());
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