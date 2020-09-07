package com.darkblade12.itemslotmachine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FileUtils {
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
        builder.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
        GSON = builder.create();
    }

    private FileUtils() {
    }

    public static String readAll(BufferedReader reader) throws IOException {
        StringBuilder text = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            text.append(line).append("\n");
        }
        return text.toString();
    }

    public static String readText(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return readAll(reader);
        }
    }

    public static String readText(String path) throws IOException {
        return readText(new File(path));
    }

    public static void saveText(File file, String text) throws IOException {
        Files.createDirectories(file.getParentFile().toPath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(text);
        }
    }

    public static void saveText(String path, String text) throws IOException {
        saveText(new File(path), text);
    }

    public static String readText(Plugin plugin, String path) throws IOException {
        InputStream stream = plugin.getResource(path);
        if (stream == null) {
            throw new IOException("Resource not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return readAll(reader);
        }
    }

    public static List<String> readLines(Plugin plugin, String path) throws IOException {
        InputStream stream = plugin.getResource(path);
        if (stream == null) {
            throw new IOException("Resource not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            return lines;
        }
    }

    public static <T> T readJson(File file, Class<T> objClass) throws IOException, JsonParseException {
        FileInputStream stream = new FileInputStream(file);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return GSON.fromJson(reader, objClass);
        }
    }

    public static <T> T readJson(String path, Class<T> objClass) throws IOException, JsonParseException {
        return readJson(new File(path), objClass);
    }

    public static <T> T readJson(Plugin plugin, String path, Class<T> objClass) throws IOException, JsonParseException {
        InputStream stream = plugin.getResource(path);
        if (stream == null) {
            throw new IOException("Resource not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return GSON.fromJson(reader, objClass);
        }
    }

    public static void saveJson(File file, Object obj) throws IOException {
        Files.createDirectories(file.getParentFile().toPath());
        FileOutputStream stream = new FileOutputStream(file);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8))) {
            String json = GSON.toJson(obj);
            writer.write(json);
        }
    }

    public static void saveJson(String path, Object obj) throws IOException {
        saveJson(new File(path), obj);
    }

    public static File[] getFiles(File directory, String... extensions) {
        if (!directory.exists() || !directory.isDirectory()) {
            return new File[0];
        }

        return directory.listFiles(new FileExtensionFilter(extensions));
    }

    public static File[] getFiles(String path, String... extensions) {
        return getFiles(new File(path), extensions);
    }

    public static List<String> getFileNames(File directory, boolean stripExtension, String... extensions) {
        return Arrays.stream(getFiles(directory, extensions)).map(f -> {
            String name = f.getName();
            return stripExtension ? name.substring(0, name.lastIndexOf('.')) : name;
        }).collect(Collectors.toList());
    }

    public static List<String> getFileNames(String path, boolean stripExtension, String... extensions) {
        return getFileNames(new File(path), stripExtension, extensions);
    }

    public static List<String> getFileNames(File directory, String... extensions) {
        return getFileNames(directory, false, extensions);
    }

    public static List<String> getFileNames(String path, String... extensions) {
        return getFileNames(new File(path), extensions);
    }

    private static final class FileExtensionFilter implements FilenameFilter {
        private final String[] extensions;

        FileExtensionFilter(String... extensions) {
            this.extensions = Arrays.stream(extensions).map(String::toLowerCase).toArray(String[]::new);
        }

        @Override
        public boolean accept(File dir, String name) {
            String lowerName = name.toLowerCase();
            return extensions.length == 0 || Arrays.stream(extensions).anyMatch(lowerName::endsWith);
        }
    }
}
