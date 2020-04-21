package com.darkblade12.itemslotmachine.command.coin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.hook.VaultHook;
import com.darkblade12.itemslotmachine.util.ItemList;

@CommandDetails(name = "purchase", params = "<amount>", executableAsConsole = false, permission = "ItemSlotMachine.coin.purchase")
public final class PurchaseCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        Player player = (Player) sender;
        if (!VaultHook.isEnabled()) {
            player.sendMessage(plugin.messageManager.coin_purchase_disabled());
            return;
        }

        String input = params[0];
        int amount;
        try {
            amount = Integer.parseInt(input);
        } catch (Exception e) {
            player.sendMessage(plugin.messageManager.input_not_numeric(input));
            return;
        }

        if (amount < 1) {
            player.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.lower_than_number(1)));
            return;
        }

        double price = plugin.coinManager.calculatePrice(amount);
        if (VaultHook.getBalance(player) < price) {
            player.sendMessage(plugin.messageManager.coin_purchase_not_enough_money(amount, price));
            return;
        }

        ItemStack coin = plugin.coinManager.getCoin(amount);
        if (!ItemList.hasEnoughSpace(player, coin)) {
            player.sendMessage(plugin.messageManager.player_not_enough_space());
            return;
        }

        VaultHook.withdrawPlayer(player, price);
        player.getInventory().addItem(coin);
        player.sendMessage(plugin.messageManager.coin_purchase(amount, price));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        return params.length == 1 ? Arrays.asList(new String[] { "1", "10", "25", "50" }) : null;
    }
}