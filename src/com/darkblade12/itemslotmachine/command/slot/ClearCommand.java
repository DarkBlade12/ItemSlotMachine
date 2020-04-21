package com.darkblade12.itemslotmachine.command.slot;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "clear", params = "<name> <money/item>", permission = "ItemSlotMachine.slot.clear")
public final class ClearCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[0]);
        if (slot == null) {
            sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
            return;
        }

        String type = params[1].toLowerCase();
        switch (type) {
            case "money":
                if (!slot.isMoneyPotEnabled()) {
                    sender.sendMessage(plugin.messageManager.slot_machine_money_pot_not_enabled());
                    return;
                } else if (slot.isMoneyPotEmpty()) {
                    sender.sendMessage(plugin.messageManager.slot_machine_money_pot_empty());
                    return;
                }
                slot.clearMoneyPot();
                sender.sendMessage(plugin.messageManager.slot_machine_money_pot_clear(slot.getName()));
                break;
            case "item":
                if (!slot.isItemPotEnabled()) {
                    sender.sendMessage(plugin.messageManager.slot_machine_item_pot_not_enabled());
                    return;
                } else if (slot.isItemPotEmpty()) {
                    sender.sendMessage(plugin.messageManager.slot_machine_item_pot_empty());
                    return;
                }
                slot.clearItemPot();
                sender.sendMessage(plugin.messageManager.slot_machine_item_pot_clear(slot.getName()));
                break;
            default:
                plugin.slotCommandHandler.showUsage(sender, label, this);
                return;
        }
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        switch (params.length) {
            case 1:
                return plugin.slotMachineManager.getSlotMachines().getNames();
            case 2:
                return Arrays.asList(new String[] { "money", "item" });
            default:
                return null;
        }
    }
}