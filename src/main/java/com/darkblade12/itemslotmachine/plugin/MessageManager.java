package com.darkblade12.itemslotmachine.plugin;

import com.darkblade12.itemslotmachine.util.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public final class MessageManager extends Manager<PluginBase> {
    private static final String FILE_PATTERN = "messages_{0}.json";
    private final Locale[] locales;
    private final Map<Message, MessageFormat> messageCache;
    private MessageFormat missing;

    public MessageManager(PluginBase plugin, Locale... locales) {
        super(plugin);
        this.locales = locales;
        messageCache = new HashMap<>();
    }

    @Override
    public void onEnable() {
        String tag = plugin.getCurrentLocale().toLanguageTag();
        File file = new File(plugin.getDataFolder(), MessageFormat.format(FILE_PATTERN, tag));
        String fileName = file.getName();
        JsonObject messageData = null;
        if (file.exists()) {
            try {
                messageData = FileUtils.readJson(file, JsonElement.class).getAsJsonObject();
            } catch (IOException | JsonParseException e) {
                plugin.logException(e, "Failed to read message file %s!", fileName);
            }
        } else {
            plugin.logWarning("Could not find message file %s! Copying default files...", fileName);
            saveDefaultFiles();
        }

        if (messageData == null) {
            try {
                tag = "en-US";
                String defaultName = MessageFormat.format(FILE_PATTERN, tag);
                messageData = FileUtils.readJson(plugin, defaultName, JsonElement.class).getAsJsonObject();
                plugin.logInfo("Default message files successfully copied.");
            } catch (IOException | JsonParseException e) {
                plugin.logException(e, "Failed to read the default language file!");
                return;
            }
        }

        loadMessages(messageData);
        plugin.logInfo("Messages for locale %s loaded.", tag);
    }

    @Override
    public void onDisable() {
        messageCache.clear();
    }

    public String formatMessage(Message message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                args[i] = "";
            }
        }

        MessageFormat format = messageCache.get(message);
        if (format == null) {
            return missing == null ? "" : missing.format(new Object[] { message.getKey() });
        }

        return format.format(args);
    }

    public boolean hasMessage(Message message) {
        return messageCache.containsKey(message);
    }

    private void saveDefaultFiles() {
        for (Locale locale : locales) {
            String tag = locale.toLanguageTag();
            String fileName = MessageFormat.format(FILE_PATTERN, tag);
            File file = new File(plugin.getDataFolder(), fileName);
            if (file.exists()) {
                continue;
            }

            try {
                plugin.saveResource(fileName, false);
            } catch (Exception e) {
                plugin.logException(e, "Failed to save message file %s!", fileName);
            }
        }
    }

    private void loadMessages(JsonObject messageData) {
        for (Entry<String, JsonElement> entry : messageData.entrySet()) {
            String key = entry.getKey();
            Message message = Message.fromKey(key);
            if (message == null) {
                plugin.logInfo("Found unknown message %s in messages file.", key);
                continue;
            }

            String text = StringEscapeUtils.unescapeJava(entry.getValue().getAsString());
            MessageFormat format = new MessageFormat(ChatColor.translateAlternateColorCodes('&', text));
            messageCache.put(message, format);
            if (missing == null && message == Message.MESSAGE_MISSING) {
                missing = format;
            }
        }

        if (missing == null) {
            missing = new MessageFormat("Message missing");
        }
    }
}
