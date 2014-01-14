package com.darkblade12.itemslotmachine.slotmachine.command;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public final class CommandList extends ArrayList<String> implements Cloneable {
	private static final long serialVersionUID = -6972409535891307954L;
	private static final String FORMAT = ".+(;.+)*";

	public CommandList() {
		super();
	}

	public CommandList(Collection<String> c) {
		super(c);
	}

	public static CommandList fromString(String s) throws IllegalArgumentException {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("has an invalid format");
		CommandList list = new CommandList();
		for (String c : s.split(";"))
			list.add(c.startsWith("/") ? c.substring(1, c.length()) : c);
		return list;
	}

	public void execute(CommandSender sender, Placeholder... placeholders) {
		for (int i = 0; i < size(); i++) {
			String command = get(i);
			for (Placeholder p : placeholders)
				command = p.replace(command);
			Bukkit.dispatchCommand(sender, command);
		}
	}

	public void execute(Placeholder... placeholders) {
		execute(Bukkit.getConsoleSender(), placeholders);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			if (s.length() > 0)
				s.append(";");
			s.append(get(i));
		}
		return s.toString();
	}

	@Override
	public CommandList clone() {
		return new CommandList(this);
	}
}