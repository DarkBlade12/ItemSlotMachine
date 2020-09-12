package com.darkblade12.itemslotmachine.command;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.plugin.command.CommandHandler;
import com.darkblade12.itemslotmachine.plugin.command.CommandRegistrationException;
import com.darkblade12.itemslotmachine.command.coin.BuyCommand;
import com.darkblade12.itemslotmachine.command.coin.GiveCommand;

public final class CoinCommandHandler extends CommandHandler<ItemSlotMachine> {
    public CoinCommandHandler(ItemSlotMachine plugin) {
        super(plugin, "coin");
    }

    @Override
    protected void registerCommands() throws CommandRegistrationException {
        registerCommand(BuyCommand.class);
        registerCommand(GiveCommand.class);
    }
}
