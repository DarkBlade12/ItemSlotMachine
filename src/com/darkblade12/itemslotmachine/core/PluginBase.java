package com.darkblade12.itemslotmachine.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

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

public abstract class PluginBase extends JavaPlugin {
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

    public void logWarning(String message) {
        logger.warning(message);
    }

    public void logException(String message, Exception exception) {
        String exceptionMessage = "Undefined";
        if (exception != null && exception.getMessage() != null) {
            exceptionMessage = exception.getMessage();
        }
        logWarning(message.replace("%c", exceptionMessage));

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

    protected void enableMetrics() {
        try {
            MetricsLite metrics = new MetricsLite(this);
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
        String response;
        try {
            URL url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + projectId);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(2000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = reader.readLine();
            reader.close();
        } catch (IOException ex) {
            logException("Failed to retrieve update information: %c", ex);
            return;
        }

        JsonElement arrayElement;
        try {
            arrayElement = JsonParser.parseString(response);
        } catch (Exception ex) {
            logException("Failed to retrieve update information: %c", ex);
            return;
        }

        JsonArray array = arrayElement.getAsJsonArray();
        if (array.size() == 0) {
            logInfo("Failed to find any files for project id '" + projectId + "'!");
            return;
        }

        JsonObject latest = (JsonObject) array.get(array.size() - 1);
        String fileName = latest.get("name").getAsString();
        String version = fileName.substring(fileName.indexOf('_') + 1, fileName.length());
        int currentVersion = Integer.parseInt(getDescription().getVersion().replace(".", ""));
        int newVersion = Integer.parseInt(version.replace(".", ""));
        try {
            currentVersion = Integer.parseInt(getDescription().getVersion().replace(".", ""));
            newVersion = Integer.parseInt(version.replace(".", ""));
        } catch (NumberFormatException ex) {
            logException("Failed to compare versions: %c", ex);
            return;
        }
        if (currentVersion >= newVersion) {
            logInfo("There is no update available!");
            return;
        }

        String fileUrl = latest.get("fileUrl").getAsString();
        logInfo("Version " + version + " is available for download at:");
        logInfo(fileUrl);
    }

    protected String getPrefix() {
        if (!messageManager.hasMessage(Message.PLUGIN_PREFIX)) {
            return "[" + getName() + "]";
        }

        return messageManager.formatMessage(Message.PLUGIN_PREFIX);
    }
    
    public abstract Locale getCurrentLanguage();

    @SuppressWarnings("deprecation")
    public OfflinePlayer getPlayer(String name) {
        String key = name.toLowerCase();
        if(playerCache.containsKey(key)) {
            return playerCache.get(key);
        }
        
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if(player == null || !player.hasPlayedBefore()) {
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
