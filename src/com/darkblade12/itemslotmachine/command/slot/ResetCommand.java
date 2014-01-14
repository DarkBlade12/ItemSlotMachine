package com.darkblade12.itemslotmachine.command.slot;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "reset", params = "<name> <money/item>", permission = "ItemSlotMachine.slot.reset")
public final class ResetCommand implements ICommand {
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
			}
			sender.sendMessage(plugin.messageManager.slot_machine_money_pot_reset(s.getName(), s.resetMoneyPot()));
		} else if (pot.equals("item")) {
			if (!s.isItemPotEnabled()) {
				sender.sendMessage(plugin.messageManager.slot_machine_item_pot_not_enabled());
				return;
			}
			sender.sendMessage(plugin.messageManager.slot_machine_item_pot_reset(s.getName(), s.resetItemPot()));
		} else
			plugin.slotCommandHandler.showUsage(sender, label, this);
	}
}