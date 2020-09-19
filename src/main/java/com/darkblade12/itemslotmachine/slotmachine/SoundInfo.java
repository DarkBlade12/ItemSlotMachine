package com.darkblade12.itemslotmachine.slotmachine;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public final class SoundInfo {
    private static final Pattern FORMAT = Pattern.compile("(?i)[a-z_]+(-\\d+(\\.\\d+)?){2}(-(true|false))?");
    private final Sound sound;
    private final float volume;
    private final float pitch;
    private final boolean broadcast;

    public SoundInfo(Sound sound, float volume, float pitch, boolean broadcast) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.broadcast = broadcast;
    }

    public static SoundInfo fromString(String text) throws IllegalArgumentException {
        if (!FORMAT.matcher(text).matches()) {
            throw new IllegalArgumentException("Invalid sound data format.");
        }

        String[] data = text.split("-");
        Sound sound;
        try {
            sound = Sound.valueOf(data[0].toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid sound name.");
        }

        float volume;
        try {
            volume = Float.parseFloat(data[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid volume number.");
        }

        float pitch;
        try {
            pitch = Float.parseFloat(data[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid pitch number.");
        }

        boolean broadcast = data.length <= 3 || Boolean.parseBoolean(data[3]);
        return new SoundInfo(sound, volume, pitch, broadcast);
    }

    public void play(Location location) {
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("World of location cannot be null.");
        }

        world.playSound(location, sound, volume, pitch);
    }

    public void play(Player player, Location location) {
        player.playSound(location, sound, volume, pitch);
    }

    public boolean isBroadcast() {
        return broadcast;
    }
}
