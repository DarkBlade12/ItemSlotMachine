package com.darkblade12.itemslotmachine.command.statistic;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandHandler;

public final class StatisticCommandHandler extends CommandHandler {
	public StatisticCommandHandler(ItemSlotMachine plugin) {
		super(plugin, "statistic", 4, "ItemSlotMachine.*", "ItemSlotMachine.statistic.*");
	}

	@Override
	protected void registerCommands() {
		register(ShowCommand.class);
		register(TopCommand.class);
		register(ResetCommand.class);
	}
}