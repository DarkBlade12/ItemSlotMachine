package com.darkblade12.itemslotmachine.command.coin;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.coin.CoinManager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class GiveCommand extends CommandBase<ItemSlotMachine> {
    public GiveCommand() {
        super("give", Permission.COMMAND_COIN_GIVE, "<player>", "<amount>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            plugin.sendMessage(sender, Message.PLAYER_NOT_FOUND, args[0]);
            return;
        }

        String input = args[1];
        int amount;
        try {
            amount = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            plugin.sendMessage(sender, Message.AMOUNT_INVALID, input);
            return;
        }

        if (amount < 1) {
            plugin.sendMessage(sender, Message.AMOUNT_LOWER_THAN, 1);
            return;
        }

        String senderName = sender.getName();
        boolean self = player.getName().equals(senderName);
        ItemStack coin = plugin.getManager(CoinManager.class).getCoin(amount);
        if (!ItemUtils.hasEnoughSpace(player, coin)) {
            if (self) {
                plugin.sendMessage(sender, Message.PLAYER_SELF_NOT_ENOUGH_SPACE);
            } else {
                plugin.sendMessage(sender, Message.PLAYER_NOT_ENOUGH_SPACE);
            }
            return;
        }

        String coinText = plugin.formatMessage(amount == 1 ? Message.WORD_COIN_SINGULAR : Message.WORD_COIN_PLURAL);
        player.getInventory().addItem(coin);
        if (self) {
            plugin.sendMessage(sender, Message.COMMAND_COIN_GIVE_RECEIVED_SELF, amount, coinText);
        } else {
            plugin.sendMessage(player, Message.COMMAND_COIN_GIVE_RECEIVED, amount, coinText, senderName);
            plugin.sendMessage(sender, Message.COMMAND_COIN_GIVE_SENT, amount, coinText, player.getName());
        }
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            case 2:
                return Arrays.asList("1", "10", "25", "50");
            default:
                return null;
        }
    }
}
