package com.darkblade12.itemslotmachine.slotmachine;

public enum Setting {
    COIN_AMOUNT("coin-amount"),
    SYMBOL_TYPES("symbol-types"),
    ALLOW_CREATIVE("allow-creative"),
    LAUNCH_FIREWORKS("launch-fireworks"),
    INDIVIDUAL_PERMISSION("individual-permission"),
    REEL_STOP("reel-stop"),
    REEL_DELAY("reel-delay"),
    WINNING_CHANCE("winning-chance"),
    LOCK_TIME("lock-time"),
    WIN_COMMANDS("win-commands"),
    SOUNDS_SPIN("sounds.spin"),
    SOUNDS_WIN("sounds.win"),
    SOUNDS_LOSE("sounds.lose"),
    MONEY_POT_ENABLED("money-pot.enabled"),
    MONEY_POT_DEFAULT("money-pot.default"),
    MONEY_POT_RAISE("money-pot.raise"),
    MONEY_POT_HOUSE_CUT("money-pot.house-cut"),
    ITEM_POT_ENABLED("item-pot.enabled"),
    ITEM_POT_DEFAULT("item-pot.default"),
    ITEM_POT_RAISE("item-pot.raise"),
    COMBOS("combos");

    private String path;

    private Setting(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}
