package com.darkblade12.itemslotmachine.command.slot;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "tp", params = "<name>", executableAsConsole = false, permission = "ItemSlotMachine.slot.tp")
public final class TpCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		Player p = (Player) sender;
		SlotMachine s = plugin.slotMachineManager.getSlotMachine(params[0]);
		if (s == null) {
			p.sendMessage(plugin.messageManager.slot_machine_not_existent());
			return;
		}
		try {
			s.teleport(p);
		} catch (Exception e) {
			p.sendMessage(plugin.messageManager.slot_machine_teleportation_failure(s.getName(), e.getMessage()));
			return;
		}
		p.sendMessage(plugin.messageManager.slot_machine_teleportation_success(s.getName()));
	}
}