package com.darkblade12.itemslotmachine.statistic;

import java.math.BigDecimal;

public final class Record implements Cloneable, Comparable<Record> {
    private final Category category;
    private Number value;

    private Record(Category category, Number value) {
        this.category = category;
        this.value = value;
    }

    public Record(Category category) {
        this(category, category.parse("0"));
    }

    @Override
    public int compareTo(Record record) {
        return BigDecimal.valueOf(value.doubleValue()).compareTo(BigDecimal.valueOf(record.getValue().doubleValue()));
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public void resetValue() {
        value = 0;
    }

    public void increaseValue(Number amount) {
        if (value instanceof Double || amount instanceof Double) {
            value = value.doubleValue() + amount.doubleValue();
            return;
        }

        value = value.intValue() + amount.intValue();
    }

    public Category getCategory() {
        return category;
    }

    public Number getValue() {
        return value;
    }

    @Override
    public Record clone() {
        try {
            return (Record) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException();
        }
    }
}
