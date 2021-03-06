package com.darkblade12.itemslotmachine.command.coin;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.coin.CoinManager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.plugin.hook.VaultHook;
import com.darkblade12.itemslotmachine.util.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class BuyCommand extends CommandBase<ItemSlotMachine> {
    public BuyCommand() {
        super("buy", false, Permission.COMMAND_COIN_BUY, "<amount>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        VaultHook vault = plugin.getVaultHook();
        if (!vault.isEnabled()) {
            plugin.sendMessage(sender, Message.COMMAND_COIN_BUY_DISABLED);
            return;
        }

        String input = args[0];
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

        CoinManager coinManager = plugin.getManager(CoinManager.class);
        double price = coinManager.calculatePrice(amount);
        String coinText = plugin.formatMessage(amount == 1 ? Message.WORD_COIN_SINGULAR : Message.WORD_COIN_PLURAL);
        String currency = vault.getCurrencyName(price, true);
        if (vault.getBalance(player) < price) {
            plugin.sendMessage(player, Message.COMMAND_COIN_BUY_NOT_ENOUGH_MONEY, amount, coinText, price, currency);
            return;
        }

        ItemStack coin = coinManager.getCoin(amount);
        if (!ItemUtils.hasEnoughSpace(player, coin)) {
            plugin.sendMessage(player, Message.PLAYER_SELF_NOT_ENOUGH_SPACE);
            return;
        }

        vault.withdrawPlayer(player, price);
        player.getInventory().addItem(coin);
        plugin.sendMessage(player, Message.COMMAND_COIN_BUY_SUCCEEDED, amount, coinText, price, currency);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? Arrays.asList("1", "10", "25", "50") : null;
    }
}
