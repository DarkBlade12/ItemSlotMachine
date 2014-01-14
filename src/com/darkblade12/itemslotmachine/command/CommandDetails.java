package com.darkblade12.itemslotmachine.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDetails {
	public abstract String name();

	public abstract String params() default "";

	public abstract boolean executableAsConsole() default true;

	public abstract String permission() default "None";

	public abstract boolean infiniteParams() default false;
}