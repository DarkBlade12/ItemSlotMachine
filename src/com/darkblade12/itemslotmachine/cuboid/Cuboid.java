package com.darkblade12.itemslotmachine.cuboid;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class Cuboid implements Iterable<Block> {
	protected int x1, y1, z1, x2, y2, z2;
	protected String worldName;

	public Cuboid(Location l1, Location l2) throws Exception {
		if (l1 == null || l2 == null)
			throw new NullPointerException("Location can not be null");
		else if (l1.getWorld() == null)
			throw new IllegalStateException("Can not create a Cuboid for an unloaded world");
		else if (!l1.getWorld().getName().equals(l2.getWorld().getName()))
			throw new IllegalStateException("Can not create a Cuboid between two different worlds");
		worldName = l1.getWorld().getName();
		x1 = Math.min(l1.getBlockX(), l2.getBlockX());
		y1 = Math.min(l1.getBlockY(), l2.getBlockY());
		z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
		x2 = Math.max(l1.getBlockX(), l2.getBlockX());
		y2 = Math.max(l1.getBlockY(), l2.getBlockY());
		z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
	}

	public boolean isInside(Location l) {
		if (!l.getWorld().getName().equals(worldName))
			return false;
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		if (x >= x1 && x <= x2)
			if (y >= y1 && y <= y2)
				if (z >= z1 && z <= z2)
					return true;
		return false;
	}

	public boolean contains(Material m) {
		if (m.isBlock())
			throw new IllegalArgumentException("'" + m.name() + "' is not a valid block material");
		for (Block b : this)
			if (b.getType() == m)
				return true;
		return false;
	}

	public boolean isEmpty() {
		for (Block b : this)
			if (b.getType() != Material.AIR)
				return false;
		return true;
	}

	public void setBlocks(Material m) {
		if (m.isBlock())
			throw new IllegalArgumentException("'" + m.name() + "' is not a valid block material");
		for (Block b : this)
			b.setType(m);
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
		return new Location(this.getWorld(), this.x1, this.y1, this.z1);
	}

	public Location getUpperSW() {
		return new Location(this.getWorld(), this.x2, this.y2, this.z2);
	}

	public World getWorld() {
		World w = Bukkit.getWorld(worldName);
		if (w == null)
			throw new IllegalStateException("World '" + worldName + "' is not loaded");
		return w;
	}

	@Override
	public Iterator<Block> iterator() {
		return new CuboidIterator(getWorld(), x1, y1, z1, x2, y2, z2);
	}

	private class CuboidIterator implements Iterator<Block> {
		private World w;
		private int baseX, baseY, baseZ;
		private int x, y, z;
		private int sizeX, sizeY, sizeZ;

		public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
			this.w = w;
			baseX = x1;
			baseY = y1;
			baseZ = z1;
			sizeX = Math.abs(x2 - x1) + 1;
			sizeY = Math.abs(y2 - y1) + 1;
			sizeZ = Math.abs(z2 - z1) + 1;
			x = this.y = this.z = 0;
		}

		@Override
		public boolean hasNext() {
			return x < sizeX && y < sizeY && z < sizeZ;
		}

		@Override
		public Block next() {
			Block b = this.w.getBlockAt(baseX + x, baseY + y, baseZ + z);
			if (++x >= sizeX) {
				x = 0;
				if (++y >= this.sizeY) {
					y = 0;
					++z;
				}
			}
			return b;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("This operation is not available");
		}
	}
}