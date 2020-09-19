package com.darkblade12.itemslotmachine.design;

import com.darkblade12.itemslotmachine.Settings;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.reference.ReferenceBlock;
import com.darkblade12.itemslotmachine.reference.ReferenceCuboid;
import com.darkblade12.itemslotmachine.reference.ReferenceItemFrame;
import com.darkblade12.itemslotmachine.util.Cuboid;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Design implements Nameable {
    public static final String DEFAULT_NAME = "default";
    public static final String DEFAULT_FILE = "design_default.json";
    public static final String FILE_EXTENSION = ".json";
    private String name;
    private Set<ReferenceBlock> blocks;
    private ReferenceItemFrame[] itemFrames;
    private ReferenceBlock sign, slot;
    private ReferenceCuboid region;
    private Direction initialDirection;

    public Design(String name, Set<ReferenceBlock> blocks, ReferenceItemFrame[] itemFrames, ReferenceBlock sign, ReferenceBlock slot,
                  ReferenceCuboid region, Direction initialDirection) {
        this.name = name;
        this.blocks = blocks;
        this.itemFrames = itemFrames;
        this.sign = sign;
        this.slot = slot;
        this.region = region;
        this.initialDirection = initialDirection;
    }

    public static Design create(Player player, Cuboid cuboid, String name) throws DesignIncompleteException {
        Set<ReferenceBlock> blocks = new HashSet<>();
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
            String ending = missingFrames == 1 ? "" : "s";
            throw new DesignIncompleteException("The design is missing %d item frame%s.", missingFrames, ending);
        } else if (sign == null) {
            throw new DesignIncompleteException("The design is missing a pot sign.");
        } else if (slot == null) {
            throw new DesignIncompleteException("The design is missing a slot (jukebox).");
        }

        return new Design(name, blocks, itemFrames, sign, slot, region, direction);
    }

    public static Design fromFile(File file) throws IOException, JsonParseException {
        return FileUtils.readJson(file, Design.class);
    }

    public static Design fromFile(String path) throws IOException, JsonParseException {
        return FileUtils.readJson(new File(path), Design.class);
    }

    public static void convert(JsonObject design) throws DesignIncompleteException {
        JsonObject region = design.getAsJsonObject("region");
        JsonElement firstVertex = region.remove("firstVertice");
        JsonElement secondVertex = region.remove("secondVertice");
        if (firstVertex.isJsonNull() || secondVertex.isJsonNull()) {
            throw new DesignIncompleteException("The design is missing region vertices.");
        }

        region.add("firstVertex", firstVertex);
        region.add("secondVertex", secondVertex);
    }

    public void invertItemFrames() {
        ReferenceItemFrame temp = itemFrames[0];
        itemFrames[0] = itemFrames[2];
        itemFrames[2] = temp;
    }

    public void build(Location viewPoint, Direction viewDirection, Settings settings) throws DesignBuildException {
        Cuboid cuboid = region.toCuboid(viewPoint, viewDirection);
        if (settings.isSpaceCheckEnabled()) {
            List<Material> ignoredTypes = settings.getSpaceCheckIgnoredTypes();
            for (Block block : cuboid) {
                Material material = block.getType();
                if (material != Material.AIR && !ignoredTypes.contains(material)) {
                    throw new DesignBuildException("There is not enough space for this design.");
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
        } catch (Exception ex) {
            dismantle(viewPoint, viewDirection);
            throw new DesignBuildException("Failed to place blocks and item frames.", ex);
        }
    }

    public void build(Player player, Settings settings) throws Exception {
        build(player.getLocation(), Direction.getViewDirection(player), settings);
    }

    public void dismantle(Location viewPoint, Direction viewDirection) {
        for (ReferenceItemFrame refFrame : itemFrames) {
            ItemFrame frame = refFrame.toBukkitItemFrame(viewPoint, viewDirection);

            if (frame != null) {
                frame.remove();
            }
        }

        sign.toBukkitBlock(viewPoint, viewDirection).setType(Material.AIR);
        slot.toBukkitBlock(viewPoint, viewDirection).setType(Material.AIR);

        for (ReferenceBlock refBlock : blocks) {
            refBlock.toBukkitBlock(viewPoint, viewDirection).setType(Material.AIR);
        }
    }

    public void saveFile(File directory) throws IOException {
        FileUtils.saveJson(new File(directory, getFileName()), this);
    }

    public void deleteFile(File directory) throws IOException {
        File file = new File(directory, getFileName());
        if (!file.exists()) {
            return;
        }

        Files.delete(file.toPath());
    }

    public void reloadFile(File directory) throws IOException, JsonIOException, JsonSyntaxException {
        Design design = fromFile(new File(directory, getFileName()));
        name = design.name;
        blocks = design.blocks;
        itemFrames = design.itemFrames;
        sign = design.sign;
        slot = design.slot;
        region = design.region;
        initialDirection = design.initialDirection;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getFileName() {
        return name + FILE_EXTENSION;
    }

    public ReferenceItemFrame[] getItemFrames() {
        return itemFrames;
    }

    public ReferenceBlock getSign() {
        return sign;
    }

    public ReferenceBlock getSlot() {
        return slot;
    }

    public ReferenceCuboid getRegion() {
        return region;
    }

    public boolean isDefault() {
        return name.equals(DEFAULT_NAME);
    }
}
