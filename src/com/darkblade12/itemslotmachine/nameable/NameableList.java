package com.darkblade12.itemslotmachine.nameable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class NameableList<T extends Nameable> extends ArrayList<T> {
	private static final long serialVersionUID = 2132464611329949798L;
	private boolean ignoreCase;

	public NameableList(boolean ignoreCase) {
		super();
		this.ignoreCase = ignoreCase;
	}

	public NameableList() {
		this(false);
	}

	public NameableList(Collection<T> c) {
		super(c);
		ignoreCase = false;
	}

	public NameableList(Collection<T> c, boolean ignoreCase) {
		super(c);
		this.ignoreCase = ignoreCase;
	}

	public void remove(String name) {
		for (int i = 0; i < size(); i++) {
			String n = get(i).getName();
			if (ignoreCase ? name.equalsIgnoreCase(n) : name.equals(n))
				remove(i);
		}
	}

	public T get(String name) {
		for (int i = 0; i < size(); i++) {
			T e = get(i);
			String n = e.getName();
			if (ignoreCase ? name.equalsIgnoreCase(n) : name.equals(n))
				return e;
		}
		return null;
	}

	public boolean contains(String name) {
		return get(name) != null;
	}

	public boolean ignoreCase() {
		return this.ignoreCase;
	}

	public List<String> getNames() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < size(); i++)
			list.add(get(i).getName());
		return list;
	}

	public List<String> getNames(Comparator<String> c) {
		List<String> list = getNames();
		Collections.sort(list, c);
		return list;
	}

	public String toString(String seperator) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			if (s.length() > 0)
				s.append(seperator);
			s.append(get(i).toString());
		}
		return s.toString();
	}
}