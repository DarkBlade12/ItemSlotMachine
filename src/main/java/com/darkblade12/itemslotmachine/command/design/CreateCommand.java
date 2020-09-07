package com.darkblade12.itemslotmachine.command.design;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.design.DesignIncompleteException;
import com.darkblade12.itemslotmachine.util.Cuboid;

public final class CreateCommand extends CommandBase<ItemSlotMachine> {
    public CreateCommand() {
        super("create", false, Permission.COMMAND_DESIGN_CREATE, "[name]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (!plugin.designManager.hasValidSelection(player)) {
            plugin.sendMessage(player, Message.COMMAND_DESIGN_CREATE_INVALID_SELECTION);
            return;
        }

        String name;
        if (args.length == 1) {
            name = args[0];
            if (plugin.designManager.hasDesign(name)) {
                plugin.sendMessage(player, Message.COMMAND_DESIGN_CREATE_ALREADY_EXISTS, name);
                return;
            }
        } else {
            name = plugin.designManager.generateName();
        }

        try {
            Cuboid selection = plugin.designManager.getSelectionRegion(player);
            Design design = Design.create(player, selection, name);
            plugin.designManager.register(design);
        } catch (NullPointerException | IllegalArgumentException | DesignIncompleteException ex) {
            plugin.sendMessage(player, Message.COMMAND_DESIGN_CREATE_FAILED, name, ex.getMessage());
            return;
        }
        plugin.sendMessage(player, Message.COMMAND_DESIGN_CREATE_SUCCEEDED, name);
    }
}