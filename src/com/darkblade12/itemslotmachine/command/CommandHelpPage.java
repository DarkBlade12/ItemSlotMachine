package com.darkblade12.itemslotmachine.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;

public final class CommandHelpPage {
	private ItemSlotMachine plugin;
	private CommandHandler handler;
	private int commandsPerPage;

	public CommandHelpPage(ItemSlotMachine plugin, CommandHandler handler, int commandsPerPage) {
		this.plugin = plugin;
		this.handler = handler;
		this.commandsPerPage = commandsPerPage;
	}

	private String insertIntoFormat(String label, ICommand i) {
		CommandDetails c = CommandList.getDetails(i);
		return plugin.messageManager.help_page_command_format(handler.getUsage(label, i), plugin.messageManager.getMessage("description_" + handler.getDefaultLabel() + "_" + c.name()), c.executableAsConsole(),
				c.permission());
	}

	public void showPage(CommandSender sender, String label, int page) {
		List<ICommand> visible = getVisibleCommands(sender);
		String header = plugin.messageManager.help_page_header(label);
		StringBuilder b = new StringBuilder(header);
		for (int i = (page - 1) * commandsPerPage; i <= page * commandsPerPage - 1; i++)
			if (i > visible.size() - 1)
				break;
			else
				b.append("\n§r" + insertIntoFormat(label, visible.get(i)));
		b.append("\n§r" + plugin.messageManager.help_page_footer(page, getPages(sender)));
		sender.sendMessage(b.toString());
	}

	public boolean hasPage(CommandSender sender, int page) {
		return page > 0 && page <= getPages(sender);
	}

	public int getPages(CommandSender sender) {
		double p = (double) getVisibleCommands(sender).size() / (double) commandsPerPage;
		int pr = (int) p;
		return p > (double) pr ? pr + 1 : pr;
	}

	public List<ICommand> getVisibleCommands(CommandSender sender) {
		List<ICommand> visible = new ArrayList<ICommand>();
		for (ICommand i : handler.getCommands()) {
			String permission = CommandList.getDetails(i).permission();
			if (permission.equals("None") || sender.hasPermission(permission))
				visible.add(i);
			else
				master: for (String p : handler.getMasterPermissions())
					if (sender.hasPermission(p)) {
						visible.add(i);
						break master;
					}
		}
		return visible;
	}

	public CommandHandler getHandler() {
		return this.handler;
	}

	public int getCommandsPerPage() {
		return this.commandsPerPage;
	}
}