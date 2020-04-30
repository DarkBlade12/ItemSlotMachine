package com.darkblade12.itemslotmachine.slotmachine;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.hook.VaultHook;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.design.DesignBuildException;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reader.CompressedStringReader;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.slotmachine.combo.Action;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.ItemPotCombo;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.MoneyPotCombo;
import com.darkblade12.itemslotmachine.slotmachine.sound.SoundList;
import com.darkblade12.itemslotmachine.statistic.Category;
import com.darkblade12.itemslotmachine.statistic.PlayerStatistic;
import com.darkblade12.itemslotmachine.statistic.SlotMachineStatistic;
import com.darkblade12.itemslotmachine.util.ItemList;
import com.darkblade12.itemslotmachine.util.MessageUtils;
import com.darkblade12.itemslotmachine.util.Rocket;
import com.darkblade12.itemslotmachine.util.SafeLocation;

public final class SlotMachine extends SlotMachineBase implements Nameable {
    public static final String FILE_EXTENSION = ".instance";
    private boolean broken;
    private String userName;
    private UUID userId;
    private long lockEnd;
    private BukkitTask task;
    private boolean active;
    private boolean halted;

    private SlotMachine(ItemSlotMachine plugin, String name) throws Exception {
        super(plugin, name);
    }

    public static SlotMachine create(ItemSlotMachine plugin, String name, Design design, Player viewer) throws Exception {
        design.build(viewer, plugin.getSettings());
        SafeLocation viewerLoc = SafeLocation.fromBukkitLocation(viewer.getLocation());
        String data = design.getName() + "#" + viewerLoc + "#" + Direction.getViewDirection(viewer).name();
        String directoryPath = plugin.slotMachineManager.getDataDirectory().getPath();
        new CompressedStringReader(name + ".instance", directoryPath).saveToFile(data);
        return load(plugin, name);
    }

    public static SlotMachine load(ItemSlotMachine plugin, String name) throws Exception {
        return new SlotMachine(plugin, name);
    }

    private void playSounds(SoundList sounds, boolean broadcast) {
        Location slotLoc = slot.getBukkitLocation();
        if (broadcast) {
            sounds.play(slotLoc);
        } else {
            sounds.play(getUser(), slotLoc);
        }
    }

    private void playTickingSounds() {
        if (tickingSoundsEnabled) {
            playSounds(tickingSounds, tickingSoundsBroadcast);
        }
    }

    private void playWinSounds() {
        if (winSoundsEnabled) {
            playSounds(winSounds, winSoundsBroadcast);
        }
    }

    private void playLoseSounds() {
        if (loseSoundsEnabled) {
            playSounds(loseSounds, loseSoundsBroadcast);
        }
    }

    private void playWinEffect() {
        playWinSounds();
        if (fireworksEnabled)
            Rocket.randomize().displayEffects(plugin, slot.getBukkitLocation().add(0.5, 2, 0.5));
    }

