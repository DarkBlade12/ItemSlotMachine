package com.darkblade12.itemslotmachine.command.design;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;

@CommandDetails(name = "reload", permission = "ItemSlotMachine.design.reload")
public final class ReloadCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		plugin.designManager.onReload();
		sender.sendMessage(plugin.messageManager.design_reload());
	}
}