package com.darkblade12.itemslotmachine.core.command.statistic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.statistic.Statistic;

public final class ResetCommand extends CommandBase<ItemSlotMachine> {
    public ResetCommand() {
        super("reset", Permission.COMMAND_STATISTIC_RESET, "<slot/player>", "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Statistic stat;
        String name = args[1];
        String type = args[0].toLowerCase();
        switch (type) {
            case "slot":
                SlotMachine slot = plugin.slotMachineManager.getSlotMachine(name);
                if (slot == null) {
                    plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
                    return;
                }
                name = slot.getName();
                stat = plugin.statisticManager.getSlotMachineStatistic(slot);
                if(stat == null) {
                    plugin.sendMessage(sender, Message.STATISTIC_UNAVAILABLE_SLOT_MACHINE, name);
                    return;
                }
                break;
            case "player":
                OfflinePlayer player = plugin.getPlayer(name);
                if (player == null) {
                    plugin.sendMessage(sender, Message.PLAYER_NOT_FOUND, name);
                    return;
                }
                name = player.getName();
                stat = plugin.statisticManager.getPlayerStatistic(name);
                if (stat == null) {
                    plugin.sendMessage(sender, Message.STATISTIC_UNAVAILABLE_PLAYER, name);
                    return;
                }
                break;
            default:
                displayUsage(sender, label);
                return;
        }

        stat.reset();
        try {
            stat.saveFile(plugin.statisticManager.getDataDirectory());
        } catch (IOException ex) {
            // TODO: Rollback on failure
            String error = ex.getMessage();
            Message message;
            if (type.equals("slot")) {
                plugin.logException("Failed to reset the statistic of slot machine {1}: {0}", ex, name);
                message = Message.COMMAND_STATISTIC_RESET_SLOT_MACHINE_FAILED;
            } else {
                plugin.logException("Failed to reset the statistic of player {1}: {0}", ex, name);
                message = Message.COMMAND_STATISTIC_RESET_PLAYER_FAILED;
            }
            plugin.sendMessage(sender, message, name, error);
            return;
        }

        Message message;
        if (type.equals("slot")) {
            message = Message.COMMAND_STATISTIC_RESET_SLOT_MACHINE_SUCCEEDED;
        } else {
            message = Message.COMMAND_STATISTIC_RESET_PLAYER_SUCCEEDED;
        }
        plugin.sendMessage(sender, message, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Arrays.asList(new String[] { "slot", "player" });
            case 2:
                switch (args[0].toLowerCase()) {
                    case "slot":
                        return plugin.slotMachineManager.getNames();
                    case "player":
                        return plugin.statisticManager.getPlayerNames();
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}