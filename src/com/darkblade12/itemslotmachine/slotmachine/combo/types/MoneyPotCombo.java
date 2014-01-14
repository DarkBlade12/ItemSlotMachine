package com.darkblade12.itemslotmachine.slotmachine.combo.types;

import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.item.ItemFactory;
import com.darkblade12.itemslotmachine.slotmachine.combo.Action;
import com.darkblade12.itemslotmachine.slotmachine.combo.Combo;

public final class MoneyPotCombo extends Combo {
	private static final String FORMAT = "(\\d+|[\\w\\s]+)(-\\d+)?(@(\\d+|[\\w\\s]+)(-\\d+)?){2}#\\w+(#\\d+(\\.\\d+)?)?";
	private double amount;

	public MoneyPotCombo(ItemStack[] icons, Action action, double amount) {
		super(icons, action);
		this.amount = amount;
	}

	public static MoneyPotCombo fromString(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("has an invalid format");
		String[] p = s.split("#");
		String[] i = p[0].split("@");
		if (i.length != 3)
			throw new IllegalArgumentException("contains an invalid format");
		ItemStack[] icons = new ItemStack[] { ItemFactory.fromString(i[0], false), ItemFactory.fromString(i[1], false), ItemFactory.fromString(i[2], false) };
		Action action = Action.fromName(p[1]);
		if (action == null || !action.isApplicable(MoneyPotCombo.class))
			throw new IllegalArgumentException("contains an invalid action name");
		double amount = 0;
		if (action.requiresInput()) {
			try {
				amount = Double.parseDouble(p[2]);
			} catch (Exception e) {
				throw new IllegalArgumentException("contains an invalid amount value");
			}
			if (amount <= 0)
				throw new IllegalArgumentException("contains an invalid amount value (lower than/equal 0");
		}
		return new MoneyPotCombo(icons, Action.fromName(p[1]), amount);
	}

	public double getAmount() {
		return this.amount;
	}

	@Override
	public String toString() {
		return super.toString() + (action.requiresInput() ? "#" + amount : "");
	}
}