package com.darkblade12.itemslotmachine.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;

public interface ICommand {
    void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params);

    List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params);
}