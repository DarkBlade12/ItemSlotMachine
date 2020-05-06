package com.darkblade12.itemslotmachine.slotmachine;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.settings.InvalidValueException;
import com.darkblade12.itemslotmachine.core.settings.SettingsBase;
import com.darkblade12.itemslotmachine.slotmachine.combo.Action;
import com.darkblade12.itemslotmachine.slotmachine.combo.Combo;
import com.darkblade12.itemslotmachine.util.ItemUtils;
import com.google.common.primitives.Ints;

public class SlotMachineSettings extends SettingsBase<ItemSlotMachine> {
    File file;
    int coinAmount;
    Material[] symbolTypes;
    boolean allowCreative;
    String individualPermission;
    boolean launchFireworks;
    int reelStop;
    int[] reelDelay;
    double winningChance;
    int lockTime;
    String[] winCommands;
    SoundInfo[] spinSounds;
    SoundInfo[] winSounds;
    SoundInfo[] loseSounds;
    boolean moneyPotEnabled;
    double moneyPotDefault;
    double moneyPotRaise;
    double moneyPotHouseCut;
    boolean itemPotEnabled;
    ItemStack[] itemPotDefault;
    ItemStack[] itemPotRaise;
    Combo[] combos;

    public SlotMachineSettings(ItemSlotMachine plugin, File file) {
        super(plugin);
        this.file = file;
    }

    public SlotMachineSettings(ItemSlotMachine plugin, String name) {
        this(plugin, new File(plugin.slotMachineManager.getDataDirectory(), name + ".yml"));
    }

    @Override
    public void load() throws InvalidValueException {
        config = YamlConfiguration.loadConfiguration(file);
        coinAmount = config.getInt(Setting.COIN_AMOUNT.getPath(), 1);
        if (coinAmount < 1) {
            throw new InvalidValueException("The value of setting {0} cannot be lower than 0.", Setting.COIN_AMOUNT);
        }

        symbolTypes = convertMaterials(Setting.SYMBOL_TYPES, 2, 0, false);
        allowCreative = config.getBoolean(Setting.ALLOW_CREATIVE.getPath(), true);
        launchFireworks = config.getBoolean(Setting.LAUNCH_FIREWORKS.getPath(), true);
        individualPermission = config.getString(Setting.INDIVIDUAL_PERMISSION.getPath());

        reelStop = config.getInt(Setting.REEL_STOP.getPath());
        List<Integer> reelDelayList = config.getIntegerList(Setting.REEL_DELAY.getPath());
        if (reelDelayList.size() != 3) {
            throw new InvalidValueException("The list size of setting {0} must be 3.", Setting.REEL_DELAY);
        }
        reelDelay = Ints.toArray(reelDelayList);

        winningChance = config.getDouble(Setting.WINNING_CHANCE.getPath());
        if (winningChance > 100) {
            throw new InvalidValueException("The value of setting {0} cannot be higher than 100.", Setting.WINNING_CHANCE);
        }
        lockTime = config.getInt(Setting.LOCK_TIME.getPath());
        List<String> winCommandsList = config.getStringList(Setting.WIN_COMMANDS.getPath());
        winCommands = new String[winCommandsList.size()];
        for (int i = 0; i < winCommands.length; i++) {
            String command = winCommandsList.get(i);
            if (command.startsWith("/")) {
                if (command.length() == 1) {
                    throw new InvalidValueException("A list value of setting {0} contains the invalid command {1}.",
                            Setting.WIN_COMMANDS, command);
                }
                winCommands[i] = command.substring(1);
            } else {
                winCommands[i] = command;
            }
        }

        spinSounds = convertSounds(Setting.SOUNDS_SPIN);
        winSounds = convertSounds(Setting.SOUNDS_WIN);
        loseSounds = convertSounds(Setting.SOUNDS_LOSE);

        moneyPotEnabled = config.getBoolean(Setting.MONEY_POT_ENABLED.getPath());
        if (moneyPotEnabled) {
            moneyPotDefault = config.getDouble(Setting.MONEY_POT_DEFAULT.getPath());
            if (moneyPotDefault < 0) {
                throw new InvalidValueException("The value of setting {0} cannot be lower than 0.", Setting.MONEY_POT_DEFAULT);
            }

            moneyPotRaise = config.getDouble(Setting.MONEY_POT_RAISE.getPath());
            if (moneyPotRaise < 0) {
                throw new InvalidValueException("The value of setting {0} cannot be lower than 0.", Setting.MONEY_POT_RAISE);
            }

            moneyPotHouseCut = config.getDouble(Setting.MONEY_POT_HOUSE_CUT.getPath());
            if (moneyPotHouseCut == 100) {
                throw new InvalidValueException("The percentage value of setting {0} cannot be equal to 100.",
                        Setting.MONEY_POT_HOUSE_CUT);
            } else if (moneyPotHouseCut > 100) {
                throw new InvalidValueException("The percentage value of setting {0} cannot be higher than 100.",
                        Setting.MONEY_POT_HOUSE_CUT);
            }
        }

        itemPotEnabled = config.getBoolean(Setting.ITEM_POT_ENABLED.getPath());
        if (itemPotEnabled) {
            itemPotDefault = convertItems(Setting.ITEM_POT_DEFAULT);
            itemPotRaise = convertItems(Setting.ITEM_POT_RAISE);
        }
        
        if(!moneyPotEnabled && !itemPotEnabled) {
            throw new InvalidValueException("At least one pot has to be enabled.");
        }

        ConfigurationSection comboSection = config.getConfigurationSection(Setting.COMBOS.getPath());
        if (comboSection != null) {
            Set<String> comboNames = comboSection.getKeys(false);
            combos = new Combo[comboNames.size()];
            int index = 0;
            for (String name : comboNames) {
                String basePath = String.format("%s.%s.", comboSection.getCurrentPath(), name);
                Material[] pattern = convertMaterials(basePath + "pattern", 3, 3, true);
                String actionPath = basePath + "actions";
                List<String> actionList = config.getStringList(actionPath);
                if (actionList.size() == 0) {
                    throw new InvalidValueException("The list size of setting {0} cannot be lower than {1}.", actionPath, 1);
                }
                Action[] actions = new Action[actionList.size()];
                for (int i = 0; i < actions.length; i++) {
                    String action = actionList.get(i);
                    try {
                        actions[i] = Action.fromString(action);
                    } catch (IllegalArgumentException ex) {
                        throw new InvalidValueException("A list value of setting {0} contains the invalid action {1}.",
                                actionPath, action);
                    }
                }

                combos[index++] = new Combo(pattern, actions);
            }
        }

    }

