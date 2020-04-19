package com.darkblade12.itemslotmachine.design;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.cuboid.Cuboid;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.reference.ReferenceBlock;
import com.darkblade12.itemslotmachine.reference.ReferenceCuboid;
import com.darkblade12.itemslotmachine.reference.ReferenceItemFrame;
import com.darkblade12.itemslotmachine.reference.ReferenceLocation;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Design implements Nameable {
    public static final File DIRECTORY = new File("plugins/ItemSlotMachine/designs/");
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

    public static Design create(Player p, Cuboid c, String name) throws Exception {
        Set<ReferenceBlock> blocks = new HashSet<ReferenceBlock>();
        ReferenceItemFrame[] itemFrames = new ReferenceItemFrame[3];
        ReferenceBlock sign = null, slot = null;
        ReferenceCuboid region = new ReferenceCuboid(ReferenceLocation.fromBukkitLocation(p, c.getUpperSW()),
                ReferenceLocation.fromBukkitLocation(p, c.getLowerNE()));
        Direction direction = Direction.getViewDirection(p);
        int f = 0;
        for (Block b : c) {
            Material m = b.getType();
            if (b.getState() instanceof Sign && sign == null)
                sign = ReferenceBlock.fromBukkitBlock(p, b);
            else if (m == Material.JUKEBOX && slot == null)
                slot = ReferenceBlock.fromBukkitBlock(p, b);
            else if (m != Material.AIR)
                blocks.add(ReferenceBlock.fromBukkitBlock(p, b));
            else if (f < 3) {
                ItemFrame i = ReferenceItemFrame.findItemFrame(b.getLocation());
                if (i != null) {
                    itemFrames[f] = ReferenceItemFrame.fromBukkitItemFrame(p, i);
                    f++;
                }
            }
        }
        if (f < 3) {
            int m = 3 - f;
            throw new Exception(m + " item frame" + (m == 1 ? " is" : "s are") + " missing");
        } else if (sign == null)
            throw new Exception("The sign is missing");
        else if (slot == null)
            throw new Exception("The slot is missing (Jukebox block)");
        return new Design(name, blocks, itemFrames, sign, slot, region, direction);
    }

    public static String getPath(String name) {
        return DIRECTORY.getPath() + "/" + name + ".json";
    }

    public static Design fromString(String json) {
        Gson gson = new Gson();
        Design design = gson.fromJson(json, Design.class);

        return design;
    }

    public static Design fromFile(String name) throws Exception {
        File file = new File(getPath(name));

        if (!file.exists()) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder json = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            json.append(line + "\n");
        }

        reader.close();
        return fromString(json.toString());
    }

    public void invertItemFrames() {
        ReferenceItemFrame r = itemFrames[0];
        itemFrames[0] = itemFrames[2];
        itemFrames[2] = r;
        saveToFile();
    }

    public void build(Location c, Direction d) throws Exception {
        if (Settings.isSpaceCheckEnabled())
            for (Block b : region.getCuboid(c, d)) {
                Material m = b.getType();
                if (m != Material.AIR && !Settings.isBlockIgnored(m))
                    throw new Exception("There is not enough space for this design");
            }
        for (ReferenceBlock r : blocks)
            r.place(c, d);
        sign.place(c, d);
        slot.place(c, d);
        for (ReferenceItemFrame r : itemFrames)
            r.place(c, d);
    }

    public void build(Player p) throws Exception {
        build(p.getLocation(), Direction.getViewDirection(p));
    }

    public void destruct(Location c, Direction d) {
        for (ReferenceItemFrame r : itemFrames) {
            ItemFrame i = r.getBukkitItemFrame(c, d);
            if (i != null)
                i.remove();
        }
        sign.getBukkitBlock(c, d).setType(Material.AIR);
        slot.getBukkitBlock(c, d).setType(Material.AIR);
        for (ReferenceBlock r : blocks)
            r.getBukkitBlock(c, d).setType(Material.AIR);
    }

    public void destruct(Player p) {
        destruct(p.getLocation(), Direction.getViewDirection(p));
    }

    public boolean saveToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdirs();
        }

        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(getPath()));
            writer.write(gson.toJson(this));
            writer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteFile() {
        new File(getPath()).delete();
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