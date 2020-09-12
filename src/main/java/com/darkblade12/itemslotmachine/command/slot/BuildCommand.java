package com.darkblade12.itemslotmachine.command.slot;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.Permission;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

public final class BuildCommand extends CommandBase<ItemSlotMachine> {
    public BuildCommand() {
        super("build", false, Permission.COMMAND_SLOT_BUILD, "<design>", "[name]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        String designName = args[0];
        Design design = plugin.designManager.getDesign(designName);
        if (design == null) {
            plugin.sendMessage(player, Message.DESIGN_NOT_FOUND, designName);
            return;
        }
        designName = design.getName();

        String name;
        if (args.length < 2) {
            name = plugin.slotMachineManager.generateName();
        } else {
            name = args[1];
        }

        if (plugin.slotMachineManager.hasSlotMachine(name)) {
            plugin.sendMessage(player, Message.COMMAND_SLOT_BUILD_ALREADY_EXISTS, name);
            return;
        }

        try {
            plugin.slotMachineManager.register(SlotMachine.create(plugin, name, design, player));
        } catch (Exception ex) {
            plugin.logException("Failed to build slot machine {1} with design {2}: {0}", ex, name, designName);
            plugin.sendMessage(player, Message.COMMAND_SLOT_BUILD_FAILED, name, designName, ex.getMessage());
            return;
        }
        plugin.sendMessage(sender, Message.COMMAND_SLOT_BUILD_SUCCEEDED, name);
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.designManager.getNames() : null;
    }
}
