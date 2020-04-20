package com.darkblade12.itemslotmachine.message;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.design.Design;
import com.darkblade12.itemslotmachine.hook.VaultHook;
import com.darkblade12.itemslotmachine.manager.Manager;
import com.darkblade12.itemslotmachine.settings.Settings;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.statistic.Category;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.statistic.StatisticRecord;
import com.darkblade12.itemslotmachine.statistic.types.PlayerStatistic;
import com.darkblade12.itemslotmachine.statistic.types.SlotMachineStatistic;
import com.darkblade12.itemslotmachine.util.FileUtils;
import com.darkblade12.itemslotmachine.util.ItemList;
import com.google.common.io.Files;

public final class MessageManager extends Manager implements MessageContainer {
    private static final Random RANDOM = new Random();
    private static final String[] DEFAULT_FILES = { "lang_en-US.txt", "lang_de-DE.txt" };
    private static final String[] COLOR_CODE_MODIFIERS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e" };
    private static final Map<String, String> EQUAL_COLOR_CODES = new HashMap<String, String>();
    private static final Map<Integer, String> NUMBER_SYMBOLS = new HashMap<Integer, String>();
    private static final String[] DICES = new String[] { "\u2680", "\u2681", "\u2682", "\u2683", "\u2684", "\u2685" };
    public static final String TRUE = "§a\u2714";
    public static final String FALSE = "§c\u2718";
    private Map<String, String> messages;

    static {
        EQUAL_COLOR_CODES.put("§1", "§9");
        EQUAL_COLOR_CODES.put("§2", "§a");
        EQUAL_COLOR_CODES.put("§3", "§b");
        EQUAL_COLOR_CODES.put("§4", "§c");
        EQUAL_COLOR_CODES.put("§5", "§d");
        EQUAL_COLOR_CODES.put("§6", "§e");
        EQUAL_COLOR_CODES.put("§7", "§8");
        NUMBER_SYMBOLS.put(1, "§6\u2776");
        NUMBER_SYMBOLS.put(2, "§7\u2777");
        NUMBER_SYMBOLS.put(3, "§8\u2778");
        NUMBER_SYMBOLS.put(4, "§f\u2779");
        NUMBER_SYMBOLS.put(5, "§f\u277A");
        NUMBER_SYMBOLS.put(6, "§f\u277B");
        NUMBER_SYMBOLS.put(7, "§f\u277C");
        NUMBER_SYMBOLS.put(8, "§f\u277D");
        NUMBER_SYMBOLS.put(9, "§f\u277E");
        NUMBER_SYMBOLS.put(10, "§f\u277F");
    }

    public MessageManager(ItemSlotMachine plugin) {
        super(plugin);
    }

