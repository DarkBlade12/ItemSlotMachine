package com.darkblade12.itemslotmachine.statistic.types;

import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reader.CompressedStringReader;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.statistic.Type;

public final class SlotMachineStatistic extends Statistic implements Nameable {
	private String name;
	private CompressedStringReader reader;

	public SlotMachineStatistic(String name) {
		super(Type.TOTAL_SPINS, Type.WON_SPINS, Type.LOST_SPINS);
		this.name = name;
		reader = new CompressedStringReader(name + ".statistic", "plugins/ItemSlotMachine/statistics/slot machine/");
	}

	public static SlotMachineStatistic fromFile(String name) throws Exception {
		SlotMachineStatistic s = new SlotMachineStatistic(name);
		s.loadStatistic();
		return s;
	}

	public void loadStatistic() throws Exception {
		if (reader.getOuputFile().exists())
			loadStatistic(reader.readFromFile());
		else
			saveToFile();
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