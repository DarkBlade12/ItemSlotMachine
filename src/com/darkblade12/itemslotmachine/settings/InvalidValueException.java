package com.darkblade12.itemslotmachine.settings;

public final class InvalidValueException extends Exception {
	private static final long serialVersionUID = 1256236386484655224L;

	public InvalidValueException(String setting, SimpleSection section, String description) {
		super("The value of '" + setting + "' in section '" + section.getName() + "' " + description);
	}
}