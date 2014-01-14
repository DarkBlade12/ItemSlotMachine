package com.darkblade12.itemslotmachine.statistic;

import java.math.BigDecimal;

import com.darkblade12.itemslotmachine.nameable.Nameable;

public final class StatisticObject implements Nameable, Cloneable, Comparable<StatisticObject> {
	private static final String FORMAT = "\\w+@\\d+(\\.\\d+)?";
	private Type type;
	private Number value;

	private StatisticObject(Type type, Number value) {
		this.type = type;
		this.value = value;
	}

	public StatisticObject(Type type) {
		this(type, type.parse("0"));
	}

	public static StatisticObject fromString(String s) throws Exception {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("Invalid format");
		String[] p = s.split("@");
		Type t = Type.fromName(p[0]);
		if (t == null)
			throw new IllegalArgumentException("Invalid type name");
		return new StatisticObject(t, t.parse(p[1]));
	}

	@Override
	public int compareTo(StatisticObject s) {
		return new BigDecimal(value.doubleValue()).compareTo(new BigDecimal(s.getValue().doubleValue()));
	}

	public void setValue(Number value) {
		this.value = value;
	}

	public void resetValue() {
		setValue(type.parse("0"));
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
		return type.name();
	}

	public Type getType() {
		return this.type;
	}

	public Number getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return type.name() + "@" + value;
	}

	@Override
	public StatisticObject clone() {
		return new StatisticObject(type, value);
	}
}