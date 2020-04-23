package com.darkblade12.itemslotmachine.core.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.PluginBase;

final class CommandHelp<T extends PluginBase> {
    private final CommandHandler<T> handler;
    private final int commandsPerPage;

    public CommandHelp(CommandHandler<T> handler, int commandsPerPage) {
        if (commandsPerPage < 1) {
            throw new IllegalArgumentException("The value of commandsPerPage cannot be lower than 1.");
        }
        this.handler = handler;
        this.commandsPerPage = commandsPerPage;
    }

    public void displayPage(CommandSender sender, String label, int page) {
        T plugin = handler.getPlugin();
        List<CommandBase<T>> visible = getVisibleCommands(sender);
        StringBuilder message = new StringBuilder(plugin.formatMessage(Message.COMMAND_HELP_HEADER));
        for (int index = (page - 1) * commandsPerPage; index <= page * commandsPerPage - 1; index++) {
            if (index > visible.size() - 1) {
                break;
            }
            message.append("\n\u00A7r").append(getInfo(visible.get(index), label));
        }
        int pages = getPages(sender);
        String currentPage = (page == pages ? "\u00A76\u00A7l" : "\u00A7a\u00A7l") + page;
        message.append("\n\u00A7r").append(plugin.formatMessage(Message.COMMAND_HELP_FOOTER, currentPage, pages));
        sender.sendMessage(message.toString());
    }

    public int getCommandsPerPage() {
        return commandsPerPage;
    }

    public boolean hasPage(CommandSender sender, int page) {
        return page > 0 && page <= getPages(sender);
    }

    public int getPages(CommandSender sender) {
        int total = getVisibleCommands(sender).size();
        int pages = total / commandsPerPage;
        return total % commandsPerPage == 0 ? pages : ++pages;
    }

    private List<CommandBase<T>> getVisibleCommands(CommandSender sender) {
        List<CommandBase<T>> visible = new ArrayList<>();
        for (CommandBase<T> command : handler) {
            if (command.hasPermission(sender)) {
                visible.add(command);
            }
        }
        return visible;
    }

    private String getInfo(CommandBase<T> command, String label) {
        T plugin = handler.getPlugin();
        String usage = command.getUsage(label);
        String commandName = command.getName();
        String defaultLabel = handler.getDefaultLabel();
        String descriptionText;
        if (commandName.equals("help")) {
            descriptionText = plugin.formatMessage(Message.COMMAND_HELP_DESCRIPTION, defaultLabel);
        } else {
            Message message = Message.fromKey("command." + defaultLabel + "." + commandName + ".description");
            descriptionText = plugin.formatMessage(message);
        }
        String permission = command.getPermission().getNode();
        return plugin.formatMessage(Message.COMMAND_HELP_COMMAND_INFO, usage, descriptionText, permission);
    }
}
