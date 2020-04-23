package com.darkblade12.itemslotmachine.core.command.design;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.design.Design;

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
        if (name.equals(Design.DEFAULT_NAME)) {
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_INVERT_NO_DEFAULT);
            return;
        }

        design.invertItemFrames();
        try {
            design.saveFile(plugin.designManager.getDataDirectory());
        } catch (IOException ex) {
            plugin.logException("Failed to invert item frame order of design '" + name + "'!", ex);
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_INVERT_FAILED, name, ex.getMessage());
        }
        plugin.sendMessage(sender, Message.COMMAND_DESIGN_INVERT_SUCCEEDED, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.designManager.getDesigns().getNames() : null;
    }
}