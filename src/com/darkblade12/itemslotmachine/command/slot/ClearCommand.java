package com.darkblade12.itemslotmachine.command.slot;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "clear", params = "<name> <money/item>", permission = "ItemSlotMachine.slot.clear")
public final class ClearCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		SlotMachine s = plugin.slotMachineManager.getSlotMachine(params[0]);
		if (s == null) {
			sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
			return;
		}
		String pot = params[1].toLowerCase();
		if (pot.equals("money")) {
			if (!s.isMoneyPotEnabled()) {
				sender.sendMessage(plugin.messageManager.slot_machine_money_pot_not_enabled());
				return;
			} else if (s.isMoneyPotEmpty()) {
				sender.sendMessage(plugin.messageManager.slot_machine_money_pot_empty());
				return;
			}
			s.clearMoneyPot();
			sender.sendMessage(plugin.messageManager.slot_machine_money_pot_clear(s.getName()));
		} else if (pot.equals("item")) {
			if (!s.isItemPotEnabled()) {
				sender.sendMessage(plugin.messageManager.slot_machine_item_pot_not_enabled());
				return;
			} else if (s.isItemPotEmpty()) {
				sender.sendMessage(plugin.messageManager.slot_machine_item_pot_empty());
				return;
			}
			s.clearItemPot();
			sender.sendMessage(plugin.messageManager.slot_machine_item_pot_clear(s.getName()));
		} else
			plugin.slotCommandHandler.showUsage(sender, label, this);
	}
}