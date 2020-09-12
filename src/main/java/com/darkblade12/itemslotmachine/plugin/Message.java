package com.darkblade12.itemslotmachine.plugin;

import java.util.HashMap;
import java.util.Map;

public enum Message {
    MESSAGE_MISSING("message.missing"),
    MESSAGE_PREFIX("message.prefix"),

    COIN_ITEM_NAME("coin.item.name"),
    COIN_ITEM_LORE("coin.item.lore"),
    
    SIGN_SHOP_HEADER("sign.shop.header"),
    SIGN_SHOP_SPACER("sign.shop.spacer"),
    SIGN_SHOP_COINS("sign.shop.coins"),
    SIGN_SHOP_PRICE("sign.shop.price"),
    SIGN_POT_MONEY("sign.pot.money"),
    SIGN_POT_ITEMS("sign.pot.items"),
    SIGN_POT_SPACER("sign.pot.spacer"),
    
    WORD_COIN_SINGULAR("word.coin-singular"),
    WORD_COIN_PLURAL("word.coin-plural"),
    WORD_AND("word.and"),
    WORD_EMPTY("word.empty"),

    AMOUNT_INVALID("amount.invalid"),
    AMOUNT_LOWER_THAN("amount.lower-than"),
    AMOUNT_HIGHER_THAN("amount.higher-than"),
    AMOUNT_EQUAL_TO("amount.equal-to"),

    PLAYER_NOT_FOUND("player.not-found"),
    PLAYER_NOT_ENOUGH_SPACE("player.not-enough-space"),
    PLAYER_SELF_NOT_ENOUGH_SPACE("player.self-not-enough-space"),

    DESIGN_NOT_FOUND("design.not-found"),
    DESIGN_WAND_NAME("design.wand.name"),
    DESIGN_WAND_LORE("design.wand.lore"),
    DESIGN_WAND_FIRST_SELECTED("design.wand.first-selected"),
    DESIGN_WAND_SECOND_SELECTED("design.wand.second-selected"),

    STATISTIC_CATEGORY_NOT_FOUND("statistic.category.not-found"),
    STATISTIC_UNAVAILABLE_SLOT_MACHINE("statistic.unavailable.slot-machine"),
    STATISTIC_UNAVAILABLE_PLAYER("statistic.unavailable.player"),
    STATISTIC_CATEGORY_TOTAL_SPINS("statistic.category.total-spins"),
    STATISTIC_CATEGORY_WON_SPINS("statistic.category.won-spins"),
    STATISTIC_CATEGORY_LOST_SPINS("statistic.category.lost-spins"),
    STATISTIC_CATEGORY_SPENT_COINS("statistic.category.spent-coins"),
    STATISTIC_CATEGORY_WON_MONEY("statistic.category.won-money"),
    STATISTIC_CATEGORY_WON_ITEMS("statistic.category.won-items"),

    SLOT_MACHINE_NOT_FOUND("slot-machine.not-found"),
    SLOT_MACHINE_INSPECTED("slot-machine.inspected"),
    SLOT_MACHINE_NO_MODIFY("slot-machine.no-modify"),
    SLOT_MACHINE_MODIFY_NOT_ALLOWED("slot-machine.modify-not-allowed"),
    SLOT_MACHINE_USE_NOT_ALLOWED("slot-machine.use-not-allowed"),
    SLOT_MACHINE_BROKEN("slot-machine.broken"),
    SLOT_MACHINE_STILL_SPINNING("slot-machine.still-spinning"),
    SLOT_MACHINE_NO_CREATIVE("slot-machine.no-creative"),
    SLOT_MACHINE_NOT_ENOUGH_COINS("slot-machine.not-enough-coins"),
    SLOT_MACHINE_USE_LIMITED("slot-machine.use-limited"),
    SLOT_MACHINE_LOCKED("slot-machine.locked"),
    SLOT_MACHINE_WON("slot-machine.won"),
    SLOT_MACHINE_LOST("slot-machine.lost"),

    COMMAND_NO_CONSOLE("command.no-console"),
    COMMAND_NO_PERMISSION("command.no-permission"),
    COMMAND_DESCRIPTION_MISSING("command.description-missing"),
    COMMAND_UNKNOWN("command.unknown"),
    COMMAND_INVALID_USAGE("command.invalid-usage"),

    COMMAND_HELP_HEADER("command.help.header"),
    COMMAND_HELP_FOOTER("command.help.footer"),
    COMMAND_HELP_COMMAND_INFO("command.help.command-info"),
    COMMAND_HELP_PAGE_INVALID("command.help.page-invalid"),
    COMMAND_HELP_PAGE_NOT_FOUND("command.help.page-not-found"),
    COMMAND_HELP_DESCRIPTION("command.help.description"),

