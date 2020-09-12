package com.darkblade12.itemslotmachine.command.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

public final class InvertCommand extends CommandBase<ItemSlotMachine> {
    public InvertCommand() {
        super("invert", Permission.COMMAND_DESIGN_INVERT, "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String name = args[0];
        Design design = plugin.designManager.getDesign(name);
        if (design == null) {
            plugin.sendMessage(sender, Message.DESIGN_NOT_FOUND, name);
            return;
        }
        name = design.getName();

        if (design.isDefault()) {
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_INVERT_NO_DEFAULT);
            return;
        }

        design.invertItemFrames();
        try {
            design.saveFile(plugin.designManager.getDataDirectory());
        } catch (IOException ex) {
            plugin.logException("Failed to invert item frame order of design {1}: {0}", ex, name);
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_INVERT_FAILED, name, ex.getMessage());
        }

        plugin.sendMessage(sender, Message.COMMAND_DESIGN_INVERT_SUCCEEDED, name);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.designManager.getNames() : null;
    }
}
