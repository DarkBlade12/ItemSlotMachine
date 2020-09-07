package com.darkblade12.itemslotmachine.util;

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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FireworkRocket {
    private static Random RANDOM = new Random();
    private static Color[] COLORS;
    private FireworkMeta meta;

    static {
        DyeColor[] values = DyeColor.values();
        COLORS = new Color[values.length];
        for (int i = 0; i < values.length; i++) {
            COLORS[i] = values[i].getFireworkColor();
        }
    }

    public FireworkRocket(FireworkMeta meta) {
        this(meta, false);
    }

    private FireworkRocket(FireworkMeta meta, boolean isClean) {
        if (isClean) {
            this.meta = meta;
        } else {
            this.meta = getCleanMeta();
            this.meta.setPower(meta.getPower());
            this.meta.addEffects(meta.getEffects());
        }
    }

    public FireworkRocket() {
        this(getCleanMeta());
    }

    public static FireworkRocket randomize() {
        FireworkMeta meta = getCleanMeta();
        meta.setPower(RANDOM.nextInt(3) + 1);
        meta.addEffects(randomizeEffects(1, 3, 1, 5));
        return new FireworkRocket(meta, true);
    }

    public static FireworkRocket fromItemStack(ItemStack item) {
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : null;
        if (item.getType() != Material.FIREWORK_ROCKET || meta == null || !(meta instanceof FireworkMeta))
            throw new IllegalArgumentException("This ItemStack is not a firework");
        return new FireworkRocket((FireworkMeta) meta);
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
        for (int i = 0; i < calculateRandom(min, max); i++) {
            Color color = COLORS[RANDOM.nextInt(COLORS.length)];
            if (!colors.contains(color)) {
                colors.add(color);
            }
        }
        return colors;
    }

    public static Type randomizeType() {
        Type[] types = Type.values();
        return types[RANDOM.nextInt(types.length)];
    }

    public static List<FireworkEffect> randomizeEffects(int min, int max, int minColors, int maxColors) {
        List<FireworkEffect> effects = new ArrayList<FireworkEffect>();
        for (int i = 0; i < calculateRandom(min, max); i++) {
            boolean flicker = RANDOM.nextBoolean();
            boolean trail = RANDOM.nextBoolean();
            List<Color> colors = randomizeColors(minColors, maxColors);
            List<Color> fadeColors = randomizeColors(minColors, maxColors);
            effects.add(FireworkEffect.builder().with(randomizeType()).flicker(flicker).trail(trail).withColor(colors)
                    .withFade(fadeColors).build());
        }
        return effects;
    }

    public Firework launch(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        firework.setFireworkMeta(meta);
        return firework;
    }

    public void displayEffects(Plugin plugin, Location location) {
        final Firework firework = launch(location);
        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(plugin, 1);
    }

    public boolean isSimilar(FireworkRocket rocket) {
        return meta.equals(rocket.getMeta());
    }

    public static FireworkMeta getCleanMeta() {
        return (FireworkMeta) new ItemStack(Material.FIREWORK_ROCKET).getItemMeta();
    }

    public FireworkMeta getMeta() {
        return this.meta;
    }

    public boolean hasEffects() {
        return meta.getEffects().size() > 0;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItem(int amount) {
        ItemStack item = getItem();
        item.setAmount(amount);
        return item;
    }
}