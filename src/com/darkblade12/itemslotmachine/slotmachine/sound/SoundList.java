package com.darkblade12.itemslotmachine.slotmachine.sound;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SoundList extends ArrayList<SoundData> {
	private static final long serialVersionUID = 2932421195026187475L;
	private static final String FORMAT = "[A-Z_]+(-\\d+(\\.\\d+)?){2}(, [A-Z_]+(-\\d+(\\.\\d+)?){2})*";

	public SoundList() {
		super();
	}

	public SoundList(Collection<SoundData> c) {
		super(c);
	}

	public static SoundList fromString(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("has an invalid format");
		SoundList list = new SoundList();
		for (String d : s.split(", "))
			list.add(SoundData.fromString(d));
		return list;
	}

	public void play(Location l) {
		for (int i = 0; i < size(); i++)
			get(i).play(l);
	}

	public void play(Player p, Location l) {
		for (int i = 0; i < size(); i++)
			get(i).play(p, l);
	}

	public void play(Player p) {
		play(p, p.getLocation());
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			if (s.length() > 0)
				s.append(", ");
			s.append(get(i).toString());
		}
		return s.toString();
	}
}