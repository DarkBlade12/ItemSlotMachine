package com.darkblade12.itemslotmachine.rocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.reader.CompressedStringReader;

public class Rocket {
	private static Random RANDOM = new Random();
	private static Color[] COLORS;
	private FireworkMeta meta;

	static {
		DyeColor[] values = DyeColor.values();
		COLORS = new Color[values.length];
		for (int i = 0; i < values.length; i++)
			COLORS[i] = values[i].getFireworkColor();
	}

	public Rocket(FireworkMeta meta) {
		this(meta, false);
	}

	private Rocket(FireworkMeta meta, boolean isClean) {
		if (isClean) {
			this.meta = meta;
		} else {
			this.meta = getCleanMeta();
			this.meta.setPower(meta.getPower());
			this.meta.addEffects(meta.getEffects());
		}
	}

	public Rocket() {
		this(getCleanMeta());
	}

	public static Rocket randomize() {
		FireworkMeta meta = getCleanMeta();
		meta.setPower(RANDOM.nextInt(3) + 1);
		meta.addEffects(randomizeEffects(1, 3, 1, 5));
		return new Rocket(meta, true);
	}

	public static Rocket fromItemStack(ItemStack i) {
		ItemMeta meta = i.hasItemMeta() ? i.getItemMeta() : null;
		if (i.getType() != Material.FIREWORK || meta == null || !(meta instanceof FireworkMeta))
			throw new IllegalArgumentException("This ItemStack is not a firework");
		return new Rocket((FireworkMeta) meta);
	}

	public static Rocket fromString(String s) throws Exception {
		try {
			FireworkMeta meta = getCleanMeta();
			String[] p = s.split("@");
			meta.setPower(Integer.parseInt(p[0]));
			String[] e = p[1].split("#");
			for (String effect : e) {
				String[] d = effect.split(",");
				String[] c = d[3].split("~");
				List<Color> colors = new ArrayList<Color>();
				List<Color> fadeColors = new ArrayList<Color>();
				for (int i = 0; i < c.length; i++)
					for (String rgb : c[i].split("-")) {
						Color col = Color.fromRGB(Integer.parseInt(rgb));
						if (i == 0)
							colors.add(col);
						else
							fadeColors.add(col);
					}
				meta.addEffect(FireworkEffect.builder().flicker(Boolean.parseBoolean(d[0])).trail(Boolean.parseBoolean(d[1])).with(Type.valueOf(d[2])).withColor(colors).withFade(fadeColors).build());
			}
			return new Rocket(meta, true);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid Rocket format");
		}
	}

	public static Rocket fromFile(String name) throws Exception {
		return fromString(new CompressedStringReader(name + ".rckt", "plugins/UltimateRockets/rockets/").readFromFile());
	}

	private static int calculateRandom(int min, int max) throws IllegalArgumentException {
		if (min < 0)
			throw new IllegalArgumentException("Min value can not be lower than 0");
		else if (max < 1)
			throw new IllegalArgumentException("Min value can not be lower than 1");
		else if (max < min)
			throw new IllegalArgumentException("Max value can not be lower than min value");
		return min == max ? min : RANDOM.nextInt((max - min) + 1) + min;
	}

	public static List<Color> randomizeColors(int min, int max) {
		List<Color> colors = new ArrayList<Color>();
		for (int a = 1; a <= calculateRandom(min, max); a++) {
			Color c = COLORS[RANDOM.nextInt(COLORS.length)];
			if (!colors.contains(c))
				colors.add(c);
		}
		return colors;
	}

	public static List<FireworkEffect> randomizeEffects(int min, int max, int minColors, int maxColors) {
		List<FireworkEffect> effects = new ArrayList<FireworkEffect>();
		for (int a = 1; a <= calculateRandom(min, max); a++)
			effects.add(FireworkEffect.builder().flicker(RANDOM.nextBoolean()).with(Type.values()[RANDOM.nextInt(Type.values().length)]).trail(RANDOM.nextBoolean()).withColor(randomizeColors(minColors, maxColors)).withFade(randomizeColors(minColors, maxColors)).build());
		return effects;
	}

	public Firework launch(Location l) {
		Firework f = l.getWorld().spawn(l, Firework.class);
		f.setFireworkMeta(meta);
		return f;
	}

	public void displayEffects(ItemSlotMachine plugin, Location l) {
		final Firework f = launch(l);
		new BukkitRunnable() {
			@Override
			public void run() {
				f.detonate();
			}
		}.runTaskLater(plugin, 1);
	}

	public boolean saveToFile(String name) {
		return new CompressedStringReader(name + ".rckt", "plugins/UltimateRockets/rockets/").saveToFile(toString());
	}

	public void deleteFile(String name) {
		new CompressedStringReader(name + ".rckt", "plugins/UltimateRockets/rockets/").deleteFile();
	}

	public boolean isSimilar(Rocket r) {
		return meta.equals(r.getMeta());
	}

	public static FireworkMeta getCleanMeta() {
		return (FireworkMeta) new ItemStack(Material.FIREWORK).getItemMeta();
	}

	public FireworkMeta getMeta() {
		return this.meta;
	}

	public boolean hasEffects() {
		return meta.getEffects().size() > 0;
	}

	public ItemStack getItem() {
		ItemStack i = new ItemStack(Material.FIREWORK);
		i.setItemMeta(meta);
		return i;
	}

	public ItemStack getItem(int amount) {
		ItemStack i = getItem();
		i.setAmount(amount);
		return i;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(meta.getPower() + "@");
		int e = 1;
		for (FireworkEffect f : meta.getEffects()) {
			s.append(f.hasFlicker() + "," + f.hasTrail() + "," + f.getType().name() + ",");
			int a = 1;
			for (Color c : f.getColors()) {
				s.append(c.asRGB() + (a == f.getColors().size() ? f.getFadeColors().size() > 0 ? "~" : "" : "-"));
				a++;
			}
			int b = 1;
			for (Color c : f.getFadeColors()) {
				s.append(c.asRGB() + (b == f.getFadeColors().size() ? "" : "-"));
				b++;
			}
			if (e != meta.getEffectsSize())
				s.append("#");
			e++;
		}
		return s.toString();
	}
}