package com.darkblade12.itemslotmachine.command.coin;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandHandler;

public final class CoinCommandHandler extends CommandHandler {
	public CoinCommandHandler(ItemSlotMachine plugin) {
		super(plugin, "coin", 4, "ItemSlotMachine.*", "ItemSlotMachine.coin.*");
	}

	@Override
	protected void registerCommands() {
		register(PurchaseCommand.class);
		register(GrantCommand.class);
	}
}