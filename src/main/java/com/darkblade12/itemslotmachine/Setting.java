package com.darkblade12.itemslotmachine;

public enum Setting {
    DEBUG_MODE_ENABLED("debug-mode-enabled"),
    LANGUAGE_TAG("language-tag"),
    
    DESIGN_NAME_PATTERN("design.name-pattern"),
    DESIGN_SPACE_CHECK_ENABLED("design.space-check.enabled"),
    DESIGN_SPACE_CHECK_IGNORED_TYPES("design.space-check.ignored-types"),
    
    SLOT_MACHINE_NAME_PATTERN("slot-machine.name-pattern"),
    SLOT_MACHINE_USE_LIMIT("slot-machine.use-limit"),

    COIN_TYPE("coin.type"),
    COIN_USE_COMMON_ITEM("coin.use-common-item"),
    COIN_PRICE("coin.price");

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
