package com.darkblade12.itemslotmachine.command.statistic;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.PluginBase;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.statistic.Category;
import com.darkblade12.itemslotmachine.statistic.PlayerStatistic;
import com.darkblade12.itemslotmachine.statistic.SlotMachineStatistic;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.statistic.StatisticManager;
import com.darkblade12.itemslotmachine.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TopCommand extends CommandBase<ItemSlotMachine> {
    public TopCommand() {
        super("top", Permission.COMMAND_STATISTIC_TOP, false, "<slot/player>", "<category>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String categoryName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Category category = Category.fromName(plugin, categoryName);
        if (category == null) {
            plugin.sendMessage(sender, Message.STATISTIC_CATEGORY_NOT_FOUND, categoryName);
            return;
        }
        categoryName = category.getLocalizedName(plugin);

        StatisticManager statManager = plugin.getManager(StatisticManager.class);
        String statText;
        String type = args[0].toLowerCase();
        switch (type) {
            case "slot":
                if (!category.isSlotMachineCategory()) {
                    plugin.sendMessage(sender, Message.COMMAND_STATISTIC_TOP_INVALID_CATEGORY, categoryName);
                    return;
                } else if (statManager.getSlotMachineStatisticCount() == 0) {
                    plugin.sendMessage(sender, Message.COMMAND_STATISTIC_TOP_NO_DATA, categoryName);
                    return;
                }

                statText = toString(plugin, statManager.getSlotMachineTop(category), category);
                plugin.sendMessage(sender, Message.COMMAND_STATISTIC_TOP_SLOT_MACHINE, categoryName, statText);
                break;
            case "player":
                if (statManager.getPlayerStatisticCount() == 0) {
                    plugin.sendMessage(sender, Message.COMMAND_STATISTIC_TOP_NO_DATA, categoryName);
                    return;
                }

                statText = toString(plugin, statManager.getPlayerTop(category), category);
                plugin.sendMessage(sender, Message.COMMAND_STATISTIC_TOP_PLAYER, categoryName, statText);
                break;
            default:
                displayUsage(sender, label);
                break;
        }
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        List<Category> categories = null;
        if (args.length > 1) {
            boolean slotOnly = args[0].equalsIgnoreCase("slot");
            categories = Arrays.stream(Category.values()).filter(c -> !slotOnly || c.isSlotMachineCategory()).collect(Collectors.toList());
        }

        switch (args.length) {
            case 1:
                return Arrays.asList("slot", "player");
            case 2:
                return categories.stream().map(MessageUtils::formatName).collect(Collectors.toList());
            default:
                if (args.length >= 3) {
                    List<String> completions = new ArrayList<>();
                    int catLength = args.length - 1;
                    for (Category category : categories) {
                        String[] split = category.name().toLowerCase().split("_");
                        int previousIndex = catLength - 2;
                        if (split.length >= catLength && args[args.length - 2].equalsIgnoreCase(split[previousIndex])) {
                            completions.add(split[previousIndex + 1]);
                        }
                    }
                    return completions;
                }

                return null;
        }
    }

    private static char getSymbol(int number) {
        return number < 1 || number > 10 ? '?' : (char) (0x2775 + number);
    }

    private static ChatColor getPlacementColor(int placement) {
        switch (placement) {
            case 1:
                return ChatColor.GOLD;
            case 2:
                return ChatColor.RED;
            case 3:
                return ChatColor.BLUE;
            default:
                return ChatColor.GRAY;
        }
    }

    private static <T extends Statistic> String toString(PluginBase plugin, List<T> statistics, Category category) {
        StringBuilder text = new StringBuilder();
        int entries = Math.min(statistics.size(), 10);
        for (int i = 0; i < entries; i++) {
            T stat = statistics.get(i);
            String name;
            if (stat instanceof PlayerStatistic) {
                name = ((PlayerStatistic) stat).getPlayerName();
            } else if (stat instanceof SlotMachineStatistic) {
                name = ((SlotMachineStatistic) stat).getName();
            } else {
                continue;
            }

            String value = String.valueOf(stat.getRecord(category).getValue());
            int placement = i + 1;
            ChatColor color = getPlacementColor(placement);
            char symbol = getSymbol(placement);
            String line = plugin.formatMessage(Message.COMMAND_STATISTIC_TOP_LINE, color, symbol, name, value);
            text.append("\n").append(ChatColor.RESET).append(line);
        }

        return text.toString();
    }
}
