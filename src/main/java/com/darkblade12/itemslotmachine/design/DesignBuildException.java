package com.darkblade12.itemslotmachine.design;

import java.text.MessageFormat;

public class DesignBuildException extends Exception {
    private static final long serialVersionUID = -5254420754496239760L;

    public DesignBuildException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DesignBuildException(String message) {
        super(message);
    }
    
    public DesignBuildException(String message, Object... args) {
        this(MessageFormat.format(message, args));
    }
}
