package com.darkblade12.itemslotmachine.slotmachine.sound;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SoundList extends ArrayList<SoundData> {
    private static final long serialVersionUID = 2932421195026187475L;
    private static final String FORMAT = "[A-Z_]+(-\\d+(\\.\\d+)?){2}(, [A-Z_]+(-\\d+(\\.\\d+)?){2})*";

    public SoundList() {
        super();
    }

    public SoundList(Collection<SoundData> sounds) {
        super(sounds);
    }

    public static SoundList fromString(String s) throws IllegalArgumentException {
        if (!s.matches(FORMAT)) {
            throw new IllegalArgumentException("has an invalid format");
        }

        SoundList list = new SoundList();
        for (String data : s.split(", ")) {
            list.add(SoundData.fromString(data));
        }
        return list;
    }

    public void play(Location location) {
        for (int i = 0; i < size(); i++) {
            get(i).play(location);
        }
    }

    public void play(Player player, Location location) {
        for (int i = 0; i < size(); i++) {
            get(i).play(player, location);
        }
    }

    public void play(Player player) {
        play(player, player.getLocation());
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (text.length() > 0) {
                text.append(", ");
            }

            text.append(get(i).toString());
        }
        return text.toString();
    }
}