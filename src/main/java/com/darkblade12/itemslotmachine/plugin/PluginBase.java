package com.darkblade12.itemslotmachine.plugin;

import com.darkblade12.itemslotmachine.plugin.command.CommandHandler;
import com.darkblade12.itemslotmachine.plugin.command.CommandRegistrationException;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PluginBase extends JavaPlugin {
    private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+(\\.\\d+){2}");
    protected final int projectId;
    protected final int pluginId;
    protected final Logger logger;
    protected final File config;
    protected final ClassToInstanceMap<Manager<?>> managers;
    protected final ClassToInstanceMap<CommandHandler<?>> commandHandlers;
    protected final Map<String, OfflinePlayer> playerCache;

    protected PluginBase(int projectId, int pluginId, Locale... locales) {
        this.projectId = projectId;
        this.pluginId = pluginId;
        logger = getLogger();
        config = new File(getDataFolder(), "config.yml");
        playerCache = new ConcurrentHashMap<>();
        managers = MutableClassToInstanceMap.create();
        managers.putInstance(MessageManager.class, new MessageManager(this, locales));
        commandHandlers = MutableClassToInstanceMap.create();
    }

    private static int[] splitVersion(String version) {
        String[] split = version.split("\\.");
        int[] numbers = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            try {
                numbers[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return numbers;
    }

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        boolean success;
        try {
            success = load();
        } catch (Exception e) {
            logException(e, "An error occurred while loading components!");
            success = false;
        }

        if (!success) {
            disable();
            return;
        }

        try {
            for (CommandHandler<?> handler : commandHandlers.values()) {
                handler.enable();
            }
        } catch (CommandRegistrationException e) {
            logException(e, "Failed to enable all command handlers!");
            disable();
            return;
        }

        try {
            managers.values().forEach(Manager::enable);
        } catch (Exception e) {
            logException(e, "Failed to enable all managers!");
            disable();
            return;
        }

        enableMetrics();
        long duration = System.currentTimeMillis() - startTime;
        logInfo("Plugin version %s enabled! (%d ms)", getVersion(), duration);

        new BukkitRunnable() {
            @Override
            public void run() {
                checkForUpdates();
            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {
        try {
            managers.values().forEach(Manager::disable);
            unload();
        } catch (Exception e) {
            logException(e, "An error occurred while unloading components!");
        }

        logInfo("Plugin version %s disabled.", getVersion());
    }

    public boolean onReload() {
        logInfo("Reloading plugin version %s...", getVersion());

        boolean success;
        try {
            success = reload();
        } catch (Exception e) {
            logException(e, "Failed to reload plugin!");
            success = false;
        }

        if (!success) {
            disable();
            return false;
        }

        try {
            managers.values().forEach(Manager::reload);
        } catch (Exception e) {
            logException(e, "Failed to reload all managers!");
            disable();
            return false;
        }

        logInfo("Plugin version %s reloaded!", getVersion());
        return true;
    }

    public abstract boolean load();

    public abstract void unload();

    public abstract boolean reload();

    public void disable() {
        logInfo("Plugin will disable...");
        getServer().getPluginManager().disablePlugin(this);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logInfo(String message, Object... args) {
        logger.info(String.format(message, args));
    }

    public void logWarning(String message) {
        logger.warning(message);
    }

    public void logWarning(String message, Object... args) {
        logger.warning(String.format(message, args));
    }

    public void logException(Exception exception, String message) {
        logger.log(Level.SEVERE, message, exception);
    }

    public void logException(Exception exception, String message, Object... args) {
        logger.log(Level.SEVERE, String.format(message, args), exception);
    }

    public String formatMessage(Message message, Object... args) {
        return getManager(MessageManager.class).formatMessage(message, args);
    }

    public void sendMessage(CommandSender sender, Message message, Object... args) {
        String text = getPrefix() + " " + ChatColor.RESET + getManager(MessageManager.class).formatMessage(message, args);
        if (sender instanceof ConsoleCommandSender) {
            text = ChatColor.stripColor(text);
        }
        sender.sendMessage(text);
    }

    @SuppressWarnings("unchecked")
    protected void registerManager(Manager<?> manager) {
        managers.putInstance((Class<Manager<?>>) manager.getClass(), manager);
    }

    @SuppressWarnings("unchecked")
    protected void registerCommandHandler(CommandHandler<?> commandHandler) {
        commandHandlers.putInstance((Class<CommandHandler<?>>) commandHandler.getClass(), commandHandler);
    }

    private void enableMetrics() {
        try {
            Metrics metrics = new Metrics(this, pluginId);
            if (!metrics.isEnabled()) {
                logInfo("Metrics is disabled.");
            } else {
                logInfo("This plugin is using Metrics by BtoBastian.");
            }
        } catch (Exception e) {
            logException(e, "Failed to enable Metrics!");
        }
    }

    private void checkForUpdates() {
        JsonArray files;
        try {
            URL url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + projectId);
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", getName() + " Update Checker");
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();
            JsonElement element = new JsonParser().parse(response);
            files = element.getAsJsonArray();
            if (files.size() == 0) {
                logWarning("Failed to find any files for project id %d!", projectId);
                return;
            }
        } catch (IOException | JsonSyntaxException e) {
            logException(e, "Failed to retrieve update information!");
            return;
        }

        JsonObject latestFile = (JsonObject) files.get(files.size() - 1);
        String fileName = latestFile.get("name").getAsString();
        Matcher matcher = VERSION_PATTERN.matcher(fileName);
        if (!matcher.find()) {
            logWarning("Failed to compare versions!");
            return;
        }

        String latestVersion = matcher.group();
        int[] currentNumbers = splitVersion(getVersion());
        int[] latestNumbers = splitVersion(latestVersion);
        if (currentNumbers == null || latestNumbers == null || currentNumbers.length != latestNumbers.length) {
            logWarning("Failed to compare versions!");
            return;
        }

        boolean updateAvailable = false;
        for (int i = 0; i < currentNumbers.length; i++) {
            if (latestNumbers[i] > currentNumbers[i]) {
                updateAvailable = true;
                break;
            }
        }

        if (!updateAvailable) {
            logInfo("There is no update available.");
            return;
        }

        String fileUrl = latestFile.get("fileUrl").getAsString();
        logInfo("Version %s is available for download at:", latestVersion);
        logInfo(fileUrl);
    }

    protected String getPrefix() {
        MessageManager manager = getManager(MessageManager.class);
        if (!manager.hasMessage(Message.MESSAGE_PREFIX)) {
            return "[" + getName() + "]";
        }

        return manager.formatMessage(Message.MESSAGE_PREFIX);
    }

    public abstract Locale getCurrentLocale();

    public String getVersion() {
        return getDescription().getVersion();
    }

    public <T extends Manager<?>> T getManager(Class<T> managerClass) {
        return managers.getInstance(managerClass);
    }

    public <T extends CommandHandler<?>> T getCommandHandler(Class<T> commandHandlerClass) {
        return commandHandlers.getInstance(commandHandlerClass);
    }

    @SuppressWarnings("deprecation")
    public OfflinePlayer getPlayer(String name) {
        String key = name.toLowerCase();
        if (playerCache.containsKey(key)) {
            return playerCache.get(key);
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (!player.hasPlayedBefore()) {
            return null;
        }

        playerCache.put(key, player);
        return player;
    }

    @Override
    public FileConfiguration getConfig() {
        if (!config.exists()) {
            saveDefaultConfig();
        }

        return super.getConfig();
    }

    public abstract boolean isDebugEnabled();
}
