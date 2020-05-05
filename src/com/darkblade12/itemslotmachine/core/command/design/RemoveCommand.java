package com.darkblade12.itemslotmachine.core.command.design;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.design.Design;

public final class RemoveCommand extends CommandBase<ItemSlotMachine> {
    public RemoveCommand() {
        super("remove", Permission.COMMAND_DESIGN_REMOVE, "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String name = args[0];
        if (name.equalsIgnoreCase(Design.DEFAULT_NAME)) {
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_REMOVE_NO_DEFAULT);
            return;
        }

        Design design = plugin.designManager.getDesign(name);
        if (design == null) {
            plugin.sendMessage(sender, Message.DESIGN_NOT_FOUND, name);
            return;
        }
        name = design.getName();

        try {
            plugin.designManager.unregister(design);
        } catch (IOException ex) {
            plugin.logException("Failed to remove design {1}: {0}", ex, name);
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_REMOVE_FAILED, name, ex.getMessage());
        }
        plugin.sendMessage(sender, Message.COMMAND_DESIGN_REMOVE_SUCCEEDED, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.designManager.getDesigns().getNames() : null;
    }
}