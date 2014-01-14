package com.darkblade12.itemslotmachine.command.slot;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "build", params = "<design> [name]", executableAsConsole = false, permission = "ItemSlotMachine.slot.build")
public final class BuildCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		Player p = (Player) sender;
		Design d = plugin.designManager.getDesign(params[0]);
		if (d == null) {
			p.sendMessage(plugin.messageManager.design_not_existent());
			return;
		}
		String name;
		if (params.length == 2) {
			name = params[1];
			if (plugin.slotMachineManager.hasSlotMachine(name)) {
				p.sendMessage(plugin.messageManager.slot_machine_already_existent());
				return;
			}
		} else
			name = plugin.slotMachineManager.generateName();
		try {
			plugin.slotMachineManager.register(SlotMachine.create(plugin, name, d, p));
		} catch (Exception e) {
			p.sendMessage(plugin.messageManager.slot_machine_building_failure(e.getMessage()));
			if (Settings.isDebugModeEnabled())
				e.printStackTrace();
			return;
		}
		p.sendMessage(plugin.messageManager.slot_machine_building_success(name));
	}
}