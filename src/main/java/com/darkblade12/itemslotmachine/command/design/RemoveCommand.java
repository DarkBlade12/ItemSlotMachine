package com.darkblade12.itemslotmachine.command.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

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

        DesignManager designManager = plugin.getManager(DesignManager.class);
        Design design = designManager.getDesign(name);
        if (design == null) {
            plugin.sendMessage(sender, Message.DESIGN_NOT_FOUND, name);
            return;
        }
        name = design.getName();

        try {
            designManager.unregister(design);
        } catch (IOException e) {
            plugin.logException(e, "Failed to remove design %s!", name);
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_REMOVE_FAILED, name, e.getMessage());
        }

        plugin.sendMessage(sender, Message.COMMAND_DESIGN_REMOVE_SUCCEEDED, name);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.getManager(DesignManager.class).getNames() : null;
    }
}
