package com.darkblade12.itemslotmachine.statistic.types;

import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reader.CompressedStringReader;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.statistic.Type;

public final class PlayerStatistic extends Statistic implements Nameable {
	private String name;
	private CompressedStringReader reader;

	public PlayerStatistic(String name) {
		super(Type.values());
		this.name = name;
		reader = new CompressedStringReader(name + ".statistic", "plugins/ItemSlotMachine/statistics/player/");
	}

	public static PlayerStatistic fromFile(String name) throws Exception {
		PlayerStatistic p = new PlayerStatistic(name);
		p.loadStatistic();
		return p;
	}

	public void loadStatistic() throws Exception {
		loadStatistic(reader.readFromFile());
	}

	public boolean saveToFile() {
		return reader.saveToFile(toString());
	}

	public void deleteFile() {
		reader.deleteFile();
	}

	@Override
	public String getName() {
		return this.name;
	}
}