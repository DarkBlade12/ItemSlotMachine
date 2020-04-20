package com.darkblade12.itemslotmachine.slotmachine.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundData {
    private static final String FORMAT = "[A-Z_]+(-\\d+(\\.\\d+)?){2}";
    private Sound sound;
    private float volume;
    private float pitch;

    public SoundData(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static SoundData fromString(String s) throws IllegalArgumentException {
        if (!s.matches(FORMAT)) {
            throw new IllegalArgumentException("has an invalid format");
        }
        
        String[] data = s.split("-");
        Sound sound;
        try {
            sound = Sound.valueOf(data[0]);
        } catch (Exception e) {
            throw new IllegalArgumentException("contains an invalid sound name");
        }
        
        return new SoundData(sound, Float.parseFloat(data[1]), Float.parseFloat(data[2]));
    }

    public void play(Location location) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public void play(Player player, Location location) {
        player.playSound(location, sound, volume, pitch);
    }

    public void play(Player player) {
        play(player, player.getLocation());
    }

    public Sound getSound() {
        return this.sound;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public String toString() {
        return sound.name() + "-" + volume + "-" + pitch;
    }
}