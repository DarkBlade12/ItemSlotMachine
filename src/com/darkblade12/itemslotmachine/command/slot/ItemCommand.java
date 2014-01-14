package com.darkblade12.itemslotmachine.command.slot;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.item.ItemList;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "item", params = "<name> <deposit/set> <hand/items>", permission = "ItemSlotMachine.slot.item", infiniteParams = true)
public final class ItemCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		SlotMachine s = plugin.slotMachineManager.getSlotMachine(params[0]);
		if (s == null) {
			sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
			return;
		} else if (!s.isItemPotEnabled()) {
			sender.sendMessage(plugin.messageManager.slot_machine_item_pot_not_enabled());
			return;
		}
		String source = params[2];
		ItemList list = new ItemList();
		if (source.equalsIgnoreCase("hand")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.messageManager.command_no_console_executor());
				return;
			}
			ItemStack i = ((Player) sender).getItemInHand();
			if (i.getType() == Material.AIR) {
				sender.sendMessage(plugin.messageManager.player_no_item_in_hand());
				return;
			}
			list.add(i);
		} else
			try {
				list = ItemList.fromString(StringUtils.join(Arrays.copyOfRange(params, 2, params.length), " "));
			} catch (Exception e) {
				sender.sendMessage(plugin.messageManager.slot_machine_item_pot_invalid_item_list(e.getMessage()));
				return;
			}
		String operation = params[1].toLowerCase();
		if (operation.equals("deposit")) {
			s.depositPotItems(list);
			if (list.size() == 1)
				sender.sendMessage(plugin.messageManager.slot_machine_item_pot_deposit(list.get(0), s.getName()));
			else
				sender.sendMessage(plugin.messageManager.slot_machine_item_pot_deposit_multiple(list, s.getName()));
		} else if (operation.equals("set")) {
			s.setItemPot(list);
			sender.sendMessage(plugin.messageManager.slot_machine_item_pot_set(s.getName(), list));
		} else
			plugin.slotCommandHandler.showUsage(sender, label, this);
	}
}