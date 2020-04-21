package com.darkblade12.itemslotmachine.command.slot;

import java.util.Arrays;
import java.util.List;

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
        Player player = (Player) sender;
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[0]);
        if (slot == null) {
            sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
            return;
        }

        Direction viewDirection = Direction.getViewDirection(player);
        float pitch = player.getLocation().getPitch();
        BlockFace face;
        if (pitch <= -67.5) {
            face = BlockFace.UP;
        } else if (pitch >= 67.5) {
            face = BlockFace.DOWN;
        } else {
            face = viewDirection.toBlockFace();
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

        try {
            slot.move(face, amount);
        } catch (Exception e) {
            sender.sendMessage(plugin.messageManager.slot_machine_moving_failure(e.getMessage()));
            return;
        }
        sender.sendMessage(plugin.messageManager.slot_machine_moving_success(slot.getName()));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        switch (params.length) {
            case 1:
                return plugin.slotMachineManager.getSlotMachines().getNames();
            case 2:
                return Arrays.asList(new String[] { "1", "5", "10", "25" });
            default:
                return null;
        }
    }
}