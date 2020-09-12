package com.darkblade12.itemslotmachine.plugin.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.Permission;
import com.darkblade12.itemslotmachine.plugin.PluginBase;

class HelpCommand<T extends PluginBase> extends CommandBase<T> {
    private final CommandHelp<T> help;

    public HelpCommand(CommandHandler<T> handler, int commandsPerPage) {
        super("help", Permission.NONE, "[page]");
        help = new CommandHelp<T>(handler, commandsPerPage);
    }

    @Override
    public void execute(T plugin, CommandSender sender, String label, String[] args) {
        int page = 1;
        if (args.length == 1) {
            String input = args[0];
            try {
                page = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                plugin.sendMessage(sender, Message.COMMAND_HELP_PAGE_INVALID, input);
                return;
            }

            if (!help.hasPage(sender, page)) {
                plugin.sendMessage(sender, Message.COMMAND_HELP_PAGE_NOT_FOUND, page);
                return;
            }
        }

        help.displayPage(sender, label, page);
    }

    @Override
    public List<String> getCompletions(T plugin, CommandSender sender, String[] args) {
        if (args.length != 1) {
            return null;
        }

        int pages = help.getPages(sender);
        List<String> completions = new ArrayList<>(pages);
        for (int i = 1; i <= pages; i++) {
            completions.add(String.valueOf(i));
        }
        return completions;
    }

}
