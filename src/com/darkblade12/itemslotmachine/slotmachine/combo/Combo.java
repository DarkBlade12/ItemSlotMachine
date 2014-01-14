package com.darkblade12.itemslotmachine.slotmachine.combo;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.item.ItemFactory;

public abstract class Combo {
	private ItemStack[] icons;
	protected Action action;

	public Combo(ItemStack[] icons, Action action) {
		if (icons.length != 3)
			throw new IllegalArgumentException("The icons array has an invalid length (not 3)");
		this.icons = icons;
		this.action = action;
	}

	public ItemStack[] getIcons() {
		return this.icons;
	}

	public boolean isActivated(ItemStack... display) {
		for (int i = 0; i < 3; i++)
			if (!display[i].isSimilar(icons[i]) && icons[i].getType() != Material.AIR)
				return false;
		return true;
	}

	public boolean hasHighPriority() {
		for (ItemStack i : icons)
			if (i.getType() == Material.AIR)
				return false;
		return true;
	}

	public Action getAction() {
		return this.action;
	}

	@Override
	public String toString() {
		return ItemFactory.toString(icons[0], false) + "@" + ItemFactory.toString(icons[1], false) + "@" + ItemFactory.toString(icons[2], false) + "#" + action.name();
	}
}