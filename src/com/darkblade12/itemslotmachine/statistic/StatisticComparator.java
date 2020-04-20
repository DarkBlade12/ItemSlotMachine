package com.darkblade12.itemslotmachine.statistic;

import java.util.Comparator;

public final class StatisticComparator implements Comparator<Statistic> {
    private Category category;

    public StatisticComparator(Category type) {
        this.category = type;
    }

    @Override
    public int compare(Statistic s1, Statistic s2) {
        StatisticRecord r1 = s1.getRecord(category);
        StatisticRecord r2 = s2.getRecord(category);

        if (r1 == null && r2 != null) {
            return 1;
        } else if (r1 != null && r2 == null) {
            return -1;
        } else if (r1 == null && r2 == null) {
            return 0;
        } else {
            return r2.compareTo(r1);
        }
    }
}