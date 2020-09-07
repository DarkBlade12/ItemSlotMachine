package com.darkblade12.itemslotmachine.core.command.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.command.CommandHandler;
import com.darkblade12.itemslotmachine.core.command.CommandRegistrationException;

public final class DesignCommandHandler extends CommandHandler<ItemSlotMachine> {
    public DesignCommandHandler(ItemSlotMachine plugin) {
        super(plugin, "design");
    }

    @Override
    protected void registerCommands() throws CommandRegistrationException {
        registerCommand(WandCommand.class);
        registerCommand(CreateCommand.class);
        registerCommand(RemoveCommand.class);
        registerCommand(ListCommand.class);
        registerCommand(InvertCommand.class);
        registerCommand(ReloadCommand.class);
    }
}