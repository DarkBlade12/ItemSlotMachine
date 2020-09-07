package com.darkblade12.itemslotmachine.util;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Iterator;

public final class Cuboid implements Iterable<Block> {
    private final String worldName;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    public Cuboid(Location location1, Location location2) {
        if (location1 == null) {
            throw new NullArgumentException("location1");
        } else if (location2 == null) {
            throw new NullArgumentException("location2");
        }

        World world1 = location1.getWorld();
        if (world1 == null) {
            throw new IllegalArgumentException("World of location1 cannot be null.");
        }

        World world2 = location2.getWorld();
        if (world2 == null) {
            throw new IllegalArgumentException("World of location2 cannot be null.");
        }

        worldName = world1.getName();
        if (!worldName.equals(world2.getName())) {
            throw new IllegalArgumentException("The worlds of the provided locations do not match.");
        }

        x1 = Math.min(location1.getBlockX(), location2.getBlockX());
        y1 = Math.min(location1.getBlockY(), location2.getBlockY());
        z1 = Math.min(location1.getBlockZ(), location2.getBlockZ());
        x2 = Math.max(location1.getBlockX(), location2.getBlockX());
        y2 = Math.max(location1.getBlockY(), location2.getBlockY());
        z2 = Math.max(location1.getBlockZ(), location2.getBlockZ());
    }

    public Cuboid(SafeLocation loc1, SafeLocation loc2) {
        this(loc1.toBukkitLocation(), loc2.toBukkitLocation());
    }

    public boolean isInside(Location location) {
        World world = location.getWorld();
        if (world == null || !world.getName().equals(worldName)) {
            return false;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public boolean contains(Material material) {
        if (material.isBlock()) {
            throw new IllegalArgumentException(material.name() + " is not a valid block material.");
        }

        for (Block block : this) {
            if (block.getType() == material) {
                return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        for (Block block : this) {
            if (block.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    public void setBlocks(Material material) {
        if (material.isBlock()) {
            throw new IllegalArgumentException(material.name() + " is not a valid block material.");
        }

        for (Block block : this) {
            block.setType(material);
        }
    }

    public int getSizeX() {
        return (x2 - x1) + 1;
    }

    public int getSizeY() {
        return (y2 - y1) + 1;
    }

    public int getSizeZ() {
        return (z2 - z1) + 1;
    }

    public int getVolume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    public Location getLowerNE() {
        return new Location(getWorld(), x1, y1, z1);
    }

    public Location getUpperSW() {
        return new Location(getWorld(), x2, y2, z2);
    }

    public World getWorld() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("World " + worldName + " is not loaded.");
        }

        return world;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator();
    }

    private final class CuboidIterator implements Iterator<Block> {
        private final World world;
        private final int baseX;
        private final int baseY;
        private final int baseZ;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;
        private int x;
        private int y;
        private int z;

        CuboidIterator() {
            world = Cuboid.this.getWorld();
            baseX = Cuboid.this.x1;
            baseY = Cuboid.this.y1;
            baseZ = Cuboid.this.z1;
            sizeX = Math.abs(Cuboid.this.x2 - baseX) + 1;
            sizeY = Math.abs(Cuboid.this.y2 - baseY) + 1;
            sizeZ = Math.abs(Cuboid.this.z2 - baseZ) + 1;
        }

        @Override
        public boolean hasNext() {
            return x < sizeX && y < sizeY && z < sizeZ;
        }

        @Override
        public Block next() {
            Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
            if (++x >= sizeX) {
                x = 0;
                if (++y >= sizeY) {
                    y = 0;
                    ++z;
                }
            }

            return block;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
