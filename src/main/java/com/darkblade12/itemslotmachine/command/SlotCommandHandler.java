package com.darkblade12.itemslotmachine.command;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.plugin.command.CommandHandler;
import com.darkblade12.itemslotmachine.plugin.command.CommandRegistrationException;
import com.darkblade12.itemslotmachine.command.slot.BuildCommand;
import com.darkblade12.itemslotmachine.command.slot.ItemCommand;
import com.darkblade12.itemslotmachine.command.slot.ListCommand;
import com.darkblade12.itemslotmachine.command.slot.MoneyCommand;
import com.darkblade12.itemslotmachine.command.slot.MoveCommand;
import com.darkblade12.itemslotmachine.command.slot.RebuildCommand;
import com.darkblade12.itemslotmachine.command.slot.ReloadCommand;
import com.darkblade12.itemslotmachine.command.slot.RemoveCommand;
import com.darkblade12.itemslotmachine.command.slot.StopCommand;
import com.darkblade12.itemslotmachine.command.slot.TpCommand;

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
        registerCommand(StopCommand.class);
        registerCommand(MoneyCommand.class);
        registerCommand(ItemCommand.class);
        registerCommand(ReloadCommand.class);
    }
}
