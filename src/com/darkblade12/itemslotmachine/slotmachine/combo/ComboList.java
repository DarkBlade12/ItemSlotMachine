package com.darkblade12.itemslotmachine.slotmachine.combo;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.slotmachine.combo.types.ItemPotCombo;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.MoneyPotCombo;

public final class ComboList<T extends Combo> extends ArrayList<T> {
	private static final long serialVersionUID = 4531025263441761532L;
	private static final String FORMAT_1 = "(\\d+|[\\w\\s]+)(-\\d+)?(@(\\d+|[\\w\\s]+)(-\\d+)?){2}#\\w+(#\\d+(\\.\\d+)?)?(;(\\d+|[\\w\\s]+)(-\\d+)?(@(\\d+|[\\w\\s]+)(-\\d+)?){2}#\\w+(#\\d+(\\.\\d+)?)?)*";
	private static final String FORMAT_2 = "(\\d+|[\\w\\s]+)(-\\d+)?(@(\\d+|[\\w\\s]+)(-\\d+)?){2}#\\w+(#(\\d+|\\w+)(-\\d+){0,2}(, (\\d+|\\w+)(-\\d+){0,2})*)?(;(\\d+|[\\w\\s]+)(-\\d+)?(@(\\d+|[\\w\\s]+)(-\\d+)?){2}#\\w+(#(\\d+|\\w+)(-\\d+){0,2}(, (\\d+|\\w+)(-\\d+){0,2})*)?)*";

	public ComboList() {
		super();
	}

	public ComboList(Collection<T> c) {
		super(c);
	}

	public static ComboList<MoneyPotCombo> fromString1(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT_1))
			throw new IllegalArgumentException("has an invalid format");
		ComboList<MoneyPotCombo> list = new ComboList<MoneyPotCombo>();
		for (String c : s.split(";"))
			list.add(MoneyPotCombo.fromString(c));
		return list;
	}

	public static ComboList<ItemPotCombo> fromString2(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT_2))
			throw new IllegalArgumentException("has an invalid format");
		ComboList<ItemPotCombo> list = new ComboList<ItemPotCombo>();
		for (String c : s.split(";"))
			list.add(ItemPotCombo.fromString(c));
		return list;
	}

	public T getActivated(ItemStack... display) {
		T a = null;
		for (T e : this)
			if (e.isActivated(display))
				if (a == null)
					a = e;
				else if (e.hasHighPriority() && !a.hasHighPriority())
					return e;
		return a;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			if (s.length() > 0)
				s.append(";");
			s.append(get(i).toString());
		}
		return s.toString();
	}
}