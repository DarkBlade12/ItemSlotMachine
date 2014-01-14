package com.darkblade12.itemslotmachine.slotmachine.combo.types;

import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.item.ItemFactory;
import com.darkblade12.itemslotmachine.item.ItemList;
import com.darkblade12.itemslotmachine.slotmachine.combo.Action;
import com.darkblade12.itemslotmachine.slotmachine.combo.Combo;

public final class ItemPotCombo extends Combo {
	private static final String FORMAT = "(\\d+|[\\w\\s]+)(-\\d+)?(@(\\d+|[\\w\\s]+)(-\\d+)?){2}#\\w+(#(\\d+|\\w+)(-\\d+){0,2}(, (\\d+|\\w+)(-\\d+){0,2})*)?";
	private ItemList items;

	public ItemPotCombo(ItemStack[] icons, Action action, ItemList items) {
		super(icons, action);
		this.items = items;
	}

	public static ItemPotCombo fromString(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("has an invalid format");
		String[] p = s.split("#");
		String[] i = p[0].split("@");
		if (i.length != 3)
			throw new IllegalArgumentException("contains an invalid format");
		ItemStack[] icons = new ItemStack[] { ItemFactory.fromString(i[0], false), ItemFactory.fromString(i[1], false), ItemFactory.fromString(i[2], false) };
		Action action = Action.fromName(p[1]);
		if (action == null || !action.isApplicable(ItemPotCombo.class))
			throw new IllegalArgumentException("contains an invalid action name");
		ItemList items = null;
		if (action.requiresInput()) {
			try {
				items = ItemList.fromString(p[2]);
			} catch (Exception e) {
				throw new IllegalArgumentException("contains an invalid item list");
			}
		}
		return new ItemPotCombo(icons, Action.fromName(p[1]), items);
	}

	public ItemList getItems() {
		return items.clone();
	}

	@Override
	public String toString() {
		return super.toString() + (action.requiresInput() ? "#" + items.toString() : "");
	}
}