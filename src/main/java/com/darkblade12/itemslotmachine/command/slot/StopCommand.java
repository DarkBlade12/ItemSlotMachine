package com.darkblade12.itemslotmachine.command.slot;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

public final class StopCommand extends CommandBase<ItemSlotMachine> {
    public StopCommand() {
        super("stop", Permission.COMMAND_SLOT_STOP, "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String name = args[0];
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(name);
        if (slot == null) {
            plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
            return;
        }
        name = slot.getName();

        if (!slot.isSpinning()) {
            plugin.sendMessage(sender, Message.COMMAND_SLOT_STOP_NOT_SPINNING, name);
            return;
        }

        slot.stop(true);
        plugin.sendMessage(sender, Message.COMMAND_SLOT_STOP_SUCCEEDED, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.slotMachineManager.getNames() : null;
    }
}