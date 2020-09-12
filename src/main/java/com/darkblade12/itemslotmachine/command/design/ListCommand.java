package com.darkblade12.itemslotmachine.command.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ListCommand extends CommandBase<ItemSlotMachine> {
    public ListCommand() {
        super("list", Permission.COMMAND_DESIGN_LIST);
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        if (!plugin.designManager.hasDesigns()) {
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_LIST_NONE_AVAILABLE);
            return;
        }

        List<String> names = plugin.designManager.getNames();
        StringBuilder list = new StringBuilder();
        for (String name : names) {
            ChatColor color = MessageUtils.randomColorCode();
            String line = plugin.formatMessage(Message.COMMAND_DESIGN_LIST_LINE, color, name);
            list.append("\n").append(ChatColor.RESET).append(line);
        }

        plugin.sendMessage(sender, Message.COMMAND_DESIGN_LIST_DISPLAYED, list.toString());
    }
}
