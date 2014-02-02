package com.darkblade12.itemslotmachine.statistic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.darkblade12.itemslotmachine.nameable.NameableList;

public abstract class Statistic {
	private static final String FORMAT = "\\w+@\\d+(\\.\\d+)?(#\\w+@\\d+(\\.\\d+)?)*";
	private NameableList<StatisticObject> objects;

	public Statistic() {
		objects = new NameableList<StatisticObject>();
	}

	public Statistic(Collection<StatisticObject> c) {
		objects = new NameableList<StatisticObject>(c);
	}

	public Statistic(StatisticObject... objects) {
		this(Arrays.asList(objects));
	}

	public Statistic(Type... types) {
		this();
		for (Type t : types)
			objects.add(t.createObject());
	}

	public void loadStatistic(String s) throws Exception {
		if (!s.matches(FORMAT))
			throw new IllegalArgumentException("Invalid format");
		objects.clear();
		for (String o : s.split("#"))
			objects.add(StatisticObject.fromString(o));
	}

	public void resetValues() {
		for (int i = 0; i < objects.size(); i++)
			objects.get(i).resetValue();
	}

	public List<StatisticObject> getObjects() {
		return Collections.unmodifiableList(objects);
	}

	public StatisticObject getObject(String name) {
		return objects.get(name);
	}

	public StatisticObject getObject(Type t) {
		return getObject(t.name());
	}

	@Override
	public String toString() {
		return objects.toString("#");
	}
}