package com.darkblade12.itemslotmachine.core.command.slot;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

public final class ReloadCommand extends CommandBase<ItemSlotMachine> {
    public ReloadCommand() {
        super("reload", Permission.COMMAND_SLOT_RELOAD, "[name]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            long startTime = System.currentTimeMillis();
            plugin.onReload(); // TODO: Send different message on failure
            long duration = System.currentTimeMillis() - startTime;
            plugin.sendMessage(sender, Message.PLUGIN_RELOADED, plugin.getDescription().getVersion(), duration);
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
            plugin.slotMachineManager.reload(slot);
        } catch (Exception ex) {
            plugin.logException("Failed to reload slot machine '" + name + "': %c", ex);
            plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_FAILED, name, ex.getMessage());
            return;
        }
        plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_SUCCEEDED, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.slotMachineManager.getNames() : null;
    }
}