package com.darkblade12.itemslotmachine.command;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;

public abstract interface ICommand {
	public abstract void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params);
}