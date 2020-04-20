package com.darkblade12.itemslotmachine.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDetails {
    String name();

    String params() default "";

    boolean executableAsConsole() default true;

    String permission() default "None";

    boolean infiniteParams() default false;
}