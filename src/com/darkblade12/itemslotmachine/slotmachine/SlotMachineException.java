package com.darkblade12.itemslotmachine.slotmachine;

public final class SlotMachineException extends Exception {
    private static final long serialVersionUID = -3923053556241306983L;

    public SlotMachineException(String message) {
        super(message);
    }

    public SlotMachineException(String message, Throwable cause) {
        super(message, cause);
    }
}