    public void activate(final Player user) {
        final ItemFrame[] frames = getItemFrameInstances();
        broken = !(getSign() != null && frames != null);
        if (broken) {
            plugin.sendMessage(user, Message.SLOT_MACHINE_BROKEN, name);
            return;
        }

        final ItemStack[] icons = generateIcons();
        if (icons == null) {
            plugin.sendMessage(user, Message.SLOT_MACHINE_BROKEN, name);
            return;
        }

        insertCoins(user);
        PlayerStatistic userStat = plugin.statisticManager.getPlayerStatistic(user, true);
        userStat.getRecord(Category.TOTAL_SPINS).increaseValue(1);
        if (user.getGameMode() != GameMode.CREATIVE) {
            userStat.getRecord(Category.SPENT_COINS).increaseValue(activationAmount);
        }
        plugin.statisticManager.trySave(userStat);

        SlotMachineStatistic slotStat = plugin.statisticManager.getSlotMachineStatistic(this, true);
        if (slotStat != null) {
            slotStat.getRecord(Category.TOTAL_SPINS).increaseValue(1);
            plugin.statisticManager.trySave(slotStat);
        }

        raisePot();
        userName = user.getName();
        userId = user.getUniqueId();
        task = new BukkitRunnable() {
            private int[] ticks = new int[3];
            private int[] delayTicks = new int[3];

            @Override
            public void run() {
                playTickingSounds();

                if (automaticHaltEnabled && ticks[0] == automaticHaltTicks) {
                    halted = true;
                }

                for (int i = 0; i < 3; i++) {
                    if (!halted || delayTicks[i] != haltTickDelay[i]) {
                        if (delayTicks[i] == haltTickDelay[i] - 1 || automaticHaltEnabled && !halted && haltTickDelay[i] == 0
                                && ticks[i] == automaticHaltTicks - 1) {
                            frames[i].setItem(icons[i]);
                        } else {
                            frames[i].setItem(getRandomIcon());
                        }

                        if (halted) {
                            delayTicks[i]++;
                        } else {
                            ticks[i]++;
                        }
                    } else if (i == 2) {
                        distribute(frames[0].getItem(), frames[1].getItem(), frames[2].getItem());
                    }
                }

                updateSign();
            }
        }.runTaskTimer(plugin, 5, 5);
        active = true;
    }

    public void halt() {
        halted = true;
    }

    private void deactivate(boolean manual) {
        active = false;
        halted = false;

        if (task != null) {
            task.cancel();
        }
        if (!manual && playerLockEnabled) {
            lockEnd = System.currentTimeMillis() + playerLockTime * 1000;
        } else {
            lockEnd = 0;
            userName = null;
            userId = null;
        }
    }

    public void deactivate() {
        deactivate(true);
    }

    private double applyHouseCut(double money) {
        return moneyPotHouseCutEnabled
                ? money - (moneyPotHouseCutPercentage ? money * (moneyPotHouseCutAmount / 100.0D) : moneyPotHouseCutAmount)
                : money;
    }

    private ItemList applyHouseCut(ItemList items) {
        if (itemPotHouseCutEnabled) {
            items.removeRandom(itemPotHouseCutAmount);
        }
        return items;
    }

    private void handleWin(double moneyPrize, ItemList itemPrize, boolean executeCommands) {
        playWinEffect();

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
            moneyPrize = applyHouseCut(moneyPrize);

            if (userStat != null) {
                userStat.getRecord(Category.WON_MONEY).increaseValue(moneyPrize);
            }

            VaultHook vault = plugin.getVaultHook();
            vault.depositPlayer(Bukkit.getOfflinePlayer(userId), moneyPrize);
            prizeText.append(ChatColor.YELLOW).append(moneyPrize).append(vault.getCurrencyName(moneyPrize, true));
        }
        if (itemPrize.size() > 0) {
            itemPrize = applyHouseCut(itemPrize);
            itemPrize.distribute(user);

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

        if (executeCommands) {
            executeCommands(userName, moneyPrize, itemPrize);
        }
        plugin.sendMessage(user, Message.SLOT_MACHINE_WON, prizeText.toString());
    }

