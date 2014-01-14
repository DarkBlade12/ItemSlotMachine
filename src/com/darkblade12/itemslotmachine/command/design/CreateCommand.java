package com.darkblade12.itemslotmachine.command.design;

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
		Player p = (Player) sender;
		if (!plugin.designManager.hasValidSelection(p)) {
			p.sendMessage(plugin.messageManager.design_invalid_selection());
			return;
		}
		String name;
		if (params.length == 1) {
			name = params[0];
			if (plugin.designManager.hasDesign(name)) {
				p.sendMessage(plugin.messageManager.design_already_existent());
				return;
			}
		} else
			name = plugin.designManager.generateName();
		try {
			plugin.designManager.register(Design.create(p, plugin.designManager.getValidSelection(p), name));
		} catch (Exception e) {
			p.sendMessage(plugin.messageManager.design_creation_failure(e.getMessage()));
			return;
		}
		p.sendMessage(plugin.messageManager.design_creation_success(name));
	}
}