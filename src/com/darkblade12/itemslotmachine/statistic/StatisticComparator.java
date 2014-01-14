package com.darkblade12.itemslotmachine.statistic;

import java.util.Comparator;

public final class StatisticComparator implements Comparator<Statistic> {
	private Type type;

	public StatisticComparator(Type type) {
		this.type = type;
	}

	@Override
	public int compare(Statistic s1, Statistic s2) {
		StatisticObject o1 = s1.getObject(type);
		StatisticObject o2 = s2.getObject(type);
		if (o1 == null && o2 != null)
			return 1;
		else if (o1 != null && o2 == null)
			return -1;
		else if (o1 == null && o2 == null)
			return 0;
		else
			return o2.compareTo(o1);
	}
}