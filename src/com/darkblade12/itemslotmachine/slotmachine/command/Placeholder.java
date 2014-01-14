package com.darkblade12.itemslotmachine.slotmachine.command;

public final class Placeholder {
	private static final String FORMAT = "<\\w+>";
	private String name;
	private String value;

	public Placeholder(String name, String value) {
		if (!name.matches(FORMAT))
			throw new IllegalArgumentException("Invalid name format");
		this.name = name;
		this.value = value;
	}

	public String replace(String s) {
		return s.replace(name, value);
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}
}