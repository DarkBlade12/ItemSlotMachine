package com.darkblade12.itemslotmachine.slotmachine.combo;

public class AmountAction extends Action {
    private double amount;

    public AmountAction(ActionType type, double amount) {
        super(type);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}
