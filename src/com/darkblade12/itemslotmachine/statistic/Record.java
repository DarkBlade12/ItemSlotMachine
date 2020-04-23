package com.darkblade12.itemslotmachine.statistic;

import java.math.BigDecimal;

import com.darkblade12.itemslotmachine.nameable.Nameable;

public final class Record implements Nameable, Cloneable, Comparable<Record> {
    private Category category;
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
        return new BigDecimal(value.doubleValue()).compareTo(new BigDecimal(record.getValue().doubleValue()));
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public void resetValue() {
        setValue(category.parse("0"));
    }

    public void increaseValue(byte amount) {
        setValue(value.byteValue() + amount);
    }

    public void increaseValue(short amount) {
        setValue(value.shortValue() + amount);
    }

    public void increaseValue(int amount) {
        setValue(value.intValue() + amount);
    }

    public void increaseValue(long amount) {
        setValue(value.longValue() + amount);
    }

    public void increaseValue(float amount) {
        setValue(value.floatValue() + amount);
    }

    public void increaseValue(double amount) {
        setValue(value.doubleValue() + amount);
    }

    @Override
    public String getName() {
        return category.name();
    }

    public Category getCategory() {
        return this.category;
    }

    public Number getValue() {
        return this.value;
    }

    @Override
    public Record clone() {
        return new Record(category, value);
    }
}