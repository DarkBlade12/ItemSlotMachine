package com.darkblade12.itemslotmachine.coin;

import org.bukkit.Location;

import com.darkblade12.itemslotmachine.util.SafeLocation;

public class ShopInfo {
    private SafeLocation location;
    private int coins;

    public ShopInfo(SafeLocation location, int coins) {
        this.location = location;
        this.coins = coins;
    }

    public SafeLocation getLocation() {
        return location;
    }

    public Location getBukkitLocation() {
        return location.toBukkitLocation();
    }

    public int getCoins() {
        return coins;
    }

    public void setLocation(SafeLocation location) {
        this.location = location;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
