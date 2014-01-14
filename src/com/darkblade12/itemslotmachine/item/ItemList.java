package com.darkblade12.itemslotmachine.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemList extends ArrayList<ItemStack> implements Cloneable {
	private static final long serialVersionUID = -4142733645445441396L;
	private static final Random RANDOM = new Random();
	private static final String FORMAT = "(\\d+|[\\w\\s]+)(-\\d+){0,2}(, (\\d+|[\\w\\s]+)(-\\d+){0,2})*";

	public ItemList() {
		super();
	}

	public ItemList(Collection<ItemStack> c) {
		super(c);
	}

	public static ItemList fromString(String s, boolean amount) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("has an invalid format");
		ItemList list = new ItemList();
		for (String i : s.split(", "))
			list.add(ItemFactory.fromString(i, amount), false);
		return list;
	}

	public static ItemList fromString(String s) throws IllegalArgumentException {
		return fromString(s, true);
	}

	public static boolean hasEnoughSpace(Player p, ItemStack i) {
		int s = 0;
		for (ItemStack is : p.getInventory().getContents())
			if (is == null)
				s += 64;
			else if (is.isSimilar(i))
				s += 64 - is.getAmount();
		return s >= i.getAmount();
	}

	public boolean add(ItemStack e, boolean stack) {
		if (stack)
			for (int i = 0; i < size(); i++) {
				ItemStack s = get(i);
				if (s.isSimilar(e)) {
					s.setAmount(s.getAmount() + e.getAmount());
					return true;
				}
			}
		return super.add(e.clone());
	}

	@Override
	public boolean add(ItemStack e) {
		return add(e, true);
	}

	@Override
	public boolean addAll(Collection<? extends ItemStack> c) {
		for (ItemStack i : c)
			add(i);
		return true;
	}

	public void removeRandom() {
		int size = size();
		if (size > 0)
			remove(RANDOM.nextInt(size));
	}

	public void removeRandom(int amount) {
		while (amount > 0) {
			removeRandom();
			amount--;
		}
	}

	public void doubleAmounts() {
		for (int i = 0; i < size(); i++) {
			ItemStack s = get(i);
			s.setAmount(s.getAmount() * 2);
			set(i, s);
		}
	}

	public void distribute(Player p) {
		Location loc = p.getLocation();
		World w = loc.getWorld();
		for (int i = 0; i < size(); i++) {
			ItemStack c = get(i).clone();
			if (hasEnoughSpace(p, c))
				p.getInventory().addItem(c);
			else
				w.dropItemNaturally(loc, c);
		}
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			if (s.length() > 0)
				s.append(", ");
			s.append(ItemFactory.toString(get(i)));
		}
		return s.toString();
	}

	@Override
	public ItemList clone() {
		return new ItemList(this);
	}
}