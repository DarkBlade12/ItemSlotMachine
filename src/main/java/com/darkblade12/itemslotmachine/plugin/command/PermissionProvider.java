package com.darkblade12.itemslotmachine.plugin.command;

import org.bukkit.command.CommandSender;

public interface PermissionProvider {
    PermissionProvider NONE = new PermissionProvider() {
        @Override
        public boolean test(CommandSender sender) {
            return true;
        }

        @Override
        public String getNode() {
            return "none";
        }
    };

    boolean test(CommandSender sender);

    String getNode();
}
