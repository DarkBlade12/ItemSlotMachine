package com.darkblade12.itemslotmachine.design;

import java.text.MessageFormat;

public class DesignIncompleteException extends Exception {
    private static final long serialVersionUID = 8021953975913528853L;

    public DesignIncompleteException(String message) {
        super(message);
    }
    
    public DesignIncompleteException(String message, Object... args) {
        this(MessageFormat.format(message, args));
    }
}
