package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Material;

@SuppressWarnings("deprecation")
public final class BlockData {

	private BlockData() {}

	public static int rotate90(int typeId, byte data) {
		switch (Material.getMaterial(typeId)) {
			case TORCH:
			case REDSTONE_TORCH_OFF:
			case REDSTONE_TORCH_ON:
				switch (data) {
					case 1:
						return 3;
					case 2:
						return 4;
					case 3:
						return 2;
					case 4:
						return 1;
				}
				break;
			case RAILS:
				switch (data) {
					case 6:
						return 7;
					case 7:
						return 8;
					case 8:
						return 9;
					case 9:
						return 6;
				}
			case POWERED_RAIL:
			case DETECTOR_RAIL:
			case ACTIVATOR_RAIL:
				switch (data & 0x7) {
					case 0:
						return 1 | (data & ~0x7);
					case 1:
						return 0 | (data & ~0x7);
					case 2:
						return 5 | (data & ~0x7);
					case 3:
						return 4 | (data & ~0x7);
					case 4:
						return 2 | (data & ~0x7);
					case 5:
						return 3 | (data & ~0x7);
				}
				break;
			case WOOD_STAIRS:
			case COBBLESTONE_STAIRS:
			case BRICK_STAIRS:
			case SMOOTH_STAIRS:
			case NETHER_BRICK_STAIRS:
			case SANDSTONE_STAIRS:
			case SPRUCE_WOOD_STAIRS:
			case BIRCH_WOOD_STAIRS:
			case JUNGLE_WOOD_STAIRS:
			case QUARTZ_STAIRS:
				switch (data) {
					case 0:
						return 2;
					case 1:
						return 3;
					case 2:
						return 1;
					case 3:
						return 0;
					case 4:
						return 6;
					case 5:
						return 7;
					case 6:
						return 5;
					case 7:
						return 4;
				}
				break;
			case LEVER:
			case STONE_BUTTON:
			case WOOD_BUTTON:
				int thrown = data & 0x8;
				int withoutThrown = data & ~0x8;
				switch (withoutThrown) {
					case 1:
						return 3 | thrown;
					case 2:
						return 4 | thrown;
					case 3:
						return 2 | thrown;
					case 4:
						return 1 | thrown;
					case 5:
						return 6 | thrown;
					case 6:
						return 5 | thrown;
					case 7:
						return 0 | thrown;
					case 0:
						return 7 | thrown;
				}
				break;
			case WOOD_DOOR:
			case IRON_DOOR:
				if ((data & 0x8) != 0)
					break;
			case COCOA:
			case TRIPWIRE_HOOK:
				int extra = data & ~0x3;
				int withoutFlags = data & 0x3;
				switch (withoutFlags) {
					case 0:
						return 1 | extra;
					case 1:
						return 2 | extra;
					case 2:
						return 3 | extra;
					case 3:
						return 0 | extra;
				}
				break;
			case SIGN_POST:
				return (data + 4) % 16;
			case LADDER:
			case WALL_SIGN:
			case CHEST:
			case FURNACE:
			case BURNING_FURNACE:
			case ENDER_CHEST:
			case TRAPPED_CHEST:
			case HOPPER:
				switch (data) {
					case 2:
						return 5;
					case 3:
						return 4;
					case 4:
						return 2;
					case 5:
						return 3;
				}
				break;
			case DISPENSER:
			case DROPPER:
				int dispPower = data & 0x8;
				switch (data & ~0x8) {
					case 2:
						return 5 | dispPower;
					case 3:
						return 4 | dispPower;
					case 4:
						return 2 | dispPower;
					case 5:
						return 3 | dispPower;
				}
				break;
			case PUMPKIN:
			case JACK_O_LANTERN:
				switch (data) {
					case 0:
						return 1;
					case 1:
						return 2;
					case 2:
						return 3;
					case 3:
						return 0;
				}
				break;
			case HAY_BLOCK:
			case LOG:
				if (data >= 4 && data <= 11)
					data ^= 0xc;
				break;
			case REDSTONE_COMPARATOR_OFF:
			case REDSTONE_COMPARATOR_ON:
			case DIODE_BLOCK_OFF:
			case DIODE_BLOCK_ON:
				int dir = data & 0x03;
				int delay = data - dir;
				switch (dir) {
					case 0:
						return 1 | delay;
					case 1:
						return 2 | delay;
					case 2:
						return 3 | delay;
					case 3:
						return 0 | delay;
				}
				break;
			case TRAP_DOOR:
				int withoutOrientation = data & ~0x3;
				int orientation = data & 0x3;
				switch (orientation) {
					case 0:
						return 3 | withoutOrientation;
					case 1:
						return 2 | withoutOrientation;
					case 2:
						return 0 | withoutOrientation;
					case 3:
						return 1 | withoutOrientation;
				}
				break;
			case PISTON_BASE:
			case PISTON_STICKY_BASE:
			case PISTON_EXTENSION:
				final int rest = data & ~0x7;
				switch (data & 0x7) {
					case 2:
						return 5 | rest;
					case 3:
						return 4 | rest;
					case 4:
						return 2 | rest;
					case 5:
						return 3 | rest;
				}
				break;
			case HUGE_MUSHROOM_1:
			case HUGE_MUSHROOM_2:
				if (data >= 10)
					return data;
				return (data * 3) % 10;
			case VINE:
				return ((data << 1) | (data >> 3)) & 0xf;
			case FENCE_GATE:
				return ((data + 1) & 0x3) | (data & ~0x3);
			case ANVIL:
				return data ^ 0x1;
			case BED:
				return data & ~0x3 | (data + 1) & 0x3;
			case SKULL:
				switch (data) {
					case 2:
						return 5;
					case 3:
						return 4;
					case 4:
						return 2;
					case 5:
						return 3;
				}
			default:
				break;
		}
		return data;
	}
}