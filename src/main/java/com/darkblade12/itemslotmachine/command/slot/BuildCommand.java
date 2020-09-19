package com.darkblade12.itemslotmachine.command.slot;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class BuildCommand extends CommandBase<ItemSlotMachine> {
    public BuildCommand() {
        super("build", false, Permission.COMMAND_SLOT_BUILD, "<design>", "[name]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        String designName = args[0];
        Design design = plugin.getManager(DesignManager.class).getDesign(designName);
        if (design == null) {
            plugin.sendMessage(player, Message.DESIGN_NOT_FOUND, designName);
            return;
        }
        designName = design.getName();

        SlotMachineManager slotManager = plugin.getManager(SlotMachineManager.class);
        String name;
        if (args.length < 2) {
            name = slotManager.generateName();
        } else {
            name = args[1];
        }

        if (slotManager.hasSlotMachine(name)) {
            plugin.sendMessage(player, Message.COMMAND_SLOT_BUILD_ALREADY_EXISTS, name);
            return;
        }

        try {
            slotManager.register(SlotMachine.create(plugin, name, design, player));
        } catch (Exception e) {
            plugin.logException(e, "Failed to build slot machine %s with design %s!", name, designName);
            plugin.sendMessage(player, Message.COMMAND_SLOT_BUILD_FAILED, name, designName, e.getMessage());
            return;
        }
        plugin.sendMessage(sender, Message.COMMAND_SLOT_BUILD_SUCCEEDED, name);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.getManager(DesignManager.class).getNames() : null;
    }
}
