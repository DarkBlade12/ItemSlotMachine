package com.darkblade12.itemslotmachine.command.slot;

import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "move", params = "<name> <amount>", executableAsConsole = false, permission = "ItemSlotMachine.slot.move")
public class MoveCommand implements ICommand {
	@Override
	public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
		Player p = (Player) sender;
		SlotMachine s = plugin.slotMachineManager.getSlotMachine(params[0]);
		if (s == null) {
			sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
			return;
		}
		Direction d = Direction.get(p);
		float pitch = p.getLocation().getPitch();
		BlockFace b;
		if (pitch <= -67.5)
			b = BlockFace.UP;
		else if (pitch >= 67.5)
			b = BlockFace.DOWN;
		else
			b = d.toBlockFace();
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
		try {
			s.move(b, amount);
		} catch (Exception e) {
			sender.sendMessage(plugin.messageManager.slot_machine_moving_failure(e.getMessage()));
			return;
		}
		sender.sendMessage(plugin.messageManager.slot_machine_moving_success(s.getName()));
	}
}