package com.darkblade12.itemslotmachine.core.command.slot;

import java.util.Arrays;
import java.util.List;

import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.design.DesignBuildException;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

public class MoveCommand extends CommandBase<ItemSlotMachine> {
    public MoveCommand() {
        super("move", false, Permission.COMMAND_SLOT_MOVE, "<name>", "<amount>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        String name = args[0];
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(name);
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
        } catch (DesignBuildException ex) {
            plugin.logException("Failed to move slot machine '" + name + "': %c", ex);
            plugin.sendMessage(player, Message.COMMAND_SLOT_MOVE_FAILED, name, ex.getMessage());
            return;
        }
        plugin.sendMessage(player, Message.COMMAND_SLOT_MOVE_SUCCEEDED, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return plugin.slotMachineManager.getSlotMachines().getNames();
            case 2:
                return Arrays.asList(new String[] { "1", "5", "10", "25" });
            default:
                return null;
        }
    }
}