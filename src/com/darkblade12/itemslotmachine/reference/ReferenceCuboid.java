package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.util.Cuboid;

public final class ReferenceCuboid {
    private ReferenceLocation firstVertice;
    private ReferenceLocation secondVertice;

    public ReferenceCuboid(ReferenceLocation firstVertice, ReferenceLocation secondVertice) {
        this.firstVertice = firstVertice;
        this.secondVertice = secondVertice;
    }

    public static ReferenceCuboid fromCuboid(Location viewPoint, Direction viewDirection, Cuboid cuboid) {
        ReferenceLocation first = ReferenceLocation.fromBukkitLocation(viewPoint, viewDirection, cuboid.getUpperSW());
        ReferenceLocation second = ReferenceLocation.fromBukkitLocation(viewPoint, viewDirection, cuboid.getLowerNE());
        return new ReferenceCuboid(first, second);
    }

    public static ReferenceCuboid fromCuboid(Player viewer, Cuboid cuboid) {
        return fromCuboid(viewer.getLocation(), Direction.getViewDirection(viewer), cuboid);
    }

    public ReferenceLocation getFirstVertice() {
        return this.firstVertice;
    }

    public ReferenceLocation getSecondVertice() {
        return this.secondVertice;
    }

    public Cuboid getCuboid(Location viewPoint, Direction viewDirection) throws Exception {
        Location first = firstVertice.getBukkitLocation(viewPoint, viewDirection);
        Location second = secondVertice.getBukkitLocation(viewPoint, viewDirection);
        return new Cuboid(first, second);
    }

    public Cuboid getCuboid(Player viewer) throws Exception {
        return getCuboid(viewer.getLocation(), Direction.getViewDirection(viewer));
    }
}