    COMMAND_COIN_BUY_DISABLED("command.coin.buy.disabled"),
    COMMAND_COIN_BUY_NOT_ENOUGH_MONEY("command.coin.buy.not-enough-money"),
    COMMAND_COIN_BUY_SUCCEEDED("command.coin.buy.succeeded"),
    COMMAND_COIN_BUY_DESCRIPTION("command.coin.buy.description"),

    COMMAND_COIN_GIVE_RECEIVED("command.coin.give.received"),
    COMMAND_COIN_GIVE_RECEIVED_SELF("command.coin.give.received-self"),
    COMMAND_COIN_GIVE_SENT("command.coin.give.sent"),
    COMMAND_COIN_GIVE_DESCRIPTION("command.coin.give.description"),

    COMMAND_DESIGN_WAND_RECEIVED("command.design.wand.received"),
    COMMAND_DESIGN_WAND_DESCRIPTION("command.design.wand.description"),

    COMMAND_DESIGN_CREATE_INVALID_SELECTION("command.design.create.invalid-selection"),
    COMMAND_DESIGN_CREATE_ALREADY_EXISTS("command.design.create.already-exists"),
    COMMAND_DESIGN_CREATE_FAILED("command.design.create.failed"),
    COMMAND_DESIGN_CREATE_SUCCEEDED("command.design.create.succeeded"),
    COMMAND_DESIGN_CREATE_DESCRIPTION("command.design.create.description"),

    COMMAND_DESIGN_REMOVE_NO_DEFAULT("command.design.remove.no-default"),
    COMMAND_DESIGN_REMOVE_FAILED("command.design.remove.failed"),
    COMMAND_DESIGN_REMOVE_SUCCEEDED("command.design.remove.succeeded"),
    COMMAND_DESIGN_REMOVE_DESCRIPTION("command.design.remove.description"),

    COMMAND_DESIGN_LIST_NONE_AVAILABLE("command.design.list.none-available"),
    COMMAND_DESIGN_LIST_LINE("command.design.list.line"),
    COMMAND_DESIGN_LIST_DISPLAYED("command.design.list.displayed"),
    COMMAND_DESIGN_LIST_DESCRIPTION("command.design.list.description"),

    COMMAND_DESIGN_INVERT_NO_DEFAULT("command.design.invert.no-default"),
    COMMAND_DESIGN_INVERT_FAILED("command.design.invert.failed"),
    COMMAND_DESIGN_INVERT_SUCCEEDED("command.design.invert.succeeded"),
    COMMAND_DESIGN_INVERT_DESCRIPTION("command.design.invert.description"),

    COMMAND_DESIGN_RELOAD_FAILED("command.design.reload.failed"),
    COMMAND_DESIGN_RELOAD_SINGLE_FAILED("command.design.reload.single-failed"),
    COMMAND_DESIGN_RELOAD_SUCCEEDED("command.design.reload.succeeded"),
    COMMAND_DESIGN_RELOAD_SINGLE_SUCCEEDED("command.design.reload.single-succeeded"),
    COMMAND_DESIGN_RELOAD_DESCRIPTION("command.design.reload.description"),

    COMMAND_STATISTIC_SHOW_LINE("command.statistic.show.line"),
    COMMAND_STATISTIC_SHOW_SLOT_MACHINE("command.statistic.show.slot-machine"),
    COMMAND_STATISTIC_SHOW_PLAYER("command.statistic.show.player"),
    COMMAND_STATISTIC_SHOW_DESCRIPTION("command.statistic.show.description"),

    COMMAND_STATISTIC_TOP_INVALID_CATEGORY("command.statistic.top.invalid-category"),
    COMMAND_STATISTIC_TOP_NO_DATA("command.statistic.top.no-data"),
    COMMAND_STATISTIC_TOP_LINE("command.statistic.top.line"),
    COMMAND_STATISTIC_TOP_SLOT_MACHINE("command.statistic.top.slot-machine"),
    COMMAND_STATISTIC_TOP_PLAYER("command.statistic.top.player"),
    COMMAND_STATISTIC_TOP_DESCRIPTION("command.statistic.top.description"),

    COMMAND_STATISTIC_RESET_SLOT_MACHINE_FAILED("command.statistic.reset.slot-machine-failed"),
    COMMAND_STATISTIC_RESET_SLOT_MACHINE_SUCCEEDED("command.statistic.reset.slot-machine-succeeded"),
    COMMAND_STATISTIC_RESET_PLAYER_FAILED("command.statistic.reset.player-failed"),
    COMMAND_STATISTIC_RESET_PLAYER_SUCCEEDED("command.statistic.reset.player-succeeded"),
    COMMAND_STATISTIC_RESET_DESCRIPTION("command.statistic.reset.description"),

