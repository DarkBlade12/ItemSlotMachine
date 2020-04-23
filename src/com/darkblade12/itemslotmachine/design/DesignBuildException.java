package com.darkblade12.itemslotmachine.design;

public class DesignBuildException extends Exception {
    private static final long serialVersionUID = -5254420754496239760L;

    public DesignBuildException(String message, String name) {
        super(message.replace("%n", name));
    }

    public DesignBuildException(String message, Design design) {
        this(message, design.getName());
    }
}
