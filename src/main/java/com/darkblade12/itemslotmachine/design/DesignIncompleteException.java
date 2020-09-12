package com.darkblade12.itemslotmachine.design;

public class DesignIncompleteException extends Exception {
    private static final long serialVersionUID = 1L;

    public DesignIncompleteException(String message) {
        super(message);
    }

    public DesignIncompleteException(String message, Object... args) {
        this(String.format(message, args));
    }
}
