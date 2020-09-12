package com.darkblade12.itemslotmachine.command.slot;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.Permission;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineException;

public final class ReloadCommand extends CommandBase<ItemSlotMachine> {
    public ReloadCommand() {
        super("reload", Permission.COMMAND_SLOT_RELOAD, "[name]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            long startTime = System.currentTimeMillis();
            if (!plugin.onReload()) {
                plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_FAILED);
            } else {
                long duration = System.currentTimeMillis() - startTime;
                String version = plugin.getDescription().getVersion();
                plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_SUCCEEDED, version, duration);
            }
            return;
        }

        String name = args[0];
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(name);
        if (slot == null) {
            plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
            return;
        }
        name = slot.getName();

        try {
            slot.reload();
        } catch (SlotMachineException ex) {
            plugin.logException("Failed to reload slot machine {1}: {0}", ex, name);
            plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_SINGLE_FAILED, name, ex.getMessage());
            return;
        }
        plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_SINGLE_SUCCEEDED, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.slotMachineManager.getNames() : null;
    }
}
