package com.darkblade12.itemslotmachine.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.PluginBase;

public abstract class CommandHandler<T extends PluginBase> implements CommandExecutor, TabCompleter, Iterable<CommandBase<T>> {
    protected final T plugin;
    protected final String defaultLabel;
    protected final Map<String, CommandBase<T>> commands;
    protected final HelpCommand<T> help;

    protected CommandHandler(T plugin, String defaultLabel, int helpPageSize) {
        this.plugin = plugin;
        this.defaultLabel = defaultLabel;
        commands = new LinkedHashMap<String, CommandBase<T>>();
        help = new HelpCommand<T>(this, helpPageSize);
    }

    protected CommandHandler(T plugin, String defaultLabel) {
        this(plugin, defaultLabel, 4);
    }

    public void enable() throws CommandRegistrationException {
        registerCommand(help);
        registerCommands();
        registerExecutor();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] args) {
        if (args.length == 0) {
            displayUnknownCommand(sender, label);
            return true;
        }

        CommandBase<T> command = getCommand(args[0]);
        if (command == null) {
            displayUnknownCommand(sender, label);
            return true;
        }

        String[] newArgs = (String[]) Arrays.copyOfRange(args, 1, args.length);
        if (!command.isExecutableAsConsole() && !(sender instanceof Player)) {
            plugin.sendMessage(sender, Message.COMMAND_NO_CONSOLE);
            return true;
        }

        if (!command.hasPermission(sender)) {
            plugin.sendMessage(sender, Message.COMMAND_NO_PERMISSION);
            return true;
        }

        if (!command.isValid(newArgs)) {
            displayInvalidUsage(sender, command, label);
            return true;
        }

        command.execute(plugin, sender, label, newArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command bukkitCommand, String alias, String[] args) {
        List<String> completions = new ArrayList<String>();
        if (args.length < 1) {
            return completions;
        }
        
        if (args.length == 1) {
            for (CommandBase<T> command : commands.values()) {
                completions.add(command.getName());
            }
        } else {
            CommandBase<T> command = getCommand(args[0]);
            if (command == null) {
                return null;
            }

            String[] newArgs = (String[]) Arrays.copyOfRange(args, 1, args.length);
            List<String> cmdCompletions = command.getCompletions(plugin, sender, newArgs);
            if(cmdCompletions == null) {
                return completions;
            }
            
            completions = cmdCompletions;
        }

        String filter = args[args.length - 1].toLowerCase();
        if (filter.length() == 0) {
            return completions;
        }

        List<String> filtered = new ArrayList<>();
        for (String arg : completions) {
            if (arg.toLowerCase().startsWith(filter)) {
                filtered.add(arg);
            }
        }
        return filtered;
    }

    private void registerExecutor() throws CommandRegistrationException {
        PluginCommand command = plugin.getCommand(defaultLabel);
        if (command == null) {
            throw new CommandRegistrationException("The command '%n' is not registered in the plugin.yml", defaultLabel);
        }

        command.setExecutor(this);
    }

    protected void registerCommand(CommandBase<T> command) throws CommandRegistrationException {
        String name = command.getName();
        if (commands.containsKey(name)) {
            throw new CommandRegistrationException("The command '%n' cannot be registered multiple times", name);
        }
        commands.put(name, command);
    }

    protected void registerCommand(Class<? extends CommandBase<T>> cmdClass) throws CommandRegistrationException {
        CommandBase<T> command;
        try {
            command = cmdClass.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new CommandRegistrationException("Failed to instantiate the command of the class '%n'", cmdClass.getName());
        }
        registerCommand(command);
    }

    protected abstract void registerCommands() throws CommandRegistrationException;

    @Override
    public Iterator<CommandBase<T>> iterator() {
        return commands.values().iterator();
    }

    private void displayUnknownCommand(CommandSender sender, String label) {
        plugin.sendMessage(sender, Message.COMMAND_UNKNOWN, help.getUsage(label));
    }

    public void displayInvalidUsage(CommandSender sender, CommandBase<T> command, String label) {
        plugin.sendMessage(sender, Message.COMMAND_INVALID_USAGE, command.getUsage(label));
    }

    public T getPlugin() {
        return plugin;
    }

    public String getDefaultLabel() {
        return defaultLabel;
    }

    public CommandBase<T> getCommand(String name) {
        return commands.getOrDefault(name.toLowerCase(), null);
    }
}
