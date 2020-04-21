package com.darkblade12.itemslotmachine.command.slot;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "tp", params = "<name>", executableAsConsole = false, permission = "ItemSlotMachine.slot.tp")
public final class TpCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        Player player = (Player) sender;
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[0]);
        if (slot == null) {
            player.sendMessage(plugin.messageManager.slot_machine_not_existent());
            return;
        }

        try {
            slot.teleport(player);
        } catch (Exception e) {
            player.sendMessage(plugin.messageManager.slot_machine_teleportation_failure(slot.getName(), e.getMessage()));
            return;
        }
        player.sendMessage(plugin.messageManager.slot_machine_teleportation_success(slot.getName()));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        return params.length == 1 ? plugin.slotMachineManager.getSlotMachines().getNames() : null;
    }
}