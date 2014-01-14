package com.darkblade12.itemslotmachine.command.slot;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "deactivate", params = "<name>", permission = "ItemSlotMachine.slot.deactivate")
public final class DeactivateCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		SlotMachine s = plugin.slotMachineManager.getSlotMachine(params[0]);
		if (s == null) {
			sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
			return;
		} else if (!s.isActive()) {
			sender.sendMessage(plugin.messageManager.slot_machine_not_active());
			return;
		}
		s.deactivate();
		sender.sendMessage(plugin.messageManager.slot_machine_deactivation(s.getName()));
	}
}