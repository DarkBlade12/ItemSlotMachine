package com.darkblade12.itemslotmachine.command.design;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.design.DesignManager;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.google.gson.JsonParseException;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

public final class ReloadCommand extends CommandBase<ItemSlotMachine> {
    public ReloadCommand() {
        super("reload", Permission.COMMAND_DESIGN_RELOAD, "[name]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        DesignManager designManager = plugin.getManager(DesignManager.class);
        if (args.length == 0) {
            try {
                designManager.reload();
            } catch (Exception ex) {
                plugin.sendMessage(sender, Message.COMMAND_DESIGN_RELOAD_FAILED, ex.getMessage());
                return;
            }
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_RELOAD_SUCCEEDED);
            return;
        }

        String name = args[0];
        Design design = designManager.getDesign(name);
        if (design == null) {
            plugin.sendMessage(sender, Message.DESIGN_NOT_FOUND, name);
            return;
        }
        name = design.getName();

        try {
            design.reloadFile(designManager.getDataDirectory());
        } catch (IOException | JsonParseException e) {
            plugin.logException(e, "Failed to reload design %s!", name);
            plugin.sendMessage(sender, Message.COMMAND_DESIGN_RELOAD_SINGLE_FAILED, name);
            return;
        }

        plugin.sendMessage(sender, Message.COMMAND_DESIGN_RELOAD_SINGLE_SUCCEEDED, name);
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        return args.length == 1 ? plugin.getManager(DesignManager.class).getNames() : null;
    }
}
