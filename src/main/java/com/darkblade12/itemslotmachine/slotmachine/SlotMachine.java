package com.darkblade12.itemslotmachine.slotmachine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Settings;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.hook.VaultHook;
import com.darkblade12.itemslotmachine.core.replacer.Placeholder;
import com.darkblade12.itemslotmachine.core.replacer.Replacer;
import com.darkblade12.itemslotmachine.core.settings.InvalidValueException;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.design.DesignBuildException;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.reference.ReferenceBlock;
import com.darkblade12.itemslotmachine.reference.ReferenceItemFrame;
import com.darkblade12.itemslotmachine.reference.ReferenceLocation;
import com.darkblade12.itemslotmachine.slotmachine.combo.Action;
import com.darkblade12.itemslotmachine.slotmachine.combo.AmountAction;
import com.darkblade12.itemslotmachine.slotmachine.combo.Combo;
import com.darkblade12.itemslotmachine.slotmachine.combo.CommandAction;
import com.darkblade12.itemslotmachine.slotmachine.combo.ItemAction;
import com.darkblade12.itemslotmachine.statistic.Category;
import com.darkblade12.itemslotmachine.statistic.PlayerStatistic;
import com.darkblade12.itemslotmachine.statistic.SlotMachineStatistic;
import com.darkblade12.itemslotmachine.util.Cuboid;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.darkblade12.itemslotmachine.util.FireworkRocket;
import com.darkblade12.itemslotmachine.util.ItemUtils;
import com.darkblade12.itemslotmachine.util.MessageUtils;
import com.darkblade12.itemslotmachine.util.SafeLocation;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public final class SlotMachine implements Nameable {
    public static final String FILE_EXTENSION = ".json";
    public static final String TEMPLATE_FILE = "template.yml";
    private static final Random RANDOM = new Random();
    private static final Placeholder<String> USER = new Placeholder<>("<user>");
    private static final Placeholder<Double> MONEY = new Placeholder<>("<money>");
    private static final Placeholder<String> CURRENCY = new Placeholder<>("<currency>");
    private static final Placeholder<Integer> ITEM_AMOUNT = new Placeholder<>("<item_amount>");
    private static final Placeholder<String> ITEMS = new Placeholder<>("<items>");
    private static final Placeholder<String> SLOT_MACHINE = new Placeholder<>("<slot_machine>");
    private String name;
    private Design design;
    private SafeLocation buildLocation;
    private Direction buildDirection;
    private double moneyPot;
    private List<ItemStack> itemPot;
    private transient ItemSlotMachine plugin;
    private transient SlotMachineSettings settings;
    private transient boolean broken;
    private transient UUID userId;
    private transient long lockEnd;
    private transient BukkitTask task;
    private transient boolean spinning;
    private transient boolean stopped;

    private SlotMachine(ItemSlotMachine plugin, String name, Design design, SafeLocation buildLocation,
                        Direction buildDirection) {
        this.plugin = plugin;
        this.name = name;
        this.design = design;
        this.buildLocation = buildLocation;
        this.buildDirection = buildDirection;
        settings = new SlotMachineSettings(plugin, name);
    }

    public static SlotMachine create(ItemSlotMachine plugin, String name, Design design,
                                     Player viewer) throws DesignBuildException, IOException {
        SafeLocation buildLocation = SafeLocation.fromBukkitLocation(viewer.getLocation());
        Direction buildDirection = Direction.getViewDirection(viewer);
        design.build(viewer.getLocation(), buildDirection, plugin.getSettings());

        File from = new File(plugin.getDataFolder(), TEMPLATE_FILE);
        File to = new File(plugin.slotMachineManager.getDataDirectory(), name + ".yml");
        Files.createParentDirs(to);
        Files.copy(from, to);

        SlotMachine slot = new SlotMachine(plugin, name, design, buildLocation, buildDirection);
        slot.settings.load();
        slot.moneyPot = slot.settings.moneyPotDefault;
        slot.itemPot = ItemUtils.cloneItems(slot.settings.itemPotDefault);
        slot.saveAndUpdate();
        return slot;
    }

    public static SlotMachine fromFile(ItemSlotMachine plugin, File file) throws IOException, JsonIOException,
                                                                          JsonSyntaxException, InvalidValueException {
        SlotMachine slot = FileUtils.readJson(file, SlotMachine.class);
        slot.plugin = plugin;
        slot.settings = new SlotMachineSettings(plugin, slot.name);
        slot.settings.load();
        return slot;
    }

    public static SlotMachine fromFile(ItemSlotMachine plugin, String path) throws IOException, JsonIOException,
                                                                            JsonSyntaxException, InvalidValueException {
        return fromFile(plugin, new File(path));
    }

    private void playSounds(SoundInfo[] sounds) {
        Location location = design.getSlot().getBukkitLocation(getLocation(), buildDirection);
        for (SoundInfo sound : sounds) {
            sound.play(getUser(), location);
        }
    }

    private boolean isWin(Material[] pattern) {
        if (pattern[0] == pattern[1] && pattern[1] == pattern[2]) {
            return true;
        }

        for (Combo combo : settings.combos) {
            if (combo.isActivated(pattern)) {
                return true;
            }
        }

        return false;
    }

    private Material generateSymbol() {
        return settings.symbolTypes[RANDOM.nextInt(settings.symbolTypes.length)];
    }

    private Material[] generatePattern() {
        if (settings.winningChance > 0) {
            boolean forceWin = RANDOM.nextDouble() * 100 <= settings.winningChance;
            List<Material> pool1 = Lists.newArrayList(settings.symbolTypes);
            while (pool1.size() > 0) {
                Material[] pattern = new Material[3];
                pattern[0] = pool1.remove(RANDOM.nextInt(pool1.size()));
                List<Material> pool2 = Lists.newArrayList(settings.symbolTypes);
                while (pool2.size() > 0) {
                    pattern[1] = pool2.remove(RANDOM.nextInt(pool2.size()));
                    List<Material> pool3 = Lists.newArrayList(settings.symbolTypes);
                    while (pool3.size() > 0) {
                        pattern[2] = pool3.remove(RANDOM.nextInt(pool3.size()));
                        if (forceWin && isWin(pattern) || !forceWin && !isWin(pattern)) {
                            return pattern;
                        }
                    }
                }
            }
        }

        return new Material[] { generateSymbol(), generateSymbol(), generateSymbol() };
    }

    private void raisePot() {
        if (settings.moneyPotEnabled) {
            moneyPot += settings.moneyPotRaise;
        }
        if (settings.itemPotEnabled) {
            ItemUtils.stackItems(itemPot, settings.itemPotRaise);
        }
        try {
            saveAndUpdate();
        } catch (IOException ex) {
            plugin.logException("Failed to save data of slot machine {1}: {0}", ex, name);
        }
    }

    public void spin(final Player user) {
        final ItemFrame[] frames = getItemFrames();
        broken = getSign() == null || frames == null;
        if (broken) {
            plugin.sendMessage(user, Message.SLOT_MACHINE_BROKEN, name);
            return;
        }

        spinning = true;
        stopped = false;
        userId = user.getUniqueId();
        raisePot();
        removeCoins(user);

        PlayerStatistic userStat = plugin.statisticManager.getPlayerStatistic(user, true);
        userStat.getRecord(Category.TOTAL_SPINS).increaseValue(1);
        if (user.getGameMode() != GameMode.CREATIVE) {
            userStat.getRecord(Category.SPENT_COINS).increaseValue(settings.coinAmount);
        }
        plugin.statisticManager.trySave(userStat);

        SlotMachineStatistic slotStat = plugin.statisticManager.getSlotMachineStatistic(this, true);
        if (slotStat != null) {
            slotStat.getRecord(Category.TOTAL_SPINS).increaseValue(1);
            plugin.statisticManager.trySave(slotStat);
        }

        final Material[] result = generatePattern();
        task = new BukkitRunnable() {
            private int spins = 0;
            private int stoppedAt = -1;

            @Override
            public void run() {
                playSounds(settings.spinSounds);
                if (settings.reelStop > 0 && spins == settings.reelStop) {
                    stopped = true;
                }

                if (stopped && stoppedAt == -1) {
                    stoppedAt = spins;
                }

                for (int i = 0; i < frames.length; i++) {
                    int remaining = stoppedAt == -1 ? 1 : stoppedAt + settings.reelDelay[i] - spins;
                    if (!stopped || remaining >= 0) {
                        Material symbol = remaining == 0 ? result[i] : generateSymbol();
                        frames[i].setItem(new ItemStack(symbol));
                    } else if (i == frames.length - 1) {
                        cancel();
                        Material[] pattern = new Material[frames.length];
                        for (int j = 0; j < frames.length; j++) {
                            pattern[j] = frames[j].getItem().getType();
                        }
                        endSpin(pattern);
                    }
                }

                updateSign();
                spins++;
            }
        }.runTaskTimer(plugin, 5, 5);
    }

    private void endSpin(Material[] pattern) {
        double moneyPrize = 0;
        List<ItemStack> itemPrize = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        boolean payOutMoneyPot = false;
        boolean payOutItemPot = false;
        for (Combo combo : settings.combos) {
            if (!combo.isActivated(pattern)) {
                continue;
            }

            for (Action action : combo.getActions()) {
                switch (action.getType()) {
                    case PAY_OUT_ITEMS:
                        ItemUtils.stackItems(itemPrize, ((ItemAction) action).getItems());
                        break;
                    case PAY_OUT_ITEM_POT:
                        payOutItemPot = true;
                        break;
                    case PAY_OUT_MONEY:
                        moneyPrize = ((AmountAction) action).getAmount();
                        break;
                    case PAY_OUT_MONEY_POT:
                        payOutMoneyPot = true;
                        break;
                    case EXECUTE_COMMAND:
                        commands.add(((CommandAction) action).getCommand());
                        break;
                    case MULTIPLY_ITEM_POT:
                        double amount = ((AmountAction) action).getAmount();
                        for (ItemStack item : itemPot) {
                            item.setAmount((int) Math.round(item.getAmount() * amount));
                        }
                        break;
                    case MULTIPLY_MONEY_POT:
                        moneyPot *= ((AmountAction) action).getAmount();
                        break;
                    case RAISE_ITEM_POT:
                        ItemUtils.stackItems(itemPot, ((ItemAction) action).getItems());
                        break;
                    case RAISE_MONEY_POT:
                        moneyPot += ((AmountAction) action).getAmount();
                        break;
                    default:
                        /* Unsupported combo action */
                        break;
                }
            }
        }

        if (pattern[0] == pattern[1] && pattern[1] == pattern[2]) {
            payOutMoneyPot = true;
            payOutItemPot = true;
        }

        if (payOutMoneyPot && isMoneyPotEnabled()) {
            moneyPrize += moneyPot;
            resetMoneyPot();
        }
        if (payOutItemPot && settings.itemPotEnabled) {
            ItemUtils.stackItems(itemPrize, itemPot);
            resetItemPot();
        }

        if (moneyPrize == 0 && itemPrize.size() == 0 && commands.size() == 0) {
            SlotMachineStatistic slotStat = plugin.statisticManager.getSlotMachineStatistic(this, true);
            if (slotStat != null) {
                slotStat.getRecord(Category.LOST_SPINS).increaseValue(1);
                plugin.statisticManager.trySave(slotStat);
            }

            Player user = getUser();
            PlayerStatistic userStat = plugin.statisticManager.getPlayerStatistic(user, true);
            if (userStat != null) {
                userStat.getRecord(Category.LOST_SPINS).increaseValue(1);
                plugin.statisticManager.trySave(userStat);
            }

            playSounds(settings.loseSounds);
            plugin.sendMessage(user, Message.SLOT_MACHINE_LOST);
        } else {
            commands.addAll(Arrays.asList(settings.winCommands));
            payOut(moneyPrize, itemPrize, commands);
        }

        if (settings.lockTime > 0) {
            lockEnd = System.currentTimeMillis() + settings.lockTime * 1000;
        }
        spinning = false;
    }

    private void payOut(double moneyPrize, List<ItemStack> itemPrize, List<String> commands) {
        playSounds(settings.winSounds);
        if (settings.launchFireworks) {
            Location slotLocation = design.getSlot().getBukkitLocation(getLocation(), buildDirection);
            FireworkRocket.randomize().displayEffects(plugin, slotLocation.add(0.5, 2, 0.5));
        }

        SlotMachineStatistic slotStat = plugin.statisticManager.getSlotMachineStatistic(this, true);
        if (slotStat != null) {
            slotStat.getRecord(Category.WON_SPINS).increaseValue(1);
            plugin.statisticManager.trySave(slotStat);
        }

        Player user = getUser();
        PlayerStatistic userStat = plugin.statisticManager.getPlayerStatistic(user, true);
        if (userStat != null) {
            userStat.getRecord(Category.WON_SPINS).increaseValue(1);
        }

        StringBuilder prizeText = new StringBuilder();
        if (moneyPrize > 0) {
            if (settings.moneyPotHouseCut > 0) {
                moneyPrize *= 1.0 - settings.moneyPotHouseCut / 100.0;
            }

            if (userStat != null) {
                userStat.getRecord(Category.WON_MONEY).increaseValue(moneyPrize);
            }

            VaultHook vault = plugin.getVaultHook();
            vault.depositPlayer(Bukkit.getOfflinePlayer(userId), moneyPrize);
            prizeText.append(ChatColor.YELLOW).append(moneyPrize).append(vault.getCurrencyName(moneyPrize, true));
        }
        if (itemPrize.size() > 0) {
            ItemUtils.giveItems(user, itemPrize);

            if (userStat != null) {
                userStat.getRecord(Category.WON_ITEMS).increaseValue(itemPrize.size());
            }

            if (prizeText.length() > 0) {
                prizeText.append(" ").append(ChatColor.GOLD).append(plugin.formatMessage(Message.WORD_AND)).append(" ");
            }
            prizeText.append(MessageUtils.toString(itemPrize));
        }

        if (userStat != null) {
            plugin.statisticManager.trySave(userStat);
        }

        if (commands.size() > 0) {
            executeCommands(commands, moneyPrize, itemPrize);
        }
        plugin.sendMessage(user, Message.SLOT_MACHINE_WON, prizeText.toString());
    }

    private void executeCommands(List<String> commands, double moneyPrize, List<ItemStack> itemPrize) {
        Replacer replacer = createReplacer(moneyPrize, itemPrize);
        CommandSender console = Bukkit.getConsoleSender();
        for (String command : commands) {
            Bukkit.dispatchCommand(console, replacer.replaceAll(command));
        }
    }

    public boolean hasEnoughCoins(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        int remaining = settings.coinAmount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && plugin.coinManager.isCoin(item)) {
                if (remaining == 0 || item.getAmount() >= remaining) {
                    return true;
                }
                remaining -= item.getAmount();
            }
        }

        return false;
    }

    private void removeCoins(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        int remaining = settings.coinAmount;
        ItemStack[] invContents = player.getInventory().getContents();
        for (int i = 0; i < invContents.length; i++) {
            ItemStack item = invContents[i];
            if (item != null && plugin.coinManager.isCoin(item)) {
                int amount = item.getAmount();
                if (amount > remaining) {
                    item.setAmount(amount - remaining);
                    break;
                } else if (amount == remaining) {
                    item.setType(Material.AIR);
                    break;
                } else if (remaining > amount) {
                    item.setType(Material.AIR);
                    remaining -= amount;
                    if (remaining == 0) {
                        break;
                    }
                }
                invContents[i] = item;
            }
        }
        player.getInventory().setContents(invContents);
    }

    public void setMoneyPot(double moneyPot) {
        this.moneyPot = moneyPot;
        try {
            saveAndUpdate();
        } catch (IOException ex) {
            plugin.logException("Failed to save money pot of slot machine {1}: {0}", ex, name);
        }
    }

    public void clearMoneyPot() {
        setMoneyPot(0);
    }

    public void resetMoneyPot() {
        setMoneyPot(settings.moneyPotDefault);
    }

    public void depositMoney(double money) {
        setMoneyPot(moneyPot + money);
    }

    public void withdrawMoney(double money) {
        setMoneyPot(moneyPot < money ? 0 : moneyPot - money);
    }

    public void setItemPot(Collection<ItemStack> itemPot) {
        this.itemPot = ItemUtils.cloneItems(itemPot);
        try {
            saveAndUpdate();
        } catch (IOException ex) {
            plugin.logException("Failed to save item pot of slot machine {1}: {0}", ex, name);
        }
    }

    public void setItemPot(ItemStack... itemPot) {
        setItemPot(Arrays.asList(itemPot));
    }

    public void clearItemPot() {
        itemPot.clear();
        try {
            saveAndUpdate();
        } catch (IOException ex) {
            plugin.logException("Failed to save item pot of slot machine {1}: {0}", ex, name);
        }
    }

    public void resetItemPot() {
        setItemPot(settings.itemPotDefault);
    }

    public void addItems(Collection<ItemStack> items) {
        ItemUtils.stackItems(itemPot, items);
        try {
            saveAndUpdate();
        } catch (IOException ex) {
            plugin.logException("Failed to save item pot of slot machine {1}: {0}", ex, name);
        }
    }

    public void stop(boolean instant) {
        if (!instant) {
            stopped = true;
            return;
        }

        if (task != null) {
            task.cancel();
        }
        spinning = false;
        lockEnd = 0;
        userId = null;
    }

    public void stop() {
        stop(false);
    }

    public void delete() throws SecurityException {
        stop(true);
        deleteFile();
        settings.file.delete();
        plugin.statisticManager.deleteSlotMachineStatistic(this);
        design.dismantle(getLocation(), buildDirection);
    }

    public void rebuild() throws DesignBuildException {
        stop(true);

        Location location = getLocation();
        design.dismantle(location, buildDirection);
        design.build(location, buildDirection, plugin.getSettings());

        updateSign();
        broken = false;
    }

    public void reload() throws SlotMachineException {
        stop(true);
        try {
            SlotMachine slot = fromFile(plugin, new File(plugin.slotMachineManager.getDataDirectory(), getFileName()));
            name = slot.name;
            design = slot.design;
            buildLocation = slot.buildLocation;
            buildDirection = slot.buildDirection;
            moneyPot = slot.moneyPot;
            itemPot = slot.itemPot;
            settings = slot.settings;
            updateSign();
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            throw new SlotMachineException("Failed to read slot machine data", ex);
        } catch (InvalidValueException ex) {
            throw new SlotMachineException("Failed to load settings", ex);
        }
    }

    public void saveAndUpdate() throws IOException {
        saveFile();
        updateSign();
    }

    public void saveFile() throws IOException {
        FileUtils.saveJson(new File(plugin.slotMachineManager.getDataDirectory(), getFileName()), this);
    }

    public void deleteFile() throws SecurityException {
        File file = new File(plugin.slotMachineManager.getDataDirectory(), getFileName());
        if (file.exists()) {
            file.delete();
        }
    }

    public void move(BlockFace direction, int amount) throws SlotMachineException {
        stop(true);
        Location oldLocation = getLocation();
        int offsetX = direction.getModX() * amount;
        int offsetY = direction.getModY() * amount;
        int offsetZ = direction.getModZ() * amount;
        Location newLocation = oldLocation.clone().add(offsetX, offsetY, offsetZ);
        Settings settings = plugin.getSettings();

        try {
            design.dismantle(oldLocation, buildDirection);
            design.build(newLocation, buildDirection, settings);
            buildLocation = SafeLocation.fromBukkitLocation(newLocation);
            saveAndUpdate();
        } catch (DesignBuildException | IOException ex) {
            design.dismantle(newLocation, buildDirection);
            buildLocation = SafeLocation.fromBukkitLocation(oldLocation);
            try {
                design.build(oldLocation, buildDirection, settings);
            } catch (DesignBuildException ex2) {
                /* should not occur */
            }
            updateSign();
            throw new SlotMachineException("Failed to build the design at the new location", ex);
        }
    }

    public void teleport(Player player, int range) throws SlotMachineException {
        boolean flying = player.isFlying();
        Location location = getLocation();
        ReferenceBlock slotBlock = design.getSlot();

        for (int offsetL = 0; offsetL <= range / 2; offsetL++) {
            for (int offsetF = 1; offsetF <= range; offsetF++) {
                List<ReferenceLocation> possibleLocs = Lists.newArrayList(slotBlock.add(-offsetL, -offsetF, 0));
                if (offsetL > 0) {
                    possibleLocs.add(slotBlock.add(offsetL, -offsetF, 0));
                }

                for (ReferenceLocation possible : possibleLocs) {
                    Block block = possible.getBukkitBlock(location, buildDirection);
                    Material above = block.getRelative(BlockFace.UP).getType();
                    Material below = block.getRelative(BlockFace.DOWN).getType();
                    if (!block.getType().isSolid() && !above.isSolid() && (below.isSolid() || flying)) {
                        Location teleportLoc = block.getLocation().add(0.5, 0, 0.5);
                        teleportLoc.setYaw(buildDirection.getOrdinal() * 90);
                        player.teleport(teleportLoc);
                        return;
                    }
                }
            }
        }

        throw new SlotMachineException("No suitable teleport location found");
    }

    public void teleport(Player player) throws SlotMachineException {
        teleport(player, 4);
    }

    public void updateSign() {
        Sign sign = getSign();
        if (sign == null) {
            return;
        }

        String[] lines;
        if (settings.moneyPotEnabled ^ settings.itemPotEnabled) {
            lines = new String[] { "", createSpacer(), createSpacer(), createSpacer() };
            if (settings.moneyPotEnabled) {
                lines[0] = plugin.formatMessage(Message.SIGN_POT_MONEY, moneyPot);
            } else {
                lines[0] = plugin.formatMessage(Message.SIGN_POT_ITEMS, itemPot.size());
            }
        } else {
            String moneyText = plugin.formatMessage(Message.SIGN_POT_MONEY, moneyPot);
            String itemsText = plugin.formatMessage(Message.SIGN_POT_ITEMS, itemPot.size());
            lines = new String[] { moneyText, createSpacer(), itemsText, createSpacer() };
        }
        lines = MessageUtils.prepareSignLines(lines, 0, 2);
        for (int i = 0; i < lines.length; i++) {
            sign.setLine(i, lines[i]);
        }
        sign.update(true);
    }

    private String createSpacer() {
        return plugin.formatMessage(Message.SIGN_POT_SPACER, MessageUtils.randomColorCode());
    }

    private Replacer createReplacer(double money, List<ItemStack> items) {
        String currency = plugin.getVaultHook().getCurrencyName(money == 1);
        Replacer replacer = Replacer.builder().with(USER, getUserName()).with(MONEY, money).with(CURRENCY, currency)
                .with(ITEM_AMOUNT, items.size()).with(ITEMS, MessageUtils.toString(items)).with(SLOT_MACHINE, name).build();
        return replacer;
    }

    private Sign getSign() {
        Block block = design.getSign().getBukkitBlock(getLocation(), buildDirection);
        BlockState state = block.getState();
        if (state == null || !(state instanceof Sign)) {
            return null;
        }
        return (Sign) state;
    }

    private ItemFrame[] getItemFrames() {
        ItemFrame[] frames = new ItemFrame[3];
        Location location = getLocation();
        ReferenceItemFrame[] frameRefs = design.getItemFrames();
        for (int i = 0; i < frameRefs.length; i++) {
            ItemFrame frame = frameRefs[i].getBukkitItemFrame(location, buildDirection);
            if (frame == null) {
                return null;
            }
            frames[i] = frame;
        }

        return frames;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return name + FILE_EXTENSION;
    }

    public Location getLocation() {
        return buildLocation.toBukkitLocation();
    }

    public boolean isInsideRegion(Location location) {
        Cuboid region = design.getRegion().getCuboid(getLocation(), buildDirection);
        return region.isInside(location);
    }

    public boolean isInteraction(Location location) {
        return design.getSlot().getBukkitLocation(getLocation(), buildDirection).equals(location);
    }

    public SlotMachineSettings getSettings() {
        return settings;
    }

    public double getMoneyPot() {
        return moneyPot;
    }

    public boolean isMoneyPotEnabled() {
        return settings.moneyPotEnabled && plugin.getVaultHook().isEconomyEnabled();
    }

    public boolean isBroken() {
        return broken;
    }

    public Player getUser() {
        return userId == null ? null : Bukkit.getPlayer(userId);
    }

    public String getUserName() {
        return getUser().getName();
    }

    public boolean isUser(Player player) {
        return userId != null && player.getUniqueId().equals(userId);
    }

    public long getLockEnd() {
        return lockEnd;
    }

    public int getRemainingLockTime() {
        return (int) (lockEnd - System.currentTimeMillis()) / 1000;
    }

    public boolean isLockExpired() {
        return System.currentTimeMillis() > lockEnd;
    }

    public boolean isSpinning() {
        return spinning;
    }

    public boolean isStopped() {
        return stopped;
    }

    public boolean isStoppable(Player player) {
        return spinning && !stopped && settings.reelStop <= 0 && isUser(player);
    }

    public boolean hasModifyPermission(Player player) {
        return player.hasPermission("itemslotmachine.slot.modify." + name) || Permission.SLOT_MODIFY_ALL.has(player);
    }

    public boolean hasUsePermission(Player player) {
        String permission = Permission.SLOT_USE.getNode();
        if (settings.individualPermission) {
            permission += "." + name;
        }
        return player.hasPermission(permission) || Permission.SLOT_USE_ALL.has(player);
    }
}