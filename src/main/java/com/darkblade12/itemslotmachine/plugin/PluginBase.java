package com.darkblade12.itemslotmachine.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public abstract class PluginBase extends JavaPlugin {
    private static final Pattern VERSION = Pattern.compile("\\d+(\\.\\d+){2}");
    protected final int projectId;
    protected final Logger logger;
    protected final File config;
    protected final Map<String, OfflinePlayer> playerCache;
    protected final MessageManager messageManager;

    protected PluginBase(int projectId, Locale... locales) {
        this.projectId = projectId;
        logger = getLogger();
        config = new File(getDataFolder(), "config.yml");
        playerCache = new ConcurrentHashMap<String, OfflinePlayer>();
        messageManager = new MessageManager(this, locales);
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract boolean onReload();

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logInfo(String message, Object... args) {
        logInfo(MessageFormat.format(message, args));
    }

    public void logWarning(String message, Object... args) {
        logger.warning(MessageFormat.format(message, args));
    }

    public void logException(String message, Exception exception, Object... args) {
        Object[] messageArgs = new Object[args.length + 1];
        for (int i = 0; i < args.length; i++) {
            messageArgs[i + 1] = args[i];
        }

        if (exception != null && exception.getMessage() != null) {
            messageArgs[0] = exception.getMessage();
        } else {
            messageArgs[0] = "Undefined";
        }

        logWarning(MessageFormat.format(message, messageArgs));
        if (isDebugEnabled()) {
            exception.printStackTrace();
        }
    }

    public final void displayMessage(CommandSender sender, String message) {
        sender.sendMessage(getPrefix() + " " + message);
    }

    public final void disable() {
        getServer().getPluginManager().disablePlugin(this);
    }

    public String formatMessage(Message message, Object... args) {
        return messageManager.formatMessage(message, args);
    }

    public void sendMessage(CommandSender sender, Message message, Object... args) {
        String text = getPrefix() + " " + ChatColor.RESET + messageManager.formatMessage(message, args);
        if (sender instanceof ConsoleCommandSender) {
            text = ChatColor.stripColor(text);
        }
        sender.sendMessage(text);
    }

    public void copyResource(String resourcePath, File output, boolean replace) throws IOException {
        if (!replace && output.exists()) {
            return;
        }

        InputStream stream = getResource(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Resource not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
             BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
        }
    }

    public void copyResource(String resourcePath, String outputPath, boolean replace) throws IOException {
        copyResource(resourcePath, new File(outputPath), replace);
    }

    protected void enableMetrics() {
        try {
            Metrics metrics = new Metrics(this, 7232);
            if (!metrics.isEnabled()) {
                logWarning("Metrics is disabled!");
            } else {
                logInfo("This plugin is using Metrics by BtoBastian.");
            }
        } catch (Exception ex) {
            logException("Failed to enable Metrics! Cause: %c", ex);
        }
    }

    protected void checkForUpdates() {
        JsonArray files;
        try {
            URL url = new URL("https://servermods.forgesvc.net/servermods/files?projectIds=" + projectId);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();
            JsonElement element = new JsonParser().parse(response);
            files = element.getAsJsonArray();
            if (files.size() == 0) {
                logInfo("Failed to find any files for project id {0}!", projectId);
                return;
            }
        } catch (IOException | JsonSyntaxException ex) {
            logException("Failed to retrieve update information: {0}", ex);
            return;
        }

        JsonObject latestFile = (JsonObject) files.get(files.size() - 1);
        String fileName = latestFile.get("name").getAsString();
        Matcher matcher = VERSION.matcher(fileName);
        if (!matcher.find()) {
            logInfo("Failed to compare versions!");
            return;
        }
        String latestVersion = matcher.group();

        int[] currentNumbers = getVersionNumbers(getDescription().getVersion());
        int[] latestNumbers = getVersionNumbers(latestVersion);
        if (currentNumbers == null || latestNumbers == null || currentNumbers.length != latestNumbers.length) {
            logInfo("Failed to compare versions!");
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
        logInfo("Version {0} is available for download at:", latestVersion);
        logInfo(fileUrl);
    }

    private int[] getVersionNumbers(String version) {
        String[] split = version.split("\\.");
        int[] numbers = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            try {
                numbers[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return numbers;
    }

    protected String getPrefix() {
        if (!messageManager.hasMessage(Message.MESSAGE_PREFIX)) {
            return "[" + getName() + "]";
        }

        return messageManager.formatMessage(Message.MESSAGE_PREFIX);
    }

    public abstract Locale getCurrentLanguage();

    @SuppressWarnings("deprecation")
    public OfflinePlayer getPlayer(String name) {
        String key = name.toLowerCase();
        if (playerCache.containsKey(key)) {
            return playerCache.get(key);
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player == null || !player.hasPlayedBefore()) {
            return null;
        }

        playerCache.put(key, player);
        return player;
    }

    public int getProjectId() {
        return projectId;
    }

    @Override
    public FileConfiguration getConfig() {
        if (!config.exists()) {
            saveDefaultConfig();
        }
        return super.getConfig();
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public abstract boolean isDebugEnabled();
}
