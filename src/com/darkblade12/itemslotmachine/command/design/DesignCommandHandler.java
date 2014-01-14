package com.darkblade12.itemslotmachine.command.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandHandler;

public final class DesignCommandHandler extends CommandHandler {
	public DesignCommandHandler(ItemSlotMachine plugin) {
		super(plugin, "design", 4, "ItemSlotMachine.*", "ItemSlotMachine.design.*");
	}

	@Override
	protected void registerCommands() {
		register(WandCommand.class);
		register(CreateCommand.class);
		register(RemoveCommand.class);
		register(ListCommand.class);
		register(InvertCommand.class);
		register(ReloadCommand.class);
	}
}