package com.darkblade12.itemslotmachine.command;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.command.CommandHandler;
import com.darkblade12.itemslotmachine.core.command.CommandRegistrationException;
import com.darkblade12.itemslotmachine.command.design.CreateCommand;
import com.darkblade12.itemslotmachine.command.design.InvertCommand;
import com.darkblade12.itemslotmachine.command.design.ListCommand;
import com.darkblade12.itemslotmachine.command.design.ReloadCommand;
import com.darkblade12.itemslotmachine.command.design.RemoveCommand;
import com.darkblade12.itemslotmachine.command.design.WandCommand;

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