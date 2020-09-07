package com.darkblade12.itemslotmachine.core.command.design;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.util.MessageUtils;

public final class ListCommand extends CommandBase<ItemSlotMachine> {
    public ListCommand() {
        super("list", Permission.COMMAND_DESIGN_LIST);
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        List<Design> designs = plugin.designManager.getDesigns();
        if (designs.isEmpty()) {
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_LIST_NONE_AVAILABLE);
            return;
        }

        StringBuilder list = new StringBuilder();
        for (Design design : designs) {
            ChatColor color = MessageUtils.randomColorCode();
            String line = plugin.formatMessage(Message.COMMAND_DESIGN_LIST_LINE, color, design.getName());
            list.append("\n").append(ChatColor.RESET).append(line);
        }
        plugin.sendMessage(sender, Message.COMMAND_DESIGN_LIST_DISPLAYED, list.toString());
    }
}