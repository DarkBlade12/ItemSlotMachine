package com.darkblade12.itemslotmachine.command;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.plugin.command.CommandHandler;
import com.darkblade12.itemslotmachine.plugin.command.CommandRegistrationException;
import com.darkblade12.itemslotmachine.command.statistic.ResetCommand;
import com.darkblade12.itemslotmachine.command.statistic.ShowCommand;
import com.darkblade12.itemslotmachine.command.statistic.TopCommand;

public final class StatisticCommandHandler extends CommandHandler<ItemSlotMachine> {
    public StatisticCommandHandler(ItemSlotMachine plugin) {
        super(plugin, "statistic");
    }

    @Override
    protected void registerCommands() throws CommandRegistrationException {
        registerCommand(ShowCommand.class);
        registerCommand(TopCommand.class);
        registerCommand(ResetCommand.class);
    }
}
