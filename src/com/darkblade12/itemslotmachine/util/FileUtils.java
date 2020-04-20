package com.darkblade12.itemslotmachine.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class FileUtils {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private FileUtils() {}

    public static String readAll(BufferedReader reader) throws IOException {
        StringBuilder text = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            text.append(line).append("\n");
        }

        return text.toString();
    }

    public static String readText(File file) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return readAll(reader);
        }
    }

    public static String readText(String path) throws FileNotFoundException, IOException {
        return readText(new File(path));
    }

    public static void saveText(File file, String text) throws IOException {
        Files.createParentDirs(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(text);
        }
    }

    public static void saveText(String path, String text) throws IOException {
        saveText(new File(path), text);
    }

    public static String readResourceText(Plugin plugin, String path) throws IOException {
        InputStream stream = plugin.getResource(path);
        if (stream == null) {
            throw new IOException("Resource '" + path + "' was not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return readAll(reader);
        }
    }

    public static List<String> readResourceLines(Plugin plugin, String path) throws IOException {
        InputStream stream = plugin.getResource(path);
        if (stream == null) {
            throw new IOException("Resource '" + path + "' was not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            return lines;
        }
    }

    public static <T> T readJson(File file, Class<T> objClass) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return GSON.fromJson(reader, objClass);
        }
    }

    public static <T> T readJson(String path, Class<T> objClass) throws FileNotFoundException, IOException {
        return readJson(new File(path), objClass);
    }

    public static void saveJson(File file, Object obj) throws IOException {
        Files.createParentDirs(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String json = GSON.toJson(obj);
            writer.write(json);
        }
    }

    public static void saveJson(String path, Object obj) throws IOException {
        saveJson(new File(path), obj);
    }
}
