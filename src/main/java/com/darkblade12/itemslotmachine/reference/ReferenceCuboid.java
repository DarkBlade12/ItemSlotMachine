package com.darkblade12.itemslotmachine.reference;

import com.darkblade12.itemslotmachine.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ReferenceCuboid {
    private final ReferenceLocation firstVertex;
    private final ReferenceLocation secondVertex;

    public ReferenceCuboid(ReferenceLocation firstVertex, ReferenceLocation secondVertex) {
        this.firstVertex = firstVertex;
        this.secondVertex = secondVertex;
    }

    public static ReferenceCuboid fromCuboid(Location viewPoint, Direction viewDirection, Cuboid cuboid) {
        ReferenceLocation first = ReferenceLocation.fromBukkitLocation(viewPoint, viewDirection, cuboid.getUpperSW());
        ReferenceLocation second = ReferenceLocation.fromBukkitLocation(viewPoint, viewDirection, cuboid.getLowerNE());
        return new ReferenceCuboid(first, second);
    }

    public static ReferenceCuboid fromCuboid(Player viewer, Cuboid cuboid) {
        return fromCuboid(viewer.getLocation(), Direction.getViewDirection(viewer), cuboid);
    }

    public Cuboid toCuboid(Location viewPoint, Direction viewDirection) {
        Location first = firstVertex.toBukkitLocation(viewPoint, viewDirection);
        Location second = secondVertex.toBukkitLocation(viewPoint, viewDirection);
        return new Cuboid(first, second);
    }

    public boolean isValid() {
        return firstVertex != null && secondVertex != null;
    }
}
