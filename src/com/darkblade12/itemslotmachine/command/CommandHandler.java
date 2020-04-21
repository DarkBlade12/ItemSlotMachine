package com.darkblade12.itemslotmachine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.general.HelpCommand;

public abstract class CommandHandler implements CommandExecutor, TabCompleter {
    private ItemSlotMachine plugin;
    private String defaultLabel;
    private CommandList commands;
    public CommandHelpPage helpPage;
    private ICommand helpCommand;
    private List<String> masterPermissions;

    public CommandHandler(ItemSlotMachine plugin, String defaultLabel, int commandsPerPage, String... masterPermissions) {
        this.plugin = plugin;
        this.defaultLabel = defaultLabel;
        PluginCommand command = plugin.getCommand(defaultLabel);
        command.setExecutor(this);
        command.setTabCompleter(this);
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
            ICommand command = commands.get(args[0]);
            if (command == null) {
                showUsage(sender, label, helpCommand);
            } else {
                CommandDetails c = CommandList.getDetails(command);
                String[] params = trimParams(args);
                if (!(sender instanceof Player) && !c.executableAsConsole()) {
                    sender.sendMessage(plugin.messageManager.command_no_console_executor());
                } else if (!c.permission().equals("None") && !sender.hasPermission(c.permission())
                        && !hasMasterPermission(sender)) {
                    sender.sendMessage(plugin.messageManager.command_no_permission());
                } else if (!checkUsage(command, params)) {
                    showUsage(sender, label, command);
                } else {
                    command.execute(plugin, sender, label, params);
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length < 1) {
            return null;
        }

        List<String> completions;
        if (args.length == 1) {
            completions = new ArrayList<String>();
            for (ICommand icmd : commands) {
                String name = icmd.getClass().getAnnotation(CommandDetails.class).name().toLowerCase();
                completions.add(name);
            }
        } else {
            ICommand command = getCommand(args[0]);
            if (command == null) {
                return null;
            }

            completions = command.getCompletions(plugin, sender, trimParams(args));
        }

        String filter = args[args.length - 1].toLowerCase();
        if (filter.length() > 0) {
            for (int i = 0; i < completions.size(); i++) {
                if (!completions.get(i).toLowerCase().startsWith(filter)) {
                    completions.remove(i--);
                }
            }
        }

        return completions;
    }

    protected void register(Class<? extends ICommand> clazz) {
        if (clazz.getAnnotation(CommandDetails.class) != null)
            try {
                commands.add(clazz.getDeclaredConstructor().newInstance());
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