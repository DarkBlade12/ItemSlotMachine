package com.darkblade12.itemslotmachine.core.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.command.CommandHandler;
import com.darkblade12.itemslotmachine.core.command.CommandRegistrationException;

public final class SlotCommandHandler extends CommandHandler<ItemSlotMachine> {
    public SlotCommandHandler(ItemSlotMachine plugin) {
        super(plugin, "slot");
    }

    @Override
    protected void registerCommands() throws CommandRegistrationException {
        registerCommand(BuildCommand.class);
        registerCommand(RemoveCommand.class);
        registerCommand(ListCommand.class);
        registerCommand(TpCommand.class);
        registerCommand(RebuildCommand.class);
        registerCommand(MoveCommand.class);
        registerCommand(DeactivateCommand.class);
        registerCommand(MoneyCommand.class);
        registerCommand(ItemCommand.class);
        registerCommand(ReloadCommand.class);
    }
}