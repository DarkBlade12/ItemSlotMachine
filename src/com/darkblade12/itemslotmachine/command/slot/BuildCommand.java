package com.darkblade12.itemslotmachine.command.slot;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;

@CommandDetails(name = "build", params = "<design> [name]", executableAsConsole = false, permission = "ItemSlotMachine.slot.build")
public final class BuildCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        Player player = (Player) sender;
        Design design = plugin.designManager.getDesign(params[0]);
        if (design == null) {
            player.sendMessage(plugin.messageManager.design_not_existent());
            return;
        }

        String name;
        if (params.length == 2) {
            name = params[1];
            if (plugin.slotMachineManager.hasSlotMachine(name)) {
                player.sendMessage(plugin.messageManager.slot_machine_already_existent());
                return;
            }
        } else {
            name = plugin.slotMachineManager.generateName();
        }

        try {
            plugin.slotMachineManager.register(SlotMachine.create(plugin, name, design, player));
        } catch (Exception e) {
            player.sendMessage(plugin.messageManager.slot_machine_building_failure(e.getMessage()));
            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }
            return;
        }
        player.sendMessage(plugin.messageManager.slot_machine_building_success(name));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        switch (params.length) {
            case 1:
                return plugin.designManager.getDesigns().getNames();
            case 2:
                return Arrays.asList(new String[] { "[name]" });
            default:
                return null;
        }
    }
}