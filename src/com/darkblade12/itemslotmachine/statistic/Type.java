package com.darkblade12.itemslotmachine.statistic;

import java.util.HashMap;
import java.util.Map;

import com.darkblade12.itemslotmachine.ItemSlotMachine;

public enum Type {
	TOTAL_SPINS(Integer.class) {
		@Override
		public Integer parse(String s) {
			return Integer.parseInt(s);
		}

		@Override
		public String getRealName(ItemSlotMachine plugin) {
			return plugin.messageManager.total_spins();
		}
	},
	WON_SPINS(Integer.class) {
		@Override
		public Integer parse(String s) {
			return Integer.parseInt(s);
		}

		@Override
		public String getRealName(ItemSlotMachine plugin) {
			return plugin.messageManager.won_spins();
		}
	},
	LOST_SPINS(Integer.class) {
		@Override
		public Integer parse(String s) {
			return Integer.parseInt(s);
		}

		@Override
		public String getRealName(ItemSlotMachine plugin) {
			return plugin.messageManager.lost_spins();
		}
	},
	COINS_SPENT(Integer.class) {
		@Override
		public Integer parse(String s) {
			return Integer.parseInt(s);
		}

		@Override
		public String getRealName(ItemSlotMachine plugin) {
			return plugin.messageManager.coins_spent();
		}
	},
	WON_MONEY(Double.class) {
		@Override
		public Double parse(String s) {
			return Double.parseDouble(s);
		}

		@Override
		public String getRealName(ItemSlotMachine plugin) {
			return plugin.messageManager.won_money();
		}
	},
	WON_ITEMS(Integer.class) {
		@Override
		public Integer parse(String s) {
			return Integer.parseInt(s);
		}

		@Override
		public String getRealName(ItemSlotMachine plugin) {
			return plugin.messageManager.won_items();
		}
	};

	private Class<? extends Number> valueType;
	private static final Map<String, Type> NAME_MAP = new HashMap<String, Type>();

	static {
		for (Type t : values())
			NAME_MAP.put(t.name(), t);
	}

	private Type(Class<? extends Number> valueType) {
		this.valueType = valueType;
	}

	public StatisticObject createObject() {
		return new StatisticObject(this);
	}

	public abstract Number parse(String s);

	public Class<? extends Number> getValueType() {
		return this.valueType;
	}

	public abstract String getRealName(ItemSlotMachine plugin);

	public static Type fromName(String name) {
		return name == null ? null : NAME_MAP.get(name.toUpperCase());
	}

	public static Type fromName(ItemSlotMachine plugin, String name) {
		Type t = fromName(name);
		if (t == null) {
			for (Type type : values())
				if (type.getRealName(plugin).equalsIgnoreCase(name))
					return type;
			t = fromName(name.replace(" ", "_"));
		}
		return t;
	}
}