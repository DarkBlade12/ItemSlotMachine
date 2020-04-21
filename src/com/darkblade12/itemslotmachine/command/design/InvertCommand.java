package com.darkblade12.itemslotmachine.command.design;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.settings.Settings;

@CommandDetails(name = "invert", params = "<name>", permission = "ItemSlotMachine.design.invert")
public final class InvertCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        Design design = plugin.designManager.getDesign(params[0]);
        if (design == null) {
            sender.sendMessage(plugin.messageManager.design_not_existent());
            return;
        } else if (design.getName().equals(Design.DEFAULT_NAME)) {
            sender.sendMessage(plugin.messageManager.design_not_modifiable());
            return;
        }

        try {
            design.invertItemFrames();
        } catch (Exception e) {
            sender.sendMessage(plugin.messageManager.design_inversion_failure(design.getName(), e.getMessage()));
            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }
        }
        sender.sendMessage(plugin.messageManager.design_inversion_success(design.getName()));
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        return params.length == 1 ? plugin.designManager.getDesigns().getNames() : null;
    }
}