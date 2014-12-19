package com.darkblade12.itemslotmachine.command.statistic;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.statistic.types.PlayerStatistic;

@CommandDetails(name = "reset", params = "<slot/player> <name>", permission = "ItemSlotMachine.statistic.reset")
public final class ResetCommand implements ICommand {
	@SuppressWarnings("deprecation")
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		String type = params[0].toLowerCase();
		if (type.equals("slot")) {
			SlotMachine s = plugin.slotMachineManager.getSlotMachine(params[1]);
			if (s == null) {
				sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
				return;
			}
			s.getStatistic().resetValues();
			sender.sendMessage(plugin.messageManager.statistic_reset_slot_machine(s.getName()));
		} else if (type.equals("player")) {
			String name = null;
			Player p = Bukkit.getPlayer(params[1]);
			if (p != null) {
				name = p.getName();
			} else {
				OfflinePlayer o = Bukkit.getOfflinePlayer(params[1]);
				if (o != null) {
					name = o.getName();
				} else {
					sender.sendMessage(plugin.messageManager.player_not_existent());
					return;
				}
			}
			PlayerStatistic s = plugin.statisticManager.getStatistic(name);
			if (s == null) {
				sender.sendMessage(plugin.messageManager.statistic_player_not_existent());
				return;
			}
			s.resetValues();
			sender.sendMessage(plugin.messageManager.statistic_reset_player(name));
		} else
			plugin.statisticCommandHandler.showUsage(sender, label, this);
	}
}