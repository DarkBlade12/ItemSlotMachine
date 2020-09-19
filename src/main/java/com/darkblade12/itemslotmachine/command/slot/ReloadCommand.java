package com.darkblade12.itemslotmachine.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineException;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import org.bukkit.command.CommandSender;

import java.util.List;

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
                return;
            }

            long duration = System.currentTimeMillis() - startTime;
            String version = plugin.getDescription().getVersion();
            plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_SUCCEEDED, version, duration);
            return;
        }

        String name = args[0];
        SlotMachine slot = plugin.getManager(SlotMachineManager.class).getSlotMachine(name);
        if (slot == null) {
            plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
            return;
        }
        name = slot.getName();

        try {
            slot.reload();
        } catch (SlotMachineException e) {
            plugin.logException(e, "Failed to reload slot machine %s!", name);
            plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_SINGLE_FAILED, name, e.getMessage());
            return;
        }

        plugin.sendMessage(sender, Message.COMMAND_SLOT_RELOAD_SINGLE_SUCCEEDED, name);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.getManager(SlotMachineManager.class).getNames() : null;
    }
}
