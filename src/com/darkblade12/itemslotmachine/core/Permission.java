package com.darkblade12.itemslotmachine.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public enum Permission {
    NONE("none") {
        @Override
        public boolean has(CommandSender sender) {
            return true;
        }
    },
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
    COMMAND_SLOT_DEACTIVATE("itemslotmachine.command.slot.deactivate", COMMAND_SLOT_ALL),
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
    SLOT_INSPECT("itemslotmachine.slot.inspect", SLOT_ALL),
    SLOT_USE("itemslotmachine.slot.use", SLOT_USE_ALL),
    SHOP_CREATE("itemslotmachine.sign.create", ALL);

    private static final Map<String, Permission> NAME_MAP = new HashMap<>();
    private static final Map<String, Permission> NODE_MAP = new HashMap<>();
    private final String node;
    private final Permission parent;

    static {
        for (Permission perm : values()) {
            NAME_MAP.put(perm.name(), perm);
            if (perm == NONE) {
                continue;
            }
            NODE_MAP.put(perm.node, perm);
        }
    }

    private Permission(String node, Permission parent) {
        this.node = node;
        this.parent = parent;
    }

    private Permission(String node) {
        this(node, null);
    }

    public static Permission fromName(String name) {
        return NAME_MAP.getOrDefault(name.toUpperCase(), null);
    }

    public static Permission fromNode(String node) {
        return NODE_MAP.getOrDefault(node.toLowerCase(), null);
    }

    public static boolean hasAny(CommandSender sender, Iterable<String> names) {
        for (String name : names) {
            Permission perm = fromName(name);
            if (perm != null && perm.has(sender) || sender.hasPermission(name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasAny(CommandSender sender, String... names) {
        return hasAny(sender, Arrays.asList(names));
    }

    public static boolean hasAll(CommandSender sender, Iterable<String> names) {
        for (String name : names) {
            if (sender.hasPermission(name)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static boolean hasAll(CommandSender sender, String... name) {
        return hasAll(sender, Arrays.asList(name));
    }

    public String getNode() {
        return node;
    }

    public Permission getParent() {
        return parent;
    }

    public List<Permission> getChildren() {
        List<Permission> children = new ArrayList<Permission>();
        for (Permission perm : values()) {
            if (perm.getParent() == this) {
                children.add(perm);
            }
        }
        return children;
    }

    public boolean has(CommandSender sender) {
        return sender.hasPermission(node) || hasParent(sender);
    }

    public boolean hasParent(CommandSender sender) {
        return parent == null || parent.has(sender);
    }
}
