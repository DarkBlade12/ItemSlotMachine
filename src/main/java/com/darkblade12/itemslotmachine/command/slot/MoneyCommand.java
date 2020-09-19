package com.darkblade12.itemslotmachine.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.plugin.hook.VaultHook;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public final class MoneyCommand extends CommandBase<ItemSlotMachine> {
    public MoneyCommand() {
        super("money", Permission.COMMAND_SLOT_MONEY, "<name>", "<clear/deposit/withdraw/set>", "[default/amount]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String name = args[0];
        SlotMachine slot = plugin.getManager(SlotMachineManager.class).getSlotMachine(name);
        if (slot == null) {
            plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
            return;
        }
        name = slot.getName();

        if (!slot.isMoneyPotEnabled()) {
            plugin.sendMessage(sender, Message.COMMAND_SLOT_MONEY_NOT_ENABLED, name);
            return;
        }

        String operation = args[1].toLowerCase();
        if (operation.equals("clear")) {
            slot.clearMoneyPot();
            plugin.sendMessage(sender, Message.COMMAND_SLOT_MONEY_CLEARED, name);
            return;
        } else if (args.length < 3) {
            plugin.sendMessage(sender, Message.COMMAND_SLOT_MONEY_NOT_SPECIFIED);
            return;
        }

        String input = args[2];
        double amount;
        if (input.equalsIgnoreCase("default")) {
            amount = slot.getSettings().getMoneyPotDefault();
        } else {
            try {
                amount = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                plugin.sendMessage(sender, Message.AMOUNT_INVALID, input);
                return;
            }

            if (amount < 0) {
                plugin.sendMessage(sender, Message.AMOUNT_LOWER_THAN, 0);
                return;
            } else if (amount == 0) {
                plugin.sendMessage(sender, Message.AMOUNT_EQUAL_TO, 0);
                return;
            }
        }

        VaultHook vault = plugin.getVaultHook();
        double pot;
        String potCurrency;
        String amountCurrency = vault.getCurrencyName(amount, true);
        switch (operation) {
            case "deposit":
                slot.depositMoney(amount);
                pot = slot.getMoneyPot();
                potCurrency = vault.getCurrencyName(pot, true);
                plugin.sendMessage(sender, Message.COMMAND_SLOT_MONEY_DEPOSITED, amount, amountCurrency, name, pot, potCurrency);
                break;
            case "withdraw":
                pot = slot.getMoneyPot();
                if (pot == 0) {
                    plugin.sendMessage(sender, Message.COMMAND_SLOT_MONEY_EMPTY, name);
                    return;
                } else if (amount > pot) {
                    plugin.sendMessage(sender, Message.AMOUNT_HIGHER_THAN, pot);
                    return;
                }

                slot.withdrawMoney(amount);
                pot = slot.getMoneyPot();
                potCurrency = vault.getCurrencyName(pot, true);
                plugin.sendMessage(sender, Message.COMMAND_SLOT_MONEY_WITHDRAWN, amount, amountCurrency, name, pot, potCurrency);
                break;
            case "set":
                slot.setMoneyPot(amount);
                plugin.sendMessage(sender, Message.COMMAND_SLOT_MONEY_SET, name, amount, amountCurrency);
                break;
            default:
                displayUsage(sender, label);
                break;
        }
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return plugin.getManager(SlotMachineManager.class).getNames();
            case 2:
                return Arrays.asList("clear", "deposit", "withdraw", "set");
            case 3:
                return Arrays.asList("default", "1", "10", "100", "1000");
            default:
                return null;
        }
    }
}
