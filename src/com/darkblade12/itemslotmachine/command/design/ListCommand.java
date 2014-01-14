package com.darkblade12.itemslotmachine.command.design;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;

@CommandDetails(name = "list", permission = "ItemSlotMachine.design.list")
public final class ListCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		sender.sendMessage(plugin.messageManager.design_list());
	}
}