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
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.statistic.Statistic;

@CommandDetails(name = "reset", params = "<slot/player> <name>", permission = "ItemSlotMachine.statistic.reset")
public final class ResetCommand implements ICommand {
    @SuppressWarnings("deprecation")
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        Statistic statistic;
        String name;

        String type = params[0].toLowerCase();
        switch (type) {
            case "slot":
                SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[1]);
                if (slot == null) {
                    sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
                    return;
                }

                statistic = slot.getStatistic();
                name = slot.getName();
                break;
            case "player":
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

                statistic = plugin.statisticManager.getStatistic(name);
                if (statistic == null) {
                    sender.sendMessage(plugin.messageManager.statistic_player_not_existent());
                    return;
                }
                break;
            default:
                plugin.statisticCommandHandler.showUsage(sender, label, this);
                return;
        }

        statistic.reset();
        try {
            statistic.saveToFile();
        } catch (Exception e) {
            String message = e.getMessage();
            if (type.equals("slot")) {
                sender.sendMessage(plugin.messageManager.statistic_reset_slot_machine_failure(name, message));
            } else {
                sender.sendMessage(plugin.messageManager.statistic_reset_player_failure(name, message));
            }

            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }
            return;
        }

        if (type.equals("slot")) {
            sender.sendMessage(plugin.messageManager.statistic_reset_slot_machine_success(name));
        } else {
            sender.sendMessage(plugin.messageManager.statistic_reset_player_success(name));
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