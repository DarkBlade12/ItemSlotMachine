package com.darkblade12.itemslotmachine.slotmachine.combo;

public class CommandAction extends Action {
    private final String command;

    public CommandAction(ActionType type, String command) {
        super(type);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
