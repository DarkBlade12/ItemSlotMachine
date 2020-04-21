package com.darkblade12.itemslotmachine.command.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.statistic.Category;

@CommandDetails(name = "top", params = "<slot/player> <category>", permission = "ItemSlotMachine.statistic.top", infiniteParams = true)
public final class TopCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        Category category = Category.fromName(plugin, String.join(" ", Arrays.copyOfRange(params, 1, params.length)));
        if (category == null) {
            sender.sendMessage(plugin.messageManager.statistic_top_category_not_existent());
            return;
        }

        String type = params[0].toLowerCase();
        switch (type) {
            case "slot":
                if (category == Category.WON_MONEY || category == Category.WON_ITEMS) {
                    sender.sendMessage(plugin.messageManager.statistic_top_slot_machine_invalid_category());
                    return;
                } else if (plugin.slotMachineManager.getSlotMachineAmount() == 0) {
                    sender.sendMessage(plugin.messageManager.statistic_top_slot_machine_not_existent());
                    return;
                }
                sender.sendMessage(plugin.messageManager.statistic_top_slot_machine(category));
                break;
            case "player":
                if (plugin.statisticManager.getStatisticAmount() == 0) {
                    sender.sendMessage(plugin.messageManager.statistic_top_player_not_existent());
                    return;
                }
                sender.sendMessage(plugin.messageManager.statistic_top_player(category));
                break;
            default:
                plugin.statisticCommandHandler.showUsage(sender, label, this);
                return;
        }
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        List<String> completions;
        switch (params.length) {
            case 1:
                return Arrays.asList(new String[] { "slot", "players" });
            case 2:
                completions = new ArrayList<String>();
                for (Category category : Category.values()) {
                    completions.add(category.name().toLowerCase().replace('_', ' '));
                }
                return completions;
            case 3:
                completions = new ArrayList<String>();
                for (Category category : Category.values()) {
                    String[] split = category.name().toLowerCase().split("_");
                    if (params[1].equalsIgnoreCase(split[0]) && split.length == 2) {
                        completions.add(split[1]);
                    }
                }
            default:
                return null;
        }
    }
}