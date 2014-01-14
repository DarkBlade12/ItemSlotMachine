package com.darkblade12.itemslotmachine.nameable;

import java.util.Set;

public abstract interface NameGenerator {
	public abstract String generateName();

	public abstract Set<String> getNames();
}