package com.darkblade12.itemslotmachine.settings;

import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class SimpleSection {
	private String path;
	private String name;

	public SimpleSection(String path) {
		this.path = path;
		String[] s = path.split("\\.");
		name = s[s.length - 1];
	}

	public SimpleSection(SimpleSection section, String name) {
		path = section.getPath() + "." + name;
		this.name = name;
	}

	public void set(Configuration c, String name, Object o) {
		c.set(path + "." + name, o);
	}

	public String getPath() {
		return this.path;
	}

	public String getName() {
		return this.name;
	}

	public String getString(Configuration c, String name) {
		return c.getString(path + "." + name);
	}

	public int getInt(Configuration c, String name) {
		return c.getInt(path + "." + name);
	}

	public boolean getBoolean(Configuration c, String name) {
		return c.getBoolean(path + "." + name);
	}

	public double getDouble(Configuration c, String name) {
		return c.getDouble(path + "." + name);
	}

	public long getLong(Configuration c, String name) {
		return c.getLong(path + "." + name);
	}

	public List<?> getList(Configuration c, String name) {
		return c.getList(path + "." + name);
	}

	public List<String> getStringList(Configuration c, String name) {
		return c.getStringList(path + "." + name);
	}

	public List<Integer> getIntegerList(Configuration c, String name) {
		return c.getIntegerList(path + "." + name);
	}

	public List<Boolean> getBooleanList(Configuration c, String name) {
		return c.getBooleanList(path + "." + name);
	}

	public List<Double> getDoubleList(Configuration c, String name) {
		return c.getDoubleList(path + "." + name);
	}

	public List<Float> getFloatList(Configuration c, String name) {
		return c.getFloatList(path + "." + name);
	}

	public List<Long> getLongList(Configuration c, String name) {
		return c.getLongList(path + "." + name);
	}

	public List<Byte> getByteList(Configuration c, String name) {
		return c.getByteList(path + "." + name);
	}

	public List<Character> getCharacterList(Configuration c, String name) {
		return c.getCharacterList(path + "." + name);
	}

	public List<Short> getShortList(Configuration c, String name) {
		return c.getShortList(path + "." + name);
	}

	public List<Map<?, ?>> getMapList(Configuration c, String name) {
		return c.getMapList(path + "." + name);
	}

	public Vector getVector(Configuration c, String name) {
		return c.getVector(path + "." + name);
	}

	public OfflinePlayer getOfflinePlayer(Configuration c, String name) {
		return c.getOfflinePlayer(path + "." + name);
	}

	public ItemStack getItemStack(Configuration c, String name) {
		return c.getItemStack(path + "." + name);
	}

	public Color getColor(Configuration c, String name) {
		return c.getColor(path + "." + name);
	}

	public ConfigurationSection getConfigurationSection(Configuration c, String name) {
		return c.getConfigurationSection(path + "." + name);
	}
}