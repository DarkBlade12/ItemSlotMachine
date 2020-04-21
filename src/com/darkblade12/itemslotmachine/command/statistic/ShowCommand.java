package com.darkblade12.itemslotmachine.command.statistic;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.statistic.types.PlayerStatistic;

@CommandDetails(name = "show", params = "<slot/player> <name>", permission = "ItemSlotMachine.statistic.show")
public final class ShowCommand implements ICommand {
    @SuppressWarnings("deprecation")
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        String type = params[0].toLowerCase();
        switch (type) {
            case "slot":
                SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[1]);
                if (slot == null) {
                    sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
                    return;
                }
                sender.sendMessage(plugin.messageManager.statistic_show_slot_machine(slot.getName(), slot.getStatistic()));
                break;
            case "player":
                String name = null;
                Player player = Bukkit.getPlayer(params[1]);
                if (player != null) {
                    name = player.getName();
                } else {
                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(params[1]);
                    if (offPlayer != null) {
                        name = offPlayer.getName();
                    } else {
                        sender.sendMessage(plugin.messageManager.player_not_existent());
                        return;
                    }
                }

                PlayerStatistic statistic = plugin.statisticManager.getStatistic(name);
                if (statistic == null) {
                    sender.sendMessage(plugin.messageManager.statistic_player_not_existent());
                    return;
                }
                sender.sendMessage(plugin.messageManager.statistic_show_player(name, statistic));
                break;
            default:
                plugin.statisticCommandHandler.showUsage(sender, label, this);
                return;
        }
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        switch (params.length) {
            case 1:
                return Arrays.asList(new String[] { "slot", "player" });
            case 2:
                switch (params[0].toLowerCase()) {
                    case "slot":
                        return plugin.slotMachineManager.getSlotMachines().getNames();
                    case "player":
                        return plugin.statisticManager.getStatistics().getNames();
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}