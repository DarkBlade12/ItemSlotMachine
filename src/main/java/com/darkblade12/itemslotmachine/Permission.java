package com.darkblade12.itemslotmachine;

import com.darkblade12.itemslotmachine.plugin.command.PermissionProvider;
import org.bukkit.command.CommandSender;

public enum Permission implements PermissionProvider {
    ALL("itemslotmachine.*"),
    COMMAND_ALL("itemslotmachine.command.*", ALL),
    COMMAND_DESIGN_ALL("itemslotmachine.command.design.*", COMMAND_ALL),
    COMMAND_DESIGN_WAND("itemslotmachine.command.design.wand", COMMAND_DESIGN_ALL),
    COMMAND_DESIGN_CREATE("itemslotmachine.command.design.create", COMMAND_DESIGN_ALL),
    COMMAND_DESIGN_REMOVE("itemslotmachine.command.design.remove", COMMAND_DESIGN_ALL),
    COMMAND_DESIGN_LIST("itemslotmachine.command.design.list", COMMAND_DESIGN_ALL),
    COMMAND_DESIGN_INVERT("itemslotmachine.command.design.invert", COMMAND_DESIGN_ALL),
    COMMAND_DESIGN_RELOAD("itemslotmachine.command.design.reload", COMMAND_DESIGN_ALL),
    COMMAND_COIN_ALL("itemslotmachine.command.coin.*", COMMAND_ALL),
    COMMAND_COIN_BUY("itemslotmachine.command.coin.buy", COMMAND_COIN_ALL),
    COMMAND_COIN_GIVE("itemslotmachine.command.coin.give", COMMAND_COIN_ALL),
    COMMAND_SLOT_ALL("itemslotmachine.command.slot.*", COMMAND_ALL),
    COMMAND_SLOT_BUILD("itemslotmachine.command.slot.build", COMMAND_SLOT_ALL),
    COMMAND_SLOT_REMOVE("itemslotmachine.command.slot.remove", COMMAND_SLOT_ALL),
    COMMAND_SLOT_LIST("itemslotmachine.command.slot.list", COMMAND_SLOT_ALL),
    COMMAND_SLOT_TP("itemslotmachine.command.slot.tp", COMMAND_SLOT_ALL),
    COMMAND_SLOT_REBUILD("itemslotmachine.command.slot.rebuild", COMMAND_SLOT_ALL),
    COMMAND_SLOT_MOVE("itemslotmachine.command.slot.move", COMMAND_SLOT_ALL),
    COMMAND_SLOT_STOP("itemslotmachine.command.slot.stop", COMMAND_SLOT_ALL),
    COMMAND_SLOT_MONEY("itemslotmachine.command.slot.money", COMMAND_SLOT_ALL),
    COMMAND_SLOT_ITEM("itemslotmachine.command.slot.item", COMMAND_SLOT_ALL),
    COMMAND_SLOT_RESET("itemslotmachine.command.slot.reset", COMMAND_SLOT_ALL),
    COMMAND_SLOT_RELOAD("itemslotmachine.command.slot.reload", COMMAND_SLOT_ALL),
    COMMAND_STATISTIC_ALL("itemslotmachine.command.statistic.*", COMMAND_ALL),
    COMMAND_STATISTIC_SHOW("itemslotmachine.command.statistic.show", COMMAND_STATISTIC_ALL),
    COMMAND_STATISTIC_TOP("itemslotmachine.command.statistic.top", COMMAND_STATISTIC_ALL),
    COMMAND_STATISTIC_RESET("itemslotmachine.command.statistic.reset", COMMAND_STATISTIC_ALL),
    SLOT_ALL("itemslotmachine.slot.*", ALL),
    SLOT_MODIFY_ALL("itemslotmachine.slot.modify.*", SLOT_ALL),
    SLOT_USE_ALL("itemslotmachine.slot.use.*", SLOT_ALL),
    SLOT_USE("itemslotmachine.slot.use", SLOT_USE_ALL),
    SLOT_INSPECT("itemslotmachine.slot.inspect", SLOT_ALL),
    SHOP_CREATE("itemslotmachine.shop.create", ALL);

    private final String node;
    private final Permission parent;

    Permission(String node, Permission parent) {
        this.node = node;
        this.parent = parent;
    }

    Permission(String node) {
        this(node, null);
    }

    @Override
    public boolean test(CommandSender sender) {
        return sender.hasPermission(node) || testParent(sender);
    }

    public boolean testParent(CommandSender sender) {
        return parent != null && parent.test(sender);
    }

    @Override
    public String getNode() {
        return node;
    }

    public Permission getParent() {
        return parent;
    }
}
