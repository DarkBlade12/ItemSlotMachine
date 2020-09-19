package com.darkblade12.itemslotmachine.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineException;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class TpCommand extends CommandBase<ItemSlotMachine> {
    public TpCommand() {
        super("tp", false, Permission.COMMAND_SLOT_TP, "<name>");
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

        try {
            slot.teleport(player);
        } catch (SlotMachineException ex) {
            plugin.sendMessage(player, Message.COMMAND_SLOT_TP_FAILED, name, ex.getMessage());
            return;
        }

        plugin.sendMessage(player, Message.COMMAND_SLOT_TP_SUCCEEDED, name);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.getManager(SlotMachineManager.class).getNames() : null;
    }
}
