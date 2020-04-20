package com.darkblade12.itemslotmachine.command.statistic;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.statistic.Category;

@CommandDetails(name = "top", params = "<slot/player> <category>", permission = "ItemSlotMachine.statistic.top", infiniteParams = true)
public final class TopCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        String type = params[0].toLowerCase();
        Category category = Category.fromName(plugin, String.join(" ", Arrays.copyOfRange(params, 1, params.length)));

        if (category == null) {
            sender.sendMessage(plugin.messageManager.statistic_top_category_not_existent());
            return;
        }

        if (type.equals("slot")) {
            if (category == Category.WON_MONEY || category == Category.WON_ITEMS) {
                sender.sendMessage(plugin.messageManager.statistic_top_slot_machine_invalid_category());
                return;
            } else if (plugin.slotMachineManager.getSlotMachineAmount() == 0) {
                sender.sendMessage(plugin.messageManager.statistic_top_slot_machine_not_existent());
                return;
            }
            sender.sendMessage(plugin.messageManager.statistic_top_slot_machine(category));
        } else if (type.equals("player")) {
            if (plugin.statisticManager.getStatisticAmount() == 0) {
                sender.sendMessage(plugin.messageManager.statistic_top_player_not_existent());
                return;
            }
            sender.sendMessage(plugin.messageManager.statistic_top_player(category));
        } else {
            plugin.statisticCommandHandler.showUsage(sender, label, this);
        }
    }
}