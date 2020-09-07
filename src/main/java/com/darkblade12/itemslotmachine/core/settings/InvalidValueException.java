package com.darkblade12.itemslotmachine.core.settings;

import java.text.MessageFormat;

public final class InvalidValueException extends RuntimeException {
    private static final long serialVersionUID = 4622357814134077126L;
    
    public InvalidValueException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }
}
