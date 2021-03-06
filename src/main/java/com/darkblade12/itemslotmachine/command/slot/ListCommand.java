package com.darkblade12.itemslotmachine.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ListCommand extends CommandBase<ItemSlotMachine> {
    public ListCommand() {
        super("list", Permission.COMMAND_SLOT_LIST);
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        List<SlotMachine> slots = plugin.getManager(SlotMachineManager.class).getSlotMachines();
        if (slots.isEmpty()) {
            plugin.sendMessage(sender, Message.COMMAND_SLOT_LIST_NONE_AVAILABLE);
            return;
        }

        StringBuilder list = new StringBuilder();
        for (SlotMachine slot : slots) {
            String spinning = (slot.isSpinning() ? "\u00A7a\u2714" : "\u00A7c\u2718");
            String line = plugin.formatMessage(Message.COMMAND_SLOT_LIST_LINE, slot.getName(), spinning);
            list.append("\n").append(ChatColor.RESET).append(line);
        }

        plugin.sendMessage(sender, Message.COMMAND_SLOT_LIST_DISPLAYED, list.toString());
    }
}
