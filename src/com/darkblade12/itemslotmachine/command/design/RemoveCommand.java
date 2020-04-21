package com.darkblade12.itemslotmachine.command.design;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.design.Design;

@CommandDetails(name = "remove", params = "<name>", permission = "ItemSlotMachine.design.remove")
public final class RemoveCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        if (params[0].equalsIgnoreCase(Design.DEFAULT_NAME)) {
            sender.sendMessage(plugin.messageManager.design_removal_failure(Design.DEFAULT_NAME));
            return;
        }

        Design design = plugin.designManager.getDesign(params[0]);
        if (design == null) {
            sender.sendMessage(plugin.messageManager.design_not_existent());
            return;
        }
        
        plugin.designManager.unregister(design);
        sender.sendMessage(plugin.messageManager.design_removal_success(design.getName()));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        return params.length == 1 ? plugin.designManager.getDesigns().getNames() : null;
    }
}