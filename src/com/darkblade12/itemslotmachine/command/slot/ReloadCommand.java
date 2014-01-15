package com.darkblade12.itemslotmachine.command.slot;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "reload", params = "[name]", permission = "ItemSlotMachine.slot.reload")
public final class ReloadCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		if (params.length == 0) {
			long check = System.currentTimeMillis();
			plugin.onReload();
			sender.sendMessage(plugin.messageManager.plugin_reloaded(System.currentTimeMillis() - check));
		} else {
			SlotMachine s = plugin.slotMachineManager.getSlotMachine(params[0]);
			if (s == null) {
				sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
				return;
			}
			try {
				plugin.slotMachineManager.reload(s);
			} catch (Exception e) {
				sender.sendMessage(plugin.messageManager.slot_machine_reload_failure(e.getMessage()));
				if (Settings.isDebugModeEnabled())
					e.printStackTrace();
				return;
			}
			sender.sendMessage(plugin.messageManager.slot_machine_reload(s.getName()));
		}
	}
}