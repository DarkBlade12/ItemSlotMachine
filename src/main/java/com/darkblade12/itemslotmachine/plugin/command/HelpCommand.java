package com.darkblade12.itemslotmachine.plugin.command;

import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.PluginBase;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class HelpCommand<T extends PluginBase> extends CommandBase<T> {
    private final CommandHelpIndex<T> help;

    HelpCommand(CommandHandler<T> handler, int commandsPerPage) {
        super("help", PermissionProvider.NONE, "[page]");
        help = new CommandHelpIndex<>(handler, commandsPerPage);
    }

    @Override
    public void execute(T plugin, CommandSender sender, String label, String[] args) {
        int page = 1;
        if (args.length == 1) {
            String input = args[0];
            try {
                page = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                plugin.sendMessage(sender, Message.COMMAND_HELP_PAGE_INVALID, input);
                return;
            }

            if (!help.hasPage(sender, page)) {
                plugin.sendMessage(sender, Message.COMMAND_HELP_PAGE_NOT_FOUND, page);
                return;
            }
        }

        help.displayPage(plugin, sender, label, page);
    }

    @Override
    public List<String> getSuggestions(T plugin, CommandSender sender, String[] args) {
        if (args.length != 1) {
            return null;
        }

        return IntStream.rangeClosed(1, help.getPages(sender)).mapToObj(String::valueOf).collect(Collectors.toList());
    }
}