    @Override
    public boolean onInitialize() {
        File directory = new File("plugins/ItemSlotMachine/");
        String tag = Settings.getLanguageTag();
        String fileName = "lang_" + tag + ".txt";
        File langFile = null;
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                String name = file.getName();
                if (name.equalsIgnoreCase(fileName)) {
                    langFile = file;
                    fileName = name;
                    break;
                }
            }
        }

        List<String> lines = null;
        if (langFile != null) {
            try {
                lines = Files.readLines(langFile, StandardCharsets.UTF_8);
            } catch (Exception e) {
                plugin.logWarning("Failed to read '" + fileName + "', messages will default to english! Cause: %c", e);

                if (Settings.isDebugModeEnabled()) {
                    e.printStackTrace();
                }
            }
        } else {
            plugin.logWarning("Language file '" + fileName + "' not found, messages will default to english!");

            for (String defaultName : DEFAULT_FILES) {
                try {
                    plugin.saveResource(defaultName, false);
                } catch (Exception e) {
                    plugin.logWarning("Failed to save '" + defaultName + "'! Cause: %c", e);
                }
            }
        }

        if (lines == null) {
            try {
                lines = FileUtils.readResourceLines(plugin, DEFAULT_FILES[0]);
            } catch (Exception e) {
                plugin.logWarning("Failed to read the default language file, plugin will disable! Cause: %c", e);
                if (Settings.isDebugModeEnabled()) {
                    e.printStackTrace();
                }
                
                return false;
            }
        }

        loadMessages(lines);
        plugin.logInfo("Language " + tag + " successfully loaded.");
        return true;
    }

    @Override
    public void onDisable() {}

    private void loadMessages(List<String> lines) {
        messages = new HashMap<String, String>();
        for (String line : lines) {
            String[] data = line.split("=");
            if (data.length == 2 && !line.startsWith("#")) {
                messages.put(data[0], ChatColor.translateAlternateColorCodes('&', data[1]));
            }
        }
    }

    public static String randomColorCode() {
        return "§" + COLOR_CODE_MODIFIERS[RANDOM.nextInt(COLOR_CODE_MODIFIERS.length)];
    }

    public static String equalColorCode(String c) {
        for (Entry<String, String> e : EQUAL_COLOR_CODES.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (k.equals(c))
                return v;
            else if (v.equals(c))
                return k;
        }
        throw new IllegalArgumentException("Invalid color code");
    }

    public static String getSymbol(int number) {
        return NUMBER_SYMBOLS.get(number);
    }

    public static String randomDice() {
        return DICES[RANDOM.nextInt(DICES.length)];
    }

    public String getMessage(String name) {
        if (!messages.containsKey(name))
            return "§cMessage not available, please check your language file! §8(§7Message name: §6" + name + "§8)";
        return messages.get(name);
    }

    public String getMessage(String name, boolean prefix) {
        return (prefix ? plugin_prefix() : "") + getMessage(name);
    }

    private static String designsToString(List<Design> list) {
        StringBuilder s = new StringBuilder();
        for (Design d : list)
            s.append("\n§r §7\u25C9 " + randomColorCode() + d.getName());
        return s.toString();
    }

    private static String slotMachinesToString(List<SlotMachine> list) {
        StringBuilder s = new StringBuilder();
        for (SlotMachine m : list)
            s.append("\n§r §6\u25C9 §2" + m.getName() + " §7\u25BB §eActive: " + (m.isActive() ? TRUE : FALSE));
        return s.toString();
    }

    private static String formatEnumName(String name) {
        StringBuilder s = new StringBuilder();
        String[] p = name.split("_");
        for (int i = 0; i < p.length; i++) {
            if (s.length() > 0)
                s.append(" ");
            s.append(Character.toUpperCase(p[i].charAt(0)) + p[i].substring(1).toLowerCase());
        }
        return s.toString();
    }

    public static String getItemName(ItemStack item) {
        return formatEnumName(item.getType().name());
    }

    public static String itemToString(ItemStack item) {
        return (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName()
                : "§2" + getItemName(item)) + " §8\u00D7 §7" + item.getAmount();
    }

    public String itemsToString(ItemList items, String colors) {
        if (colors.length() == 0)
            colors = "§a";
        StringBuilder s = new StringBuilder();
        int a = 0;
        for (ItemStack i : items) {
            if (a > 0)
                if (a == items.size() - 1)
                    s.append(colors + " " + and() + " ");
                else
                    s.append(colors + ", ");
            s.append(itemToString(i));
            a++;
        }
        return s.length() > 0 ? s.toString() : "§cN/A";
    }

    private String statisticToString(Statistic statistic) {
        StringBuilder s = new StringBuilder();
        for (StatisticRecord o : statistic.getRecords()) {
            String c = randomColorCode();
            s.append("\n§r §7" + randomDice() + " " + c + o.getCategory().getLocalizedName(plugin) + ": " + equalColorCode(c)
                    + o.getValue());
        }
        return s.toString();
    }

    private String slotMachineTopToString(Category category) {
        StringBuilder s = new StringBuilder();
        List<SlotMachineStatistic> top = plugin.slotMachineManager.getTop(category);
        int size = top.size();
        for (int i = 0; i < (size > 10 ? 10 : size); i++) {
            SlotMachineStatistic m = top.get(i);
            s.append("\n§r " + getSymbol(i + 1) + " §a" + m.getName() + " §8(§e" + m.getRecord(category).getValue() + "§8)");
        }
        return s.toString();
    }

    private String playerTopToString(Category category) {
        StringBuilder s = new StringBuilder();
        List<PlayerStatistic> top = plugin.statisticManager.getTop(category);
        int size = top.size();
        for (int i = 0; i < (size > 10 ? 10 : size); i++) {
            PlayerStatistic p = top.get(i);
            s.append("\n§r " + getSymbol(i + 1) + " §a" + p.getName() + " §8(§e" + p.getRecord(category).getValue() + "§8)");
        }
        return s.toString();
    }

    @Override
    public String plugin_prefix() {
        return getMessage("plugin_prefix");
    }

    @Override
    public String plugin_reloaded(String version, long time) {
        return getMessage("plugin_reloaded", true).replace("<version>", version).replace("<time>", Long.toString(time));
    }

    public String plugin_reloaded(long time) {
        return plugin_reloaded(plugin.getDescription().getVersion(), time);
    }

    @Override
    public String command_no_console_executor() {
        return getMessage("command_no_console_executor");
    }

    @Override
    public String command_no_permission() {
        return getMessage("command_no_permission");
    }

    @Override
    public String command_invalid_usage(String usage) {
        return getMessage("command_invalid_usage").replace("<usage>", usage);
    }

    @Override
    public String help_page_header(String label) {
        return getMessage("help_page_header", true).replace("<label>", label);
    }

    @Override
    public String help_page_footer(int currentPage, int pageAmount) {
        return getMessage("help_page_footer")
                .replace("<current_page>", (currentPage == pageAmount ? "§6§l" : "§a§l") + Integer.toString(currentPage))
                .replace("<page_amount>", Integer.toString(pageAmount));
    }

    @Override
    public String help_page_command_format(String command, String description, boolean executableAsConsole, String permission) {
        return getMessage("help_page_command_format").replace("<command>", command).replace("<description>", description)
                .replace("<executable_as_console>", executableAsConsole ? TRUE : FALSE).replace("<permission>", permission);
    }

    @Override
    public String help_page_not_existent(int page) {
        return getMessage("help_page_not_existent", true).replace("<page>", Integer.toString(page));
    }

    @Override
    public String player_not_enough_space() {
        return getMessage("player_not_enough_space", true);
    }

    @Override
    public String player_not_enough_space_other() {
        return getMessage("player_not_enough_space_other", true);
    }

    @Override
    public String player_not_existent() {
        return getMessage("player_not_existent", true);
    }

    @Override
    public String player_no_item_in_hand() {
        return getMessage("player_no_item_in_hand", true);
    }

    @Override
    public String design_wand_name() {
        return getMessage("design_wand_name");
    }

    @Override
    public String[] design_wand_lore() {
        return getMessage("design_wand_lore").split("\n");
    }

    @Override
    public String design_wand_got() {
        return getMessage("design_wand_got", true);
    }

    @Override
    public String design_wand_first_position_selected(int x, int y, int z, String world) {
        return getMessage("design_wand_first_position_selected", true).replace("<x>", Integer.toString(x))
                .replace("<y>", Integer.toString(y)).replace("<z>", Integer.toString(z)).replace("<world>", world);
    }

    @Override
    public String design_wand_second_position_selected(int x, int y, int z, String world) {
        return getMessage("design_wand_second_position_selected", true).replace("<x>", Integer.toString(x))
                .replace("<y>", Integer.toString(y)).replace("<z>", Integer.toString(z)).replace("<world>", world);
    }

    @Override
    public String design_already_existent() {
        return getMessage("design_already_existent", true);
    }

    @Override
    public String design_not_existent() {
        return getMessage("design_not_existent", true);
    }

    @Override
    public String design_invalid_selection() {
        return getMessage("design_invalid_selection", true);
    }

    @Override
    public String design_creation_failure(String cause) {
        return getMessage("design_creation_failure", true).replace("<cause>", cause);
    }

    @Override
    public String design_creation_success(String name) {
        return getMessage("design_creation_success", true).replace("<name>", name);
    }

    @Override
    public String design_removal_failure(String name) {
        return getMessage("design_removal_failure", true).replace("<name>", name);
    }

    @Override
    public String design_removal_success(String name) {
        return getMessage("design_removal_success", true).replace("<name>", name);
    }

    @Override
    public String design_list(String list) {
        return getMessage("design_list", true).replace("<list>", list);
    }

    public String design_list() {
        return design_list(designsToString(plugin.designManager.getDesigns()));
    }

    @Override
    public String design_not_modifiable() {
        return getMessage("design_not_modifiable", true);
    }

    @Override
    public String design_inversion_failure(String name, String cause) {
        return getMessage("design_inversion_failure", true).replace("<name>", name).replace("<cause>",
                                                                                            cause == null ? "Unknown" : cause);
    }

    @Override
    public String design_inversion_success(String name) {
        return getMessage("design_inversion_success", true).replace("<name>", name);
    }

    @Override
    public String design_reload() {
        return getMessage("design_reload", true);
    }

    @Override
    public String coin_name() {
        return getMessage("coin_name");
    }

    @Override
    public String[] coin_lore() {
        return getMessage("coin_lore").split("\n");
    }

    @Override
    public String coin_purchase_disabled() {
        return getMessage("coin_purchase_disabled", true);
    }

    @Override
    public String coin_purchase_not_enough_money(int coins, double price, String currencyName) {
        return getMessage("coin_purchase_not_enough_money", true).replace("<coins>", Integer.toString(coins))
                .replace("<price>", Double.toString(price)).replace("<currency_name>", currencyName);
    }

    public String coin_purchase_not_enough_money(int coins, double price) {
        return coin_purchase_not_enough_money(coins, price, VaultHook.getCurrencyName(price == 1));
    }

    @Override
    public String coin_purchase(int coins, double price, String currencyName) {
        return getMessage("coin_purchase", true).replace("<coins>", Integer.toString(coins))
                .replace("<price>", Double.toString(price)).replace("<currency_name>", currencyName);
    }

    public String coin_purchase(int coins, double price) {
        return coin_purchase(coins, price, VaultHook.getCurrencyName(price == 1));
    }

    @Override
    public String coin_grant_self(int coins) {
        return getMessage("coin_grant_self", true).replace("<coins>", Integer.toString(coins));
    }

    @Override
    public String coin_grant_sender(String player, int coins) {
        return getMessage("coin_grant_sender", true).replace("<player>", player).replace("<coins>", Integer.toString(coins));
    }

    @Override
    public String coin_grant_receiver(int coins, String sender) {
        return getMessage("coin_grant_receiver", true).replace("<coins>", Integer.toString(coins)).replace("<sender>", sender);
    }

    @Override
    public String slot_machine_modifying_not_allowed() {
        return getMessage("slot_machine_modifying_not_allowed", true);
    }

    @Override
    public String slot_machine_clicked(String name) {
        return getMessage("slot_machine_clicked", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_usage_not_allowed() {
        return getMessage("slot_machine_usage_not_allowed", true);
    }

    @Override
    public String slot_machine_still_active() {
        return getMessage("slot_machine_still_active", true);
    }

    @Override
    public String slot_machine_broken() {
        return getMessage("slot_machine_broken", true);
    }

    @Override
    public String slot_machine_creative_not_allowed() {
        return getMessage("slot_machine_creative_not_allowed", true);
    }

    @Override
    public String slot_machine_not_enough_coins(int coins) {
        return getMessage("slot_machine_not_enough_coins", true).replace("<coins>", Integer.toString(coins));
    }

    @Override
    public String slot_machine_limited_usage(int amount) {
        return getMessage("slot_machine_limited_usage", true).replace("<amount>", Integer.toString(amount));
    }

    public String slot_machine_limited_usage() {
        return slot_machine_limited_usage(Settings.getLimitedUsageAmount());
    }

    @Override
    public String slot_machine_locked(String player, int seconds) {
        return getMessage("slot_machine_locked", true).replace("<player>", player)
                .replace("<seconds>", seconds < 0 ? "N/A" : Integer.toString(seconds));
    }

    @Override
    public String slot_machine_won(double money, String currencyName, int itemAmount, String items) {
        return getMessage("slot_machine_won", true).replace("<money>", Double.toString(money))
                .replace("<currency_name>", currencyName).replace("<item_amount>", Integer.toString(itemAmount))
                .replace("<items>", items);
    }

    public String slot_machine_won(double money, ItemList items) {
        String message = getMessage("slot_machine_won");
        int index = message.indexOf("<items>");
        String currency = "money";
        if (VaultHook.isEnabled()) {
            currency = VaultHook.getCurrencyName(money == 1);
        }

        String itemsText = "";

        if (index > -1) {
            itemsText = itemsToString(items, ChatColor.getLastColors(message.substring(0, index)));
        }

        return slot_machine_won(money, currency, items.size(), itemsText);
    }

    @Override
    public String slot_machine_lost() {
        return getMessage("slot_machine_lost", true);
    }

    @Override
    public String slot_machine_already_existent() {
        return getMessage("slot_machine_already_existent", true);
    }

    @Override
    public String slot_machine_not_existent() {
        return getMessage("slot_machine_not_existent", true);
    }

    @Override
    public String slot_machine_building_failure(String cause) {
        return getMessage("slot_machine_building_failure", true).replace("<cause>", cause == null ? "Unknown" : cause);
    }

    @Override
    public String slot_machine_building_success(String name) {
        return getMessage("slot_machine_building_success", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_destruction(String name) {
        return getMessage("slot_machine_destruction", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_list_empty() {
        return getMessage("slot_machine_list_empty", true);
    }

    @Override
    public String slot_machine_list(String list) {
        return getMessage("slot_machine_list", true).replace("<list>", list);
    }

    public String slot_machine_list() {
        return slot_machine_list(slotMachinesToString(plugin.slotMachineManager.getSlotMachines()));
    }

    @Override
    public String slot_machine_teleportation_failure(String name, String cause) {
        return getMessage("slot_machine_teleportation_failure", true).replace("<name>", name).replace("<cause>", cause);
    }

    @Override
    public String slot_machine_teleportation_success(String name) {
        return getMessage("slot_machine_teleportation_success", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_rebuilding_failure(String name, String cause) {
        return getMessage("slot_machine_rebuilding_failure", true).replace("<name>", name).replace("<cause>",
                                                                                           cause == null ? "Unknown" : cause);
    }

    @Override
    public String slot_machine_rebuilding_success(String name) {
        return getMessage("slot_machine_rebuilding_success", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_moving_failure(String cause) {
        return getMessage("slot_machine_moving_failure", true).replace("<cause>", cause);
    }

    @Override
    public String slot_machine_moving_success(String name) {
        return getMessage("slot_machine_moving_success", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_not_active() {
        return getMessage("slot_machine_not_active", true);
    }

    @Override
    public String slot_machine_deactivation(String name) {
        return getMessage("slot_machine_deactivation", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_money_pot_not_enabled() {
        return getMessage("slot_machine_money_pot_not_enabled", true);
    }

    @Override
    public String slot_machine_money_pot_empty() {
        return getMessage("slot_machine_money_pot_empty", true);
    }

    @Override
    public String slot_machine_money_pot_deposit(double money, String currencyName, String name, double pot) {
        return getMessage("slot_machine_money_pot_deposit", true).replace("<money>", Double.toString(money))
                .replace("<currency_name>", currencyName).replace("<name>", name).replace("<pot>", Double.toString(pot));
    }

    public String slot_machine_money_pot_deposit(double money, String name, double pot) {
        return slot_machine_money_pot_deposit(money, VaultHook.getCurrencyName(false), name, pot);
    }

    @Override
    public String slot_machine_money_pot_withdraw(double money, String currencyName, String name, double pot) {
        return getMessage("slot_machine_money_pot_withdraw", true).replace("<money>", Double.toString(money))
                .replace("<currency_name>", currencyName).replace("<name>", name).replace("<pot>", Double.toString(pot));
    }

    public String slot_machine_money_pot_withdraw(double money, String name, double pot) {
        return slot_machine_money_pot_withdraw(money, VaultHook.getCurrencyName(false), name, pot);
    }

    @Override
    public String slot_machine_money_pot_set(String name, double money, String currencyName) {
        return getMessage("slot_machine_money_pot_set", true).replace("<name>", name).replace("<money>", Double.toString(money))
                .replace("<currency_name>", currencyName);
    }

    public String slot_machine_money_pot_set(String name, double money) {
        return slot_machine_money_pot_set(name, money, VaultHook.getCurrencyName(money == 1));
    }

    @Override
    public String slot_machine_money_pot_reset(String name, double pot, String currencyName) {
        return getMessage("slot_machine_money_pot_reset", true).replace("<name>", name).replace("<pot>", Double.toString(pot))
                .replace("<currency_name>", currencyName);
    }

    public String slot_machine_money_pot_reset(String name, double pot) {
        return slot_machine_money_pot_reset(name, pot, VaultHook.getCurrencyName(pot == 1));
    }

    @Override
    public String slot_machine_money_pot_clear(String name) {
        return getMessage("slot_machine_money_pot_clear", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_item_pot_not_enabled() {
        return getMessage("slot_machine_item_pot_not_enabled", true);
    }

    @Override
    public String slot_machine_item_pot_empty() {
        return getMessage("slot_machine_item_pot_empty", true);
    }

    @Override
    public String slot_machine_item_pot_invalid_item_list(String cause) {
        return getMessage("slot_machine_item_pot_invalid_item_list", true).replace("<cause>", cause);
    }

    @Override
    public String slot_machine_item_pot_deposit(String item, String name) {
        return getMessage("slot_machine_item_pot_deposit", true).replace("<item>", item).replace("<name>", name);
    }

    public String slot_machine_item_pot_deposit(ItemStack item, String name) {
        return slot_machine_item_pot_deposit(itemToString(item), name);
    }

    @Override
    public String slot_machine_item_pot_deposit_multiple(String items, String name) {
        return getMessage("slot_machine_item_pot_deposit_multiple", true).replace("<items>", items).replace("<name>", name);
    }

    public String slot_machine_item_pot_deposit_multiple(ItemList items, String name) {
        String message = getMessage("slot_machine_item_pot_deposit_multiple");
        int index = message.indexOf("<items>");
        String itemsText = "";

        if (index > -1) {
            itemsText = itemsToString(items, ChatColor.getLastColors(message.substring(0, index)));
        }

        return slot_machine_item_pot_deposit_multiple(itemsText, name);
    }

    @Override
    public String slot_machine_item_pot_set(String name, String items) {
        return getMessage("slot_machine_item_pot_set", true).replace("<name>", name).replace("<items>", items);
    }

    public String slot_machine_item_pot_set(String name, ItemList items) {
        String message = getMessage("slot_machine_item_pot_set");
        int index = message.indexOf("<items>");
        String itemsText = "";

        if (index > -1) {
            itemsText = itemsToString(items, ChatColor.getLastColors(message.substring(0, index)));
        }

        return slot_machine_item_pot_set(name, itemsText);
    }

    @Override
    public String slot_machine_item_pot_reset(String name, String items) {
        return getMessage("slot_machine_item_pot_reset", true).replace("<name>", name).replace("<items>", items);
    }

    public String slot_machine_item_pot_reset(String name, ItemList items) {
        String message = getMessage("slot_machine_item_pot_reset");
        int index = message.indexOf("<items>");
        String itemsText = "";

        if (index > -1) {
            itemsText = itemsToString(items, ChatColor.getLastColors(message.substring(0, index)));
        }

        return slot_machine_item_pot_reset(name, itemsText);
    }

    @Override
    public String slot_machine_item_pot_clear(String name) {
        return getMessage("slot_machine_item_pot_clear", true).replace("<name>", name);
    }

    @Override
    public String slot_machine_reload_failure(String cause) {
        return getMessage("slot_machine_reload_failure", true).replace("<cause>", cause);
    }

    @Override
    public String slot_machine_reload(String name) {
        return getMessage("slot_machine_reload", true).replace("<name>", name);
    }

    @Override
    public String statistic_show_slot_machine(String name, String statistic) {
        return getMessage("statistic_show_slot_machine", true).replace("<name>", name).replace("<statistic>", statistic);
    }

    public String statistic_show_slot_machine(String name, SlotMachineStatistic statistic) {
        return statistic_show_slot_machine(name, statisticToString(statistic));
    }

    @Override
    public String statistic_show_player(String name, String statistic) {
        return getMessage("statistic_show_player", true).replace("<name>", name).replace("<statistic>", statistic);
    }

    @Override
    public String statistic_player_not_existent() {
        return getMessage("statistic_player_not_existent", true);
    }

    public String statistic_show_player(String name, PlayerStatistic statistic) {
        return statistic_show_player(name, statisticToString(statistic));
    }

    public String statistic_top_category_not_existent() {
        return getMessage("statistic_top_category_not_existent", true);
    }

    @Override
    public String statistic_top_slot_machine_invalid_category() {
        return getMessage("statistic_top_slot_machine_invalid_category", true);
    }

    @Override
    public String statistic_top_slot_machine_not_existent() {
        return getMessage("statistic_top_slot_machine_not_existent", true);
    }

    @Override
    public String statistic_top_slot_machine(String category, String top) {
        return getMessage("statistic_top_slot_machine", true).replace("<category>", category).replace("<top>", top);
    }

    public String statistic_top_slot_machine(Category category) {
        return statistic_top_slot_machine(category.getLocalizedName(plugin), slotMachineTopToString(category));
    }

    @Override
    public String statistic_top_player_not_existent() {
        return getMessage("statistic_top_player_not_existent", true);
    }

    @Override
    public String statistic_top_player(String category, String top) {
        return getMessage("statistic_top_player", true).replace("<category>", category).replace("<top>", top);
    }

    public String statistic_top_player(Category category) {
        return statistic_top_player(category.getLocalizedName(plugin), playerTopToString(category));
    }

    @Override
    public String statistic_reset_slot_machine(String name) {
        return getMessage("statistic_reset_slot_machine", true).replace("<name>", name);
    }

    @Override
    public String statistic_reset_player(String name) {
        return getMessage("statistic_reset_player", true).replace("<name>", name);
    }

    @Override
    public String sign_coin_shop_header() {
        return getMessage("sign_coin_shop_header");
    }

    @Override
    public String sign_coin_shop_coins(int coins) {
        return getMessage("sign_coin_shop_coins").replace("<coins>", Integer.toString(coins));
    }

    @Override
    public String sign_coin_shop_price(double price) {
        return getMessage("sign_coin_shop_price").replace("<price>", Double.toString(price));
    }

    @Override
    public String sign_coin_shop_spacer() {
        return getMessage("sign_coin_shop_spacer");
    }

    @Override
    public String sign_pot_money(double money) {
        //        String moneyText;
        //        if (money > 9999999.99) {
        //            Locale locale = Locale.forLanguageTag(Settings.getLanguageName());
        //            if (locale == null) {
        //                locale = Locale.US;
        //            }
        //
        //            NumberFormat format = NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.SHORT);
        //            moneyText = format.format(money);
        //        } else {
        //            moneyText = Double.toString(money);
        //        }

        return getMessage("sign_pot_money").replace("<money>", Double.toString(money));
    }

    @Override
    public String sign_pot_items(int items) {
        return getMessage("sign_pot_items").replace("<items>", Integer.toString(items));
    }

    @Override
    public String sign_pot_spacer(String colorCode) {
        return getMessage("sign_pot_spacer").replace("<color_code>", colorCode);
    }

    public String sign_pot_spacer() {
        return sign_pot_spacer(randomColorCode());
    }

    @Override
    public String input_not_numeric(String input) {
        return getMessage("input_not_numeric", true).replace("<input>", input);
    }

    @Override
    public String lower_than_number(int number) {
        return getMessage("lower_than_number").replace("<number>", Integer.toString(number));
    }

    @Override
    public String equals_number(int number) {
        return getMessage("equals_number").replace("<number>", Integer.toString(number));
    }

    @Override
    public String higher_than_number(double number) {
        return getMessage("higher_than_number").replace("<number>", Double.toString(number));
    }

    @Override
    public String invalid_amount(String cause) {
        return getMessage("invalid_amount").replace("<cause>", cause);
    }

    @Override
    public String and() {
        return getMessage("and");
    }

    @Override
    public String total_spins() {
        return getMessage("total_spins");
    }

    @Override
    public String won_spins() {
        return getMessage("won_spins");
    }

    @Override
    public String lost_spins() {
        return getMessage("lost_spins");
    }

    @Override
    public String coins_spent() {
        return getMessage("coins_spent");
    }

    @Override
    public String won_money() {
        return getMessage("won_money");
    }

    @Override
    public String won_items() {
        return getMessage("won_items");
    }
}