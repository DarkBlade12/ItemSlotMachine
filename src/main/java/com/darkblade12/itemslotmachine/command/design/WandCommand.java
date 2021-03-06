package com.darkblade12.itemslotmachine.command.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.util.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class WandCommand extends CommandBase<ItemSlotMachine> {
    public WandCommand() {
        super("wand", false, Permission.COMMAND_DESIGN_WAND);
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        ItemStack wand = plugin.getManager(DesignManager.class).getWand();
        if (!ItemUtils.hasEnoughSpace(player, wand)) {
            plugin.sendMessage(player, Message.PLAYER_SELF_NOT_ENOUGH_SPACE);
            return;
        }

        player.getInventory().addItem(wand);
        plugin.sendMessage(sender, Message.COMMAND_DESIGN_WAND_RECEIVED);
    }
}
