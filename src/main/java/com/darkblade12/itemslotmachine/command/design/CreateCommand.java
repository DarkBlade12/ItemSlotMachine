package com.darkblade12.itemslotmachine.command.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.design.DesignIncompleteException;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.util.Cuboid;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CreateCommand extends CommandBase<ItemSlotMachine> {
    public CreateCommand() {
        super("create", false, Permission.COMMAND_DESIGN_CREATE, "[name]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        DesignManager designManager = plugin.getManager(DesignManager.class);
        if (!designManager.hasValidSelection(player)) {
            plugin.sendMessage(player, Message.COMMAND_DESIGN_CREATE_INVALID_SELECTION);
            return;
        }

        String name;
        if (args.length == 1) {
            name = args[0];
            if (designManager.hasDesign(name)) {
                plugin.sendMessage(player, Message.COMMAND_DESIGN_CREATE_ALREADY_EXISTS, name);
                return;
            }
        } else {
            name = designManager.generateName();
        }

        try {
            Cuboid selection = designManager.getSelectionRegion(player);
            Design design = Design.create(player, selection, name);
            designManager.register(design);
        } catch (NullPointerException | IllegalArgumentException | DesignIncompleteException ex) {
            plugin.sendMessage(player, Message.COMMAND_DESIGN_CREATE_FAILED, name, ex.getMessage());
            return;
        }
        plugin.sendMessage(player, Message.COMMAND_DESIGN_CREATE_SUCCEEDED, name);
    }
}
