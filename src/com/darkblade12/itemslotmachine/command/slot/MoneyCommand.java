package com.darkblade12.itemslotmachine.command.slot;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "money", params = "<name> <deposit/withdraw/set> <amount>", permission = "ItemSlotMachine.slot.money")
public final class MoneyCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[0]);
        if (slot == null) {
            sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
            return;
        } else if (!slot.isMoneyPotEnabled()) {
            sender.sendMessage(plugin.messageManager.slot_machine_money_pot_not_enabled());
            return;
        }

        String input = params[2];
        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (Exception e) {
            sender.sendMessage(plugin.messageManager.input_not_numeric(input));
            return;
        }

        if (amount < 0) {
            sender.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.lower_than_number(0)));
            return;
        } else if (amount == 0) {
            sender.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.equals_number(0)));
            return;
        }

        double pot;
        String operation = params[1].toLowerCase();
        switch (operation) {
            case "deposit":
                pot = slot.depositPotMoney(amount);
                sender.sendMessage(plugin.messageManager.slot_machine_money_pot_deposit(amount, slot.getName(), pot));
                break;
            case "withdraw":
                pot = slot.getMoneyPot();
                if (pot == 0) {
                    sender.sendMessage(plugin.messageManager.slot_machine_money_pot_empty());
                    return;
                } else if (amount > pot) {
                    sender.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.higher_than_number(pot)));
                    return;
                }

                pot = slot.withdrawPotMoney(amount);
                sender.sendMessage(plugin.messageManager.slot_machine_money_pot_withdraw(amount, slot.getName(), pot));
                break;
            case "set":
                slot.setMoneyPot(amount);
                sender.sendMessage(plugin.messageManager.slot_machine_money_pot_set(slot.getName(), amount));
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
                return Arrays.asList(new String[] { "deposit", "withdraw", "set" });
            case 3:
                return Arrays.asList(new String[] { "1", "10", "100", "1000" });
            default:
                return null;
        }
    }
}