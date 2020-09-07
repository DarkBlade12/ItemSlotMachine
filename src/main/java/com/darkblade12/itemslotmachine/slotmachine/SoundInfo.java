package com.darkblade12.itemslotmachine.slotmachine;

import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundInfo {
    private static final Pattern FORMAT = Pattern.compile("(?i)[a-z_]+(-\\d+(\\.\\d+)?){2}(-(true|false))?");
    private Sound sound;
    private float volume;
    private float pitch;
    private boolean broadcast;

    public SoundInfo(Sound sound, float volume, float pitch, boolean broadcast) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.broadcast = broadcast;
    }

    public static SoundInfo fromString(String text) throws IllegalArgumentException {
        if (!FORMAT.matcher(text).matches()) {
            throw new IllegalArgumentException("Invalid text format");
        }

        String[] data = text.split("-");
        Sound sound;
        try {
            sound = Sound.valueOf(data[0].toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid sound name");
        }

        float volume;
        try {
            volume = Float.parseFloat(data[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid volume number");
        }

        float pitch;
        try {
            pitch = Float.parseFloat(data[2]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid pitch number");
        }

        boolean broadcast = data.length > 3 ? Boolean.parseBoolean(data[3]) : true;
        return new SoundInfo(sound, volume, pitch, broadcast);
    }

    public void play(Player player, Location location) {
        if (broadcast) {
            location.getWorld().playSound(location, sound, volume, pitch);
        } else {
            player.playSound(location, sound, volume, pitch);
        }
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isBroadcast() {
        return broadcast;
    }
}