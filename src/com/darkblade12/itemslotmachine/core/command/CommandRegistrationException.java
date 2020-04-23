package com.darkblade12.itemslotmachine.core.command;

public class CommandRegistrationException extends Exception {
    private static final long serialVersionUID = 8484187501487250881L;

    public CommandRegistrationException(String message, String name) {
        super(message.replace("%n", name));
    }
    
    public CommandRegistrationException(String message, CommandBase<?> command) {
        this(message, command.getName());
    }
}
