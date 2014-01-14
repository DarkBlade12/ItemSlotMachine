package com.darkblade12.itemslotmachine.command.design;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.design.Design;

@CommandDetails(name = "invert", params = "<name>", permission = "ItemSlotMachine.design.invert")
public final class InvertCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		Design d = plugin.designManager.getDesign(params[0]);
		if (d == null) {
			sender.sendMessage(plugin.messageManager.design_not_existent());
			return;
		} else if (d.getName().equals("default")) {
			sender.sendMessage(plugin.messageManager.design_not_modifiable());
			return;
		}
		d.invertItemFrames();
		sender.sendMessage(plugin.messageManager.design_inversion(d.getName()));
	}
}