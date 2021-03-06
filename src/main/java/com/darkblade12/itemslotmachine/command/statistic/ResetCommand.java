package com.darkblade12.itemslotmachine.command.statistic;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.statistic.StatisticManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class ResetCommand extends CommandBase<ItemSlotMachine> {
    public ResetCommand() {
        super("reset", Permission.COMMAND_STATISTIC_RESET, "<slot/player>", "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        StatisticManager statManager = plugin.getManager(StatisticManager.class);
        Statistic stat;
        String name = args[1];
        String type = args[0].toLowerCase();
        switch (type) {
            case "slot":
                SlotMachine slot = plugin.getManager(SlotMachineManager.class).getSlotMachine(name);
                if (slot == null) {
                    plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
                    return;
                }

                name = slot.getName();
                stat = statManager.getSlotMachineStatistic(slot);
                if (stat == null) {
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
                stat = statManager.getPlayerStatistic(name);
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
            stat.saveFile(statManager.getDataDirectory());
        } catch (IOException e) {
            // TODO: Rollback on failure
            String error = e.getMessage();
            Message message;
            if (type.equals("slot")) {
                plugin.logException(e, "Failed to reset the statistic of slot machine %s!", name);
                message = Message.COMMAND_STATISTIC_RESET_SLOT_MACHINE_FAILED;
            } else {
                plugin.logException(e, "Failed to reset the statistic of player %s!", name);
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
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Arrays.asList("slot", "player");
            case 2:
                switch (args[0].toLowerCase()) {
                    case "slot":
                        return plugin.getManager(SlotMachineManager.class).getNames();
                    case "player":
                        return plugin.getManager(StatisticManager.class).getPlayerNames();
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
