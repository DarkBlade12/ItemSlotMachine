package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.cuboid.Cuboid;

public final class ReferenceCuboid {
    private ReferenceLocation firstLocation;
    private ReferenceLocation secondLocation;

    public ReferenceCuboid(ReferenceLocation firstLocation, ReferenceLocation secondLocation) {
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;
    }

    public ReferenceLocation getFirstLocation() {
        return this.firstLocation;
    }

    public ReferenceLocation getSecondLocation() {
        return this.secondLocation;
    }

    public Cuboid getCuboid(Location viewPoint, Direction viewDirection) throws Exception {
        return new Cuboid(firstLocation.getBukkitLocation(viewPoint, viewDirection),
                secondLocation.getBukkitLocation(viewPoint, viewDirection));
    }

    public Cuboid getCuboid(Player viewer) throws Exception {
        return getCuboid(viewer.getLocation(), Direction.getViewDirection(viewer));
    }
}