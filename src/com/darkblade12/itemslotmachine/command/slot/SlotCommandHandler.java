package com.darkblade12.itemslotmachine.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandHandler;

public final class SlotCommandHandler extends CommandHandler {
	public SlotCommandHandler(ItemSlotMachine plugin) {
		super(plugin, "slot", 4, "ItemSlotMachine.*", "ItemSlotMachine.slot.*");
	}

	@Override
	protected void registerCommands() {
		register(BuildCommand.class);
		register(DestructCommand.class);
		register(ListCommand.class);
		register(TpCommand.class);
		register(RebuildCommand.class);
		register(MoveCommand.class);
		register(DeactivateCommand.class);
		register(MoneyCommand.class);
		register(ItemCommand.class);
		register(ResetCommand.class);
		register(ClearCommand.class);
		register(ReloadCommand.class);
	}
}