package com.darkblade12.itemslotmachine.command.statistic;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.PluginBase;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.statistic.PlayerStatistic;
import com.darkblade12.itemslotmachine.statistic.Record;
import com.darkblade12.itemslotmachine.statistic.SlotMachineStatistic;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.util.MessageUtils;

public final class ShowCommand extends CommandBase<ItemSlotMachine> {
    private static final Random RANDOM = new Random();

    public ShowCommand() {
        super("show", Permission.COMMAND_STATISTIC_SHOW, "<slot/player>", "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String statText;
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

                SlotMachineStatistic slotStat = plugin.statisticManager.getSlotMachineStatistic(slot);
                if(slotStat == null) {
                    plugin.sendMessage(sender, Message.STATISTIC_UNAVAILABLE_SLOT_MACHINE, name);
                    return;
                }
                
                statText = toString(plugin, slotStat);
                plugin.sendMessage(sender, Message.COMMAND_STATISTIC_SHOW_SLOT_MACHINE, name, statText);
                break;
            case "player":
                OfflinePlayer player = plugin.getPlayer(name);
                if (player == null) {
                    plugin.sendMessage(sender, Message.PLAYER_NOT_FOUND, name);
                    return;
                }
                name = player.getName();

                PlayerStatistic playerStat = plugin.statisticManager.getPlayerStatistic(player);
                if (playerStat == null) {
                    plugin.sendMessage(sender, Message.STATISTIC_UNAVAILABLE_PLAYER, name);
                    return;
                }

                statText = toString(plugin, playerStat);
                plugin.sendMessage(sender, Message.COMMAND_STATISTIC_SHOW_PLAYER, name, statText);
                break;
            default:
                displayUsage(sender, label);
                return;
        }
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

    private static char randomDiceSymbol() {
        return (char) (0x2680 + RANDOM.nextInt(6));
    }

    private static String toString(PluginBase plugin, Statistic statistic) {
        StringBuilder text = new StringBuilder();
        for (Record record : statistic.getRecords()) {
            ChatColor color = MessageUtils.randomColorCode();
            ChatColor altColor = MessageUtils.similarColor(color);
            char dice = randomDiceSymbol();
            String category = StringUtils.capitalize(record.getCategory().getLocalizedName(plugin));
            Number value = record.getValue();
            String line = plugin.formatMessage(Message.COMMAND_STATISTIC_SHOW_LINE, dice, color, category, altColor, value);
            text.append("\n").append(ChatColor.RESET).append(line);
        }
        return text.toString();
    }
}