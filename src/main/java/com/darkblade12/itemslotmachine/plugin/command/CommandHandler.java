package com.darkblade12.itemslotmachine.plugin.command;

import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.PluginBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CommandHandler<T extends PluginBase> implements CommandExecutor, TabCompleter, Iterable<CommandBase<T>> {
    protected final T plugin;
    protected final String defaultLabel;
    protected final Map<String, CommandBase<T>> commands;
    protected final HelpCommand<T> help;

    protected CommandHandler(T plugin, String defaultLabel, int helpPageSize) {
        this.plugin = plugin;
        this.defaultLabel = defaultLabel;
        commands = new LinkedHashMap<>();
        help = new HelpCommand<>(this, helpPageSize);
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

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        if (!command.isExecutableAsConsole() && !(sender instanceof Player)) {
            plugin.sendMessage(sender, Message.COMMAND_NO_CONSOLE);
            return true;
        }

        if (!command.testPermission(sender)) {
            plugin.sendMessage(sender, Message.COMMAND_NO_PERMISSION);
            return true;
        }

        if (!command.isValid(subArgs)) {
            displayInvalidUsage(sender, command, label);
            return true;
        }

        command.execute(plugin, sender, label, subArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command bukkitCommand, String alias, String[] args) {
        if (args.length < 1) {
            return Collections.emptyList();
        }

        List<String> suggestions;
        if (args.length == 1) {
            suggestions = commands.values().stream().filter(c -> c.testPermission(sender)).map(CommandBase::getName)
                                  .collect(Collectors.toList());
        } else {
            CommandBase<T> command = getCommand(args[0]);
            if (command == null) {
                return null;
            }

            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            suggestions = command.getSuggestions(plugin, sender, subArgs);
        }

        if (suggestions == null || suggestions.isEmpty()) {
            return Collections.emptyList();
        }

        String filter = args[args.length - 1].toLowerCase();
        if (filter.isEmpty()) {
            return suggestions;
        }

        return suggestions.stream().filter(arg -> arg.toLowerCase().startsWith(filter)).collect(Collectors.toList());
    }

    private void registerExecutor() throws CommandRegistrationException {
        PluginCommand command = plugin.getCommand(defaultLabel);
        if (command == null) {
            throw new CommandRegistrationException("The command '%s' is not registered in the plugin.yml.", defaultLabel);
        }

        command.setExecutor(this);
    }

    protected void registerCommand(CommandBase<T> command) throws CommandRegistrationException {
        String name = command.getName();
        if (commands.containsKey(name)) {
            throw new CommandRegistrationException("The command '%s' cannot be registered multiple times.", name);
        }

        commands.put(name, command);
    }

    protected void registerCommand(Class<? extends CommandBase<T>> cmdClass) throws CommandRegistrationException {
        CommandBase<T> command;
        try {
            command = cmdClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new CommandRegistrationException("Failed to instantiate the command of class '%s'.", cmdClass.getName(), e);
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

    public String getDefaultLabel() {
        return defaultLabel;
    }

    public CommandBase<T> getCommand(String name) {
        return commands.getOrDefault(name.toLowerCase(), null);
    }
}
