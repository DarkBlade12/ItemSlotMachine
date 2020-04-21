package com.darkblade12.itemslotmachine.command.coin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.util.ItemList;

@CommandDetails(name = "grant", params = "<player> <amount>", executableAsConsole = true, permission = "ItemSlotMachine.coin.grant")
public final class GrantCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        Player player = Bukkit.getPlayer(params[0]);
        if (player == null) {
            sender.sendMessage(plugin.messageManager.player_not_existent());
            return;
        }

        String input = params[1];
        int amount;
        try {
            amount = Integer.parseInt(input);
        } catch (Exception e) {
            sender.sendMessage(plugin.messageManager.input_not_numeric(input));
            return;
        }

        if (amount < 1) {
            sender.sendMessage(plugin.messageManager.invalid_amount(plugin.messageManager.lower_than_number(1)));
            return;
        }

        String name = sender.getName();
        boolean self = player.getName().equals(name);
        ItemStack coin = plugin.coinManager.getCoin(amount);
        if (!ItemList.hasEnoughSpace(player, coin)) {
            if (self) {
                sender.sendMessage(plugin.messageManager.player_not_enough_space());
            } else {
                sender.sendMessage(plugin.messageManager.player_not_enough_space_other());
            }
            return;
        }
        
        player.getInventory().addItem(coin);
        if (self) {
            sender.sendMessage(plugin.messageManager.coin_grant_self(amount));
        } else {
            player.sendMessage(plugin.messageManager.coin_grant_receiver(amount, name));
            sender.sendMessage(plugin.messageManager.coin_grant_sender(player.getName(), amount));
        }
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        switch (params.length) {
            case 1:
                List<String> completions = new ArrayList<String>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
                return completions;
            case 2:
                return Arrays.asList(new String[] { "1", "10", "25", "50" });
            default:
                return null;
        }
    }
}