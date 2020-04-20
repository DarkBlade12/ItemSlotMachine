package com.darkblade12.itemslotmachine.design;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.reference.ReferenceBlock;
import com.darkblade12.itemslotmachine.reference.ReferenceCuboid;
import com.darkblade12.itemslotmachine.reference.ReferenceItemFrame;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.util.Cuboid;
import com.darkblade12.itemslotmachine.util.FileUtils;

public final class Design implements Nameable {
    public static final File DIRECTORY = new File("plugins/ItemSlotMachine/designs/");
    public static final String DEFAULT_NAME = "default";
    private String name;
    private Set<ReferenceBlock> blocks;
    private ReferenceItemFrame[] itemFrames;
    private ReferenceBlock sign, slot;
    private ReferenceCuboid region;
    private Direction initialDirection;

    public Design(String name, Set<ReferenceBlock> blocks, ReferenceItemFrame[] itemFrames, ReferenceBlock sign,
                  ReferenceBlock slot, ReferenceCuboid region, Direction initialDirection) {
        this.name = name;
        this.blocks = blocks;
        this.itemFrames = itemFrames;
        this.sign = sign;
        this.slot = slot;
        this.region = region;
        this.initialDirection = initialDirection;
    }

    public static Design create(Player player, Cuboid cuboid, String name) throws Exception {
        Set<ReferenceBlock> blocks = new HashSet<ReferenceBlock>();
        ReferenceItemFrame[] itemFrames = new ReferenceItemFrame[3];
        ReferenceBlock sign = null, slot = null;
        ReferenceCuboid region = ReferenceCuboid.fromCuboid(player, cuboid);
        Direction direction = Direction.getViewDirection(player);
        int frameIndex = 0;

        for (Block block : cuboid) {
            Material material = block.getType();

            if (block.getState() instanceof Sign && sign == null) {
                sign = ReferenceBlock.fromBukkitBlock(player, block);
            } else if (material == Material.JUKEBOX && slot == null) {
                slot = ReferenceBlock.fromBukkitBlock(player, block);
            } else if (material != Material.AIR) {
                blocks.add(ReferenceBlock.fromBukkitBlock(player, block));
            } else if (frameIndex < 3) {
                ItemFrame frame = ReferenceItemFrame.findItemFrame(block.getLocation());

                if (frame != null) {
                    itemFrames[frameIndex++] = ReferenceItemFrame.fromBukkitItemFrame(player, frame);
                }
            }
        }

        
        if (frameIndex < 3) {
            int missingFrames = 3 - frameIndex;
            throw new Exception(missingFrames + " item frame" + (missingFrames == 1 ? " is" : "s are") + " missing");
        } else if (sign == null) {
            throw new Exception("The sign is missing");
        } else if (slot == null) {
            throw new Exception("The slot is missing (Jukebox block)");
        }

        return new Design(name, blocks, itemFrames, sign, slot, region, direction);
    }

    public static String getPath(String name) {
        return DIRECTORY.getPath() + "/" + name + ".json";
    }

    public static Design fromFile(String name) throws Exception {
        return FileUtils.readJson(getPath(name), Design.class);
    }

    public void invertItemFrames() throws IOException {
        ReferenceItemFrame temp = itemFrames[0];
        itemFrames[0] = itemFrames[2];
        itemFrames[2] = temp;
        
        saveToFile();
    }

    public void build(Location viewPoint, Direction viewDirection) throws Exception {
        Cuboid cuboid = region.getCuboid(viewPoint, viewDirection);

        if (Settings.isSpaceCheckEnabled()) {
            for (Block block : cuboid) {
                Material material = block.getType();

                if (material != Material.AIR && !Settings.isBlockIgnored(material)) {
                    throw new Exception("There is not enough space for this design");
                }
            }
        }

        try {
            for (ReferenceBlock refBlock : blocks) {
                refBlock.place(viewPoint, viewDirection);
            }

            sign.place(viewPoint, viewDirection);
            slot.place(viewPoint, viewDirection);

            for (ReferenceItemFrame refFrame : itemFrames) {
                refFrame.place(viewPoint, viewDirection);
            }
        } catch (Exception e) {
            destruct(viewPoint, viewDirection);

            throw e;
        }
    }

    public void build(Player player) throws Exception {
        build(player.getLocation(), Direction.getViewDirection(player));
    }

    public void destruct(Location viewPoint, Direction viewDirection) {
        for (ReferenceItemFrame refFrame : itemFrames) {
            ItemFrame frame = refFrame.getBukkitItemFrame(viewPoint, viewDirection);

            if (frame != null) {
                frame.remove();
            }
        }

        sign.getBukkitBlock(viewPoint, viewDirection).setType(Material.AIR);
        slot.getBukkitBlock(viewPoint, viewDirection).setType(Material.AIR);

        for (ReferenceBlock refBlock : blocks) {
            refBlock.getBukkitBlock(viewPoint, viewDirection).setType(Material.AIR);
        }
    }

    public void destruct(Player player) {
        destruct(player.getLocation(), Direction.getViewDirection(player));
    }

    public void saveToFile() throws IOException {
        FileUtils.saveJson(getPath(), this);
    }

    public void deleteFile() {
        File file = new File(getPath());

        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getPath() {
        return getPath(name);
    }

    public Set<ReferenceBlock> getBlocks() {
        return this.blocks;
    }

    public ReferenceItemFrame[] getItemFrames() {
        return this.itemFrames;
    }

    public ReferenceBlock getSign() {
        return this.sign;
    }

    public ReferenceBlock getSlot() {
        return this.slot;
    }

    public ReferenceCuboid getRegion() {
        return this.region;
    }

    public Direction getInitialDirection() {
        return this.initialDirection;
    }
}