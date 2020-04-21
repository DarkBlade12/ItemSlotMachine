package com.darkblade12.itemslotmachine.command.slot;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "rebuild", params = "<name>", permission = "ItemSlotMachine.slot.rebuild")
public final class RebuildCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[0]);
        if (slot == null) {
            sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
            return;
        }

        try {
            slot.rebuild();
        } catch (Exception e) {
            sender.sendMessage(plugin.messageManager.slot_machine_rebuilding_failure(slot.getName(), e.getMessage()));
            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }
        }
        sender.sendMessage(plugin.messageManager.slot_machine_rebuilding_success(slot.getName()));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        switch (params.length) {
            case 1:
                return plugin.slotMachineManager.getSlotMachines().getNames();
            default:
                return null;
        }
    }
}