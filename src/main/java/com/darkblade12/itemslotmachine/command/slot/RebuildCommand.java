package com.darkblade12.itemslotmachine.command.slot;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.design.DesignBuildException;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

public final class RebuildCommand extends CommandBase<ItemSlotMachine> {
    public RebuildCommand() {
        super("rebuild", Permission.COMMAND_SLOT_REBUILD, "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String name = args[0];
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(name);
        if (slot == null) {
            plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
            return;
        }
        name = slot.getName();

        try {
            slot.rebuild();
        } catch (DesignBuildException ex) {
            plugin.logException("Failed to rebuild slot machine {1}: {0}", ex, name);
            plugin.sendMessage(sender, Message.COMMAND_SLOT_REBUILD_FAILED, name, ex.getMessage());
            return;
        }
        plugin.sendMessage(sender, Message.COMMAND_SLOT_REBUILD_SUCCEEDED, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.slotMachineManager.getNames() : null;
    }
}