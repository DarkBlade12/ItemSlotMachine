package com.darkblade12.itemslotmachine.command.design;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.design.Design;

@CommandDetails(name = "create", params = "[name]", executableAsConsole = false, permission = "ItemSlotMachine.design.create")
public final class CreateCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        Player player = (Player) sender;
        if (!plugin.designManager.hasValidSelection(player)) {
            player.sendMessage(plugin.messageManager.design_invalid_selection());
            return;
        }

        String name;
        if (params.length == 1) {
            name = params[0];
            if (plugin.designManager.hasDesign(name)) {
                player.sendMessage(plugin.messageManager.design_already_existent());
                return;
            }
        } else {
            name = plugin.designManager.generateName();
        }

        try {
            plugin.designManager.register(Design.create(player, plugin.designManager.getValidSelection(player), name));
        } catch (Exception e) {
            player.sendMessage(plugin.messageManager.design_creation_failure(e.getMessage()));
            return;
        }
        player.sendMessage(plugin.messageManager.design_creation_success(name));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        return params.length == 1 ? Arrays.asList(new String[] { "[name]" }) : null;
    }
}