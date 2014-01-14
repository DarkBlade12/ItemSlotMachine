package com.darkblade12.itemslotmachine.item;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public final class ItemFactory {
	private static final String FORMAT = "(\\d+|[\\w\\s]+)(-\\d+){0,2}";

	private ItemFactory() {}

	public static ItemStack fromString(String s, boolean amount) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("has an invalid format");
		String[] i = s.split("-");
		boolean id = true;
		Material m;
		try {
			m = Material.getMaterial(Integer.parseInt(i[0]));
		} catch (Exception e) {
			id = false;
			m = Material.matchMaterial(i[0]);
		}
		if (m == null)
			throw new IllegalArgumentException("contains an invalid item " + (id ? "id" : "name"));
		return new ItemStack(m, amount ? i.length >= 2 ? Integer.parseInt(i[1]) : 1 : 1, amount ? i.length == 3 ? Short.parseShort(i[2]) : 0 : i.length == 2 ? Short.parseShort(i[1]) : 0);
	}

	public static ItemStack fromString(String s) throws Exception {
		return fromString(s, true);
	}

	public static String toString(ItemStack i, boolean amount) {
		return i.getTypeId() + "-" + (amount ? i.getAmount() : i.getDurability()) + (amount ? "-" + i.getDurability() : "");
	}

	public static String toString(ItemStack i) {
		return toString(i, true);
	}

	public static ItemStack setName(ItemStack i, String name) {
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(name);
		i.setItemMeta(meta);
		return i;
	}

	public static ItemStack setLore(ItemStack i, List<String> lore) {
		ItemMeta meta = i.getItemMeta();
		meta.setLore(lore);
		i.setItemMeta(meta);
		return i;
	}

	public static ItemStack setLore(ItemStack i, String... lore) {
		return setLore(i, Arrays.asList(lore));
	}

	public static ItemStack setNameAndLore(ItemStack i, String name, List<String> lore) {
		return setLore(setName(i, name), lore);
	}

	public static ItemStack setNameAndLore(ItemStack i, String name, String... lore) {
		return setNameAndLore(i, name, Arrays.asList(lore));
	}
}