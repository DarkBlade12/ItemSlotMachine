package com.darkblade12.itemslotmachine.command.general;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.CommandHelpPage;
import com.darkblade12.itemslotmachine.command.ICommand;

@CommandDetails(name = "help", params = "[page]")
public final class HelpCommand implements ICommand {
	private CommandHelpPage helpPage;

	public HelpCommand(CommandHelpPage helpPage) {
		this.helpPage = helpPage;
	}

	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		int page = 1;
		if (params.length == 1) {
			String input = params[0];
			try {
				page = Integer.parseInt(input);
				if (!helpPage.hasPage(sender, page)) {
					sender.sendMessage(plugin.messageManager.help_page_not_existent(page));
					return;
				}
			} catch (Exception e) {
				sender.sendMessage(plugin.messageManager.input_not_numeric(input));
				return;
			}
		}
		helpPage.showPage(sender, label, page);
	}
}