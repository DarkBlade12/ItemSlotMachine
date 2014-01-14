package com.darkblade12.itemslotmachine.command.coin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.item.ItemList;

@CommandDetails(name = "grant", params = "<player> <amount>", executableAsConsole = true, permission = "ItemSlotMachine.coin.grant")
public final class GrantCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		Player p = Bukkit.getPlayer(params[0]);
		if (p == null) {
			sender.sendMessage(plugin.messageManager.player_not_existent());
			return;
		}
		String input = params[1];
		int amount;
		try {
			amount = Integer.parseInt(input);
		} catch (Exception e) {
			sender.sendMessage(plugin.messageManager.input_not_numeric(input));
			return;
		}
		if (amount < 1) {
			sender.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.lower_than_number(1)));
			return;
		}
		String name = sender.getName();
		boolean self = p.getName().equals(name);
		ItemStack i = plugin.coinManager.getCoin(amount);
		if (!ItemList.hasEnoughSpace(p, i)) {
			sender.sendMessage(self ? plugin.messageManager.player_not_enough_space() : plugin.messageManager.player_not_enough_space_other());
			return;
		}
		p.getInventory().addItem(i);
		if (self) {
			sender.sendMessage(plugin.messageManager.coin_grant_self(amount));
		} else {
			p.sendMessage(plugin.messageManager.coin_grant_receiver(amount, name));
			sender.sendMessage(plugin.messageManager.coin_grant_sender(p.getName(), amount));
		}
	}
}