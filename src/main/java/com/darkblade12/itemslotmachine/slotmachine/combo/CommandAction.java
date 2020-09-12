package com.darkblade12.itemslotmachine.slotmachine.combo;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.plugin.replacer.Replacer;

public class CommandAction extends Action {
    private String command;

    public CommandAction(ActionType type, String command) {
        super(type);
        this.command = command;
    }

    public void executeCommand(CommandSender sender, Replacer replacer) {
        String preparedCmd = replacer == null ? command : replacer.replaceAll(command);
        Bukkit.dispatchCommand(sender, preparedCmd);
    }

    public void executeCommand(Replacer replacer) {
        executeCommand(Bukkit.getConsoleSender(), replacer);
    }

    public String getCommand() {
        return command;
    }
}
