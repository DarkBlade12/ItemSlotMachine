package com.darkblade12.itemslotmachine.command.slot;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "money", params = "<name> <deposit/withdraw/set> <amount>", permission = "ItemSlotMachine.slot.money")
public final class MoneyCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		SlotMachine s = plugin.slotMachineManager.getSlotMachine(params[0]);
		if (s == null) {
			sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
			return;
		} else if (!s.isMoneyPotEnabled()) {
			sender.sendMessage(plugin.messageManager.slot_machine_money_pot_not_enabled());
			return;
		}
		String input = params[2];
		double amount;
		try {
			amount = Double.parseDouble(input);
		} catch (Exception e) {
			sender.sendMessage(plugin.messageManager.input_not_numeric(input));
			return;
		}
		if (amount < 0) {
			sender.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.lower_than_number(0)));
			return;
		} else if (amount == 0) {
			sender.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.equals_number(0)));
			return;
		}
		String operation = params[1].toLowerCase();
		if (operation.equals("deposit")) {
			sender.sendMessage(plugin.messageManager.slot_machine_money_pot_deposit(amount, s.getName(), s.depositPotMoney(amount)));
		} else if (operation.equals("withdraw")) {
			double pot = s.getMoneyPot();
			if (pot == 0) {
				sender.sendMessage(plugin.messageManager.slot_machine_money_pot_empty());
				return;
			} else if (amount > pot) {
				sender.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.higher_than_number(pot)));
				return;
			}
			sender.sendMessage(plugin.messageManager.slot_machine_money_pot_withdraw(amount, s.getName(), s.withdrawPotMoney(amount)));
		} else if (operation.equals("set")) {
			s.setMoneyPot(amount);
			sender.sendMessage(plugin.messageManager.slot_machine_money_pot_set(s.getName(), amount));
		} else
			plugin.slotCommandHandler.showUsage(sender, label, this);
	}
}