    private void distribute(ItemStack... icons) {
        MoneyPotCombo moneyCombo = getMoneyPotCombosEnabled() ? moneyPotCombos.getActivated(icons) : null;
        ItemPotCombo itemCombo = getItemPotCombosEnabled() ? itemPotCombos.getActivated(icons) : null;

        if (icons[0].isSimilar(icons[1]) && icons[1].isSimilar(icons[2])) {
            double moneyPrize = 0;
            if (moneyPotEnabled && plugin.getVaultHook().isEnabled()) {
                moneyPrize = moneyPot;

                if (moneyCombo != null) {
                    switch (moneyCombo.getAction()) {
                        case MULTIPLY_POT_AND_DISTRIBUTE:
                            moneyPrize *= moneyCombo.getAmount();
                            break;
                        case ADD_TO_POT_AND_DISTRIBUTE:
                            moneyPrize += moneyCombo.getAmount();
                            break;
                        default:
                            break;
                    }
                }

                resetMoneyPot();
            }

            ItemList itemPrize = new ItemList();
            if (itemPotEnabled) {
                itemPrize = itemPot.clone();

                if (itemCombo != null) {
                    switch (itemCombo.getAction()) {
                        case ADD_TO_POT_AND_DISTRIBUTE:
                            itemPrize.addAll(itemCombo.getItems());
                            break;
                        case DOUBLE_POT_ITEMS_AND_DISTRIBUTE:
                            itemPrize.doubleAmounts();
                            break;
                        default:
                            break;
                    }
                }

                resetItemPot();
            }

            handleWin(moneyPrize, itemPrize, true);
        } else if (moneyCombo != null) {
            double moneyPrize = moneyPot;
            switch (moneyCombo.getAction()) {
                case MULTIPLY_POT_AND_DISTRIBUTE:
                    moneyPrize *= moneyCombo.getAmount();
                    break;
                case ADD_TO_POT_AND_DISTRIBUTE:
                    moneyPrize += moneyCombo.getAmount();
                    break;
                case DISTRIBUTE_INDEPENDENT_MONEY:
                    moneyPrize = moneyCombo.getAmount();
                    break;
                default:
                    break;
            }
            if (moneyCombo.getAction() != Action.DISTRIBUTE_INDEPENDENT_MONEY) {
                resetMoneyPot();
            }

            handleWin(moneyPrize, new ItemList(), false);
        } else if (itemCombo != null) {
            ItemList itemPrize = itemPot.clone();
            switch (itemCombo.getAction()) {
                case ADD_TO_POT_AND_DISTRIBUTE:
                    itemPrize.addAll(itemCombo.getItems());
                    break;
                case DOUBLE_POT_ITEMS_AND_DISTRIBUTE:
                    itemPrize.doubleAmounts();
                    break;
                case DISTRIBUTE_INDEPENDENT_ITEMS:
                    itemPrize = itemCombo.getItems();
                    break;
                default:
                    break;
            }
            if (itemCombo.getAction() != Action.DISTRIBUTE_INDEPENDENT_ITEMS) {
                resetItemPot();
            }

            handleWin(0, itemPrize, false);
        } else {
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

            playLoseSounds();
            plugin.sendMessage(user, Message.SLOT_MACHINE_LOST);
        }

        deactivate(false);
    }

    public void delete() throws SecurityException {
        // TODO: Rollback on failure
        deactivate();
        design.dismantle(center.getBukkitLocation(), initialDirection);
        instanceReader.deleteFile();
        plugin.statisticManager.deleteSlotMachineStatistic(this);
        configReader.deleteConfig();
    }

    public void rebuild() throws DesignBuildException {
        deactivate();

        Location centerLoc = center.getBukkitLocation();
        design.dismantle(centerLoc, initialDirection);
        design.build(centerLoc, initialDirection, plugin.getSettings());

        updateSign();
        broken = false;
    }

    @Override
    public void move(BlockFace direction, int amount) throws DesignBuildException {
        deactivate();
        super.move(direction, amount);
    }

    public boolean isBroken() {
        return this.broken;
    }

    public String getUserName() {
        return this.userName;
    }

    public Player getUser() {
        return userId == null ? null : Bukkit.getPlayer(userId);
    }

    public boolean isUser(Player p) {
        return userName == null ? false : p.getName().equals(userName);
    }

    public long getLockEnd() {
        return this.lockEnd;
    }

    public int getRemainingLockTime() {
        return (int) (lockEnd - System.currentTimeMillis()) / 1000;
    }

    public boolean isLockExpired() {
        return System.currentTimeMillis() > lockEnd;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isHalted() {
        return this.halted;
    }

    public boolean isPermittedToHalt(Player p) {
        return active && !halted && !automaticHaltEnabled && isUser(p);
    }
}