    COMMAND_SLOT_BUILD_ALREADY_EXISTS("command.slot.build.already-exists"),
    COMMAND_SLOT_BUILD_FAILED("command.slot.build.failed"),
    COMMAND_SLOT_BUILD_SUCCEEDED("command.slot.build.succeeded"),
    COMMAND_SLOT_BUILD_DESCRIPTION("command.slot.build.description"),

    COMMAND_SLOT_REMOVE_SUCCEEDED("command.slot.remove.succeeded"),
    COMMAND_SLOT_REMOVE_DESCRIPTION("command.slot.remove.description"),

    COMMAND_SLOT_LIST_NONE_AVAILABLE("command.slot.list.none-available"),
    COMMAND_SLOT_LIST_LINE("command.slot.list.line"),
    COMMAND_SLOT_LIST_DISPLAYED("command.slot.list.displayed"),
    COMMAND_SLOT_LIST_DESCRIPTION("command.slot.list.description"),

    COMMAND_SLOT_TP_FAILED("command.slot.tp.failed"),
    COMMAND_SLOT_TP_SUCCEEDED("command.slot.tp.succeeded"),
    COMMAND_SLOT_TP_DESCRIPTION("command.slot.tp.description"),

    COMMAND_SLOT_REBUILD_FAILED("command.slot.rebuild.failed"),
    COMMAND_SLOT_REBUILD_SUCCEEDED("command.slot.rebuild.succeeded"),
    COMMAND_SLOT_REBUILD_DESCRIPTION("command.slot.rebuild.description"),

    COMMAND_SLOT_MOVE_FAILED("command.slot.move.failed"),
    COMMAND_SLOT_MOVE_SUCCEEDED("command.slot.move.succeeded"),
    COMMAND_SLOT_MOVE_DESCRIPTION("command.slot.move.description"),

    COMMAND_SLOT_STOP_NOT_SPINNING("command.slot.stop.not-spinning"),
    COMMAND_SLOT_STOP_SUCCEEDED("command.slot.stop.succeeded"),
    COMMAND_SLOT_STOP_DESCRIPTION("command.slot.stop.description"),

    COMMAND_SLOT_MONEY_NOT_ENABLED("command.slot.money.not-enabled"),
    COMMAND_SLOT_MONEY_NOT_SPECIFIED("command.slot.money.not-specified"),
    COMMAND_SLOT_MONEY_EMPTY("command.slot.money.empty"),
    COMMAND_SLOT_MONEY_CLEARED("command.slot.money.cleared"),
    COMMAND_SLOT_MONEY_DEPOSITED("command.slot.money.deposited"),
    COMMAND_SLOT_MONEY_WITHDRAWN("command.slot.money.withdrawn"),
    COMMAND_SLOT_MONEY_SET("command.slot.money.set"),
    COMMAND_SLOT_MONEY_DESCRIPTION("command.slot.money.description"),

    COMMAND_SLOT_ITEM_NOT_ENABLED("command.slot.item.not-enabled"),
    COMMAND_SLOT_ITEM_NOT_SPECIFIED("command.slot.item.not-specified"),
    COMMAND_SLOT_ITEM_EMPTY("command.slot.item.empty"),
    COMMAND_SLOT_ITEM_EMPTY_HAND("command.slot.item.empty-hand"),
    COMMAND_SLOT_ITEM_INVALID_LIST("command.slot.item.invalid-list"),
    COMMAND_SLOT_ITEM_CLEARED("command.slot.item.cleared"),
    COMMAND_SLOT_ITEM_ADDED("command.slot.item.added"),
    COMMAND_SLOT_ITEM_SINGLE_ADDED("command.slot.item.single-added"),
    COMMAND_SLOT_ITEM_SET("command.slot.item.set"),
    COMMAND_SLOT_ITEM_DESCRIPTION("command.slot.item.description"),
    
    COMMAND_SLOT_RELOAD_FAILED("command.slot.reload.failed"),
    COMMAND_SLOT_RELOAD_SINGLE_FAILED("command.slot.reload.single-failed"),
    COMMAND_SLOT_RELOAD_SUCCEEDED("command.slot.reload.succeeded"),
    COMMAND_SLOT_RELOAD_SINGLE_SUCCEEDED("command.slot.reload.single-succeeded"),
    COMMAND_SLOT_RELOAD_DESCRIPTION("command.slot.reload.description");

    private static final Map<String, Message> NAME_MAP = new HashMap<>();
    private static final Map<String, Message> KEY_MAP = new HashMap<>();
    private String key;

    static {
        for (Message msg : values()) {
            NAME_MAP.put(msg.name(), msg);
            KEY_MAP.put(msg.key, msg);
        }
    }

    private Message(String key) {
        this.key = key.toLowerCase();
    }

    public static Message fromName(String name) {
        return NAME_MAP.getOrDefault(name, null);
    }

    public static Message fromKey(String key) {
        return KEY_MAP.getOrDefault(key, null);
    }

    public String getKey() {
        return key;
    }
}
