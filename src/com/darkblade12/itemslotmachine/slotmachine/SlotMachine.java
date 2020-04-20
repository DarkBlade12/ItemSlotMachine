package com.darkblade12.itemslotmachine.slotmachine;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.hook.VaultHook;
import com.darkblade12.itemslotmachine.nameable.Nameable;
import com.darkblade12.itemslotmachine.reader.CompressedStringReader;
import com.darkblade12.itemslotmachine.reference.Direction;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.combo.Action;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.ItemPotCombo;
import com.darkblade12.itemslotmachine.slotmachine.combo.types.MoneyPotCombo;
import com.darkblade12.itemslotmachine.slotmachine.sound.SoundList;
import com.darkblade12.itemslotmachine.statistic.Category;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.statistic.types.PlayerStatistic;
import com.darkblade12.itemslotmachine.util.ItemList;
import com.darkblade12.itemslotmachine.util.Rocket;
import com.darkblade12.itemslotmachine.util.SafeLocation;

public final class SlotMachine extends SlotMachineBase implements Nameable {
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
        design.build(viewer);
        SafeLocation viewerLoc = SafeLocation.fromBukkitLocation(viewer.getLocation());
        String data = design.getName() + "#" + viewerLoc + "#" + Direction.getViewDirection(viewer).name();
        new CompressedStringReader(name + ".instance", "plugins/ItemSlotMachine/slot machines/").saveToFile(data);
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

    private boolean trySaveStatistic(Statistic statistic) {
        try {
            statistic.saveToFile();
            return true;
        } catch (Exception e) {
            String type = statistic instanceof PlayerStatistic ? "player" : "slot machine";
            plugin.logWarning("Failed to save " + type + " statistic '" + statistic.getName() + "'!");
            if (Settings.isDebugModeEnabled()) {
                e.printStackTrace();
            }

            return false;
        }
    }

    public void activate(final Player user) {
        final ItemFrame[] frames = getItemFrameInstances();
        broken = !(getSign() != null && frames != null);
        if (broken) {
            user.sendMessage(plugin.messageManager.slot_machine_broken());
            return;
        }

        final ItemStack[] icons = generateIcons();
        if (icons == null) {
            user.sendMessage(plugin.messageManager.slot_machine_broken());
            return;
        }

        insertCoins(user);
        PlayerStatistic userStat = plugin.statisticManager.getStatistic(user, true);
        userStat.getRecord(Category.TOTAL_SPINS).increaseValue(1);
        if (user.getGameMode() != GameMode.CREATIVE) {
            userStat.getRecord(Category.COINS_SPENT).increaseValue(activationAmount);
        }
        trySaveStatistic(userStat);

        statistic.getRecord(Category.TOTAL_SPINS).increaseValue(1);
        trySaveStatistic(statistic);

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
                    if (halted ? delayTicks[i] != haltTickDelay[i] : true) {
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

        statistic.getRecord(Category.WON_SPINS).increaseValue(1);
        trySaveStatistic(statistic);

        Player user = getUser();
        PlayerStatistic userStat = plugin.statisticManager.getStatistic(user, true);
        userStat.getRecord(Category.WON_SPINS).increaseValue(1);
        if (moneyPrize > 0) {
            moneyPrize = applyHouseCut(moneyPrize);
            userStat.getRecord(Category.WON_MONEY).increaseValue(moneyPrize);
            VaultHook.depositPlayer(Bukkit.getOfflinePlayer(userId), moneyPrize);
        }
        if (itemPrize.size() > 0) {
            itemPrize = applyHouseCut(itemPrize);
            userStat.getRecord(Category.WON_ITEMS).increaseValue(itemPrize.size());
            itemPrize.distribute(user);
        }
        trySaveStatistic(userStat);

        if (executeCommands) {
            executeCommands(userName, moneyPrize, itemPrize);
        }

        user.sendMessage(plugin.messageManager.slot_machine_won(moneyPrize, itemPrize));
    }

    private void distribute(ItemStack... display) {
        MoneyPotCombo moneyCombo = getMoneyPotCombosEnabled() ? moneyPotCombos.getActivated(display) : null;
        ItemPotCombo itemCombo = getItemPotCombosEnabled() ? itemPotCombos.getActivated(display) : null;

        if (display[0].isSimilar(display[1]) && display[1].isSimilar(display[2])) {
            double moneyPrize = 0;
            if (moneyPotEnabled && VaultHook.isEnabled()) {
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
            statistic.getRecord(Category.LOST_SPINS).increaseValue(1);
            trySaveStatistic(statistic);

            Player user = getUser();
            PlayerStatistic userStat = plugin.statisticManager.getStatistic(user, true);
            userStat.getRecord(Category.LOST_SPINS).increaseValue(1);
            trySaveStatistic(userStat);

            playLoseSounds();
            user.sendMessage(plugin.messageManager.slot_machine_lost());
        }

        deactivate(false);
    }

    public void destruct() {
        deactivate();
        design.destruct(center.getBukkitLocation(), initialDirection);
        instanceReader.deleteFile();
        statistic.deleteFile();
        configReader.deleteConfig();
    }

    public void rebuild() throws Exception {
        deactivate();
        
        Location l = center.getBukkitLocation();
        design.destruct(l, initialDirection);
        design.build(l, initialDirection);
        
        updateSign();
        broken = false;
    }

    @Override
    public void move(BlockFace face, int amount) throws Exception {
        deactivate();
        super.move(face, amount);
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