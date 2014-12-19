package com.darkblade12.itemslotmachine.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.general.HelpCommand;

public abstract class CommandHandler implements CommandExecutor {
	private ItemSlotMachine plugin;
	private String defaultLabel;
	private CommandList commands;
	public CommandHelpPage helpPage;
	private ICommand helpCommand;
	private List<String> masterPermissions;

	public CommandHandler(ItemSlotMachine plugin, String defaultLabel, int commandsPerPage, String... masterPermissions) {
		this.plugin = plugin;
		this.defaultLabel = defaultLabel;
		plugin.getCommand(defaultLabel).setExecutor(this);
		commands = new CommandList();
		registerCommands();
		helpPage = new CommandHelpPage(plugin, this, commandsPerPage);
		helpCommand = new HelpCommand(helpPage);
		commands.add(helpCommand);
		this.masterPermissions = Arrays.asList(masterPermissions);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			showUsage(sender, label, helpCommand);
		} else {
			ICommand i = commands.get(args[0]);
			if (i == null) {
				showUsage(sender, label, helpCommand);
			} else {
				CommandDetails c = CommandList.getDetails(i);
				String[] params = trimParams(args);
				if (!(sender instanceof Player) && !c.executableAsConsole())
					sender.sendMessage(plugin.messageManager.command_no_console_executor());
				else if (!c.permission().equals("None") && !sender.hasPermission(c.permission()) && !hasMasterPermission(sender))
					sender.sendMessage(plugin.messageManager.command_no_permission());
				else if (!checkUsage(i, params))
					showUsage(sender, label, i);
				else
					i.execute(plugin, sender, label, params);
			}
		}
		return true;
	}

	protected void register(Class<? extends ICommand> clazz) {
		if (clazz.getAnnotation(CommandDetails.class) != null)
			try {
				commands.add(clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	protected abstract void registerCommands();

	private String[] trimParams(String[] args) {
		return (String[]) Arrays.copyOfRange(args, 1, args.length);
	}

	private boolean checkUsage(ICommand i, String[] params) {
		CommandDetails c = CommandList.getDetails(i);
		String commandParams = c.params();
		if (commandParams.length() == 0)
			return params.length == 0;
		String[] p = commandParams.split(" ");
		int min = 0, max = c.infiniteParams() ? 100 : 0;
		for (int a = 0; a < p.length; a++) {
			max++;
			if (!p[a].matches("\\[.*\\]"))
				min++;
		}
		return params.length >= min && params.length <= max;
	}

	public void showUsage(CommandSender sender, String label, ICommand i) {
		sender.sendMessage(plugin.messageManager.command_invalid_usage(getUsage(label, i)));
	}

	public String getDefaultLabel() {
		return this.defaultLabel;
	}

	public List<ICommand> getCommands() {
		return Collections.unmodifiableList(commands);
	}

	public ICommand getCommand(String name) {
		return commands.get(name);
	}

	public List<String> getMasterPermissions() {
		return this.masterPermissions;
	}

	private boolean hasMasterPermission(CommandSender sender) {
		for (String m : masterPermissions)
			if (sender.hasPermission(m))
				return true;
		return false;
	}

	public String getUsage(String label, ICommand i) {
		CommandDetails c = CommandList.getDetails(i);
		String params = c.params();
		return "/" + label + " " + c.name() + (params.length() > 0 ? " " + params : "");
	}

	public String getUsage(ICommand i) {
		return getUsage(defaultLabel, i);
	}
}