package com.darkblade12.itemslotmachine.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineException;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MoveCommand extends CommandBase<ItemSlotMachine> {
    public MoveCommand() {
        super("move", false, Permission.COMMAND_SLOT_MOVE, "<name>", "<amount>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        String name = args[0];
        SlotMachine slot = plugin.getManager(SlotMachineManager.class).getSlotMachine(name);
        if (slot == null) {
            plugin.sendMessage(player, Message.SLOT_MACHINE_NOT_FOUND, name);
            return;
        }
        name = slot.getName();

        BlockFace moveDirection = Direction.getViewFace(player);
        String input = args[1];
        int amount;
        try {
            amount = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            plugin.sendMessage(player, Message.AMOUNT_INVALID, input);
            return;
        }

        if (amount < 1) {
            plugin.sendMessage(player, Message.AMOUNT_LOWER_THAN, 1);
            return;
        }

        try {
            slot.move(moveDirection, amount);
        } catch (SlotMachineException e) {
            plugin.logException(e, "Failed to move slot machine %s!", name);
            plugin.sendMessage(player, Message.COMMAND_SLOT_MOVE_FAILED, name, e.getMessage());
            return;
        }

        plugin.sendMessage(player, Message.COMMAND_SLOT_MOVE_SUCCEEDED, name);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return plugin.getManager(SlotMachineManager.class).getSlotMachines().getNames();
            case 2:
                return Arrays.asList("1", "5", "10", "25");
            default:
                return null;
        }
    }
}
