package com.darkblade12.itemslotmachine.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class RemoveCommand extends CommandBase<ItemSlotMachine> {
    public RemoveCommand() {
        super("remove", Permission.COMMAND_SLOT_REMOVE, "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String name = args[0];
        SlotMachineManager slotManager = plugin.getManager(SlotMachineManager.class);
        SlotMachine slot = slotManager.getSlotMachine(name);
        if (slot == null) {
            plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
            return;
        }
        name = slot.getName();

        slotManager.unregister(slot);
        plugin.sendMessage(sender, Message.COMMAND_SLOT_REMOVE_SUCCEEDED, name);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.getManager(SlotMachineManager.class).getNames() : null;
    }
}