    private Material[] convertMaterials(String path, int minSize, int maxSize, boolean allowAir) throws InvalidValueException {
        List<String> materialList = config.getStringList(path);
        int size = materialList.size();
        if (minSize > 0 && minSize == maxSize && size != minSize) {
            throw new InvalidValueException("The list size of setting {0} must be {1}.", path, minSize);
        } else if (minSize > 0 && size < minSize) {
            throw new InvalidValueException("The list size of setting {0} cannot be lower than {1}.", path, minSize);
        } else if (maxSize > 0 && size > maxSize) {
            throw new InvalidValueException("The list size of setting {0} cannot be higher than {1}.", path, maxSize);
        }

        Material[] materials = new Material[size];
        for (int i = 0; i < materials.length; i++) {
            String name = materialList.get(i);
            materials[i] = name.equals("*") ? Material.AIR : Material.matchMaterial(name);
            if (materials[i] == null || !allowAir && materials[i] == Material.AIR) {
                throw new InvalidValueException("A list value of setting {0} contains the invalid material {1}.", path, name);
            }
        }
        return materials;
    }

    private Material[] convertMaterials(Setting setting, int minSize, int maxSize,
                                        boolean allowAir) throws InvalidValueException {
        return convertMaterials(setting.getPath(), minSize, maxSize, allowAir);
    }

    private SoundInfo[] convertSounds(Setting setting) throws InvalidValueException {
        List<String> soundList = config.getStringList(setting.getPath());
        SoundInfo[] sounds = new SoundInfo[soundList.size()];
        for (int i = 0; i < sounds.length; i++) {
            String sound = soundList.get(i);
            try {
                sounds[i] = SoundInfo.fromString(sound);
            } catch (IllegalArgumentException ex) {
                throw new InvalidValueException("A list value of setting {0} contains the invalid sound {1}.", setting, sound);
            }
        }
        return sounds;
    }

    private ItemStack[] convertItems(Setting setting) throws InvalidValueException {
        List<String> itemList = config.getStringList(setting.getPath());
        ItemStack[] items = new ItemStack[itemList.size()];
        for (int i = 0; i < items.length; i++) {
            String item = itemList.get(i);
            try {
                items[i] = ItemUtils.fromString(item);
            } catch (IllegalArgumentException ex) {
                throw new InvalidValueException("A list value of setting {0} contains the invalid item {1}.", setting, item);
            }
        }
        return items;
    }

    @Override
    public void unload() {}

    public File getFile() {
        return file;
    }

    public int getCoinAmount() {
        return coinAmount;
    }

    public Material[] getSymbolTypes() {
        return symbolTypes.clone();
    }

    public boolean getAllowCreative() {
        return allowCreative;
    }

    public String getIndividualPermission() {
        return individualPermission;
    }

    public boolean isLaunchFireworks() {
        return launchFireworks;
    }

    public int getReelStop() {
        return reelStop;
    }

    public int[] getReelDelay() {
        return reelDelay.clone();
    }

    public double getWinningChance() {
        return winningChance;
    }

    public int getLockTime() {
        return lockTime;
    }

    public String[] getWinCommands() {
        return winCommands.clone();
    }

    public SoundInfo[] getSpinSounds() {
        return spinSounds.clone();
    }

    public SoundInfo[] getWinSounds() {
        return winSounds.clone();
    }

    public SoundInfo[] getLoseSounds() {
        return loseSounds.clone();
    }

    public boolean isMoneyPotEnabled() {
        return moneyPotEnabled;
    }

    public double getMoneyPotDefault() {
        return moneyPotDefault;
    }

    public double getMoneyPotRaise() {
        return moneyPotRaise;
    }

    public double getMoneyPotHouseCut() {
        return moneyPotHouseCut;
    }

    public boolean isItemPotEnabled() {
        return itemPotEnabled;
    }

    public ItemStack[] getItemPotDefault() {
        return itemPotDefault;
    }

    public ItemStack[] getItemPotRaise() {
        return itemPotRaise;
    }

    public Combo[] getCombos() {
        return combos;
    }
}
