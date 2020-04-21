package com.darkblade12.itemslotmachine.command.slot;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "destruct", params = "<name>", permission = "ItemSlotMachine.slot.destruct")
public final class DestructCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[0]);
        if (slot == null) {
            sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
            return;
        }

        plugin.slotMachineManager.unregister(slot);
        sender.sendMessage(plugin.messageManager.slot_machine_destruction(slot.getName()));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        return params.length == 1 ? plugin.slotMachineManager.getSlotMachines().getNames() : null;
    }
}