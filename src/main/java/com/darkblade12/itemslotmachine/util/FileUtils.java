package com.darkblade12.itemslotmachine.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public final class FileUtils {
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
        builder.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
        GSON = builder.create();
    }

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
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            return lines;
        }
    }

    public static <T> T readJson(File file, Class<T> objClass) throws IOException, JsonIOException, JsonSyntaxException {
        FileInputStream stream = new FileInputStream(file);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return GSON.fromJson(reader, objClass);
        }
    }

    public static <T> T readJson(String path, Class<T> objClass) throws IOException, JsonIOException, JsonSyntaxException {
        return readJson(new File(path), objClass);
    }

    public static <T> T readJson(Plugin plugin, String path, Class<T> objClass) throws IOException, JsonIOException,
                                                                                JsonSyntaxException {
        InputStream stream = plugin.getResource(path);
        if (stream == null) {
            throw new IOException("Resource not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return GSON.fromJson(reader, objClass);
        }
    }

    public static void saveJson(File file, Object obj) throws IOException {
        Files.createParentDirs(file);
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
        List<String> names = new ArrayList<>();
        for (File file : getFiles(directory, extensions)) {
            String name = file.getName();
            if (stripExtension) {
                name = name.substring(0, name.lastIndexOf('.'));
            }
            names.add(name);
        }
        return names;
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

    public static List<String> getFileNames(File directory, boolean stripExtension) {
        return getFileNames(directory, stripExtension);
    }

    public static List<String> getFileNames(String path, boolean stripExtension) {
        return getFileNames(new File(path), stripExtension);
    }

    private static final class FileExtensionFilter implements FilenameFilter {
        private final String[] extensions;

        FileExtensionFilter(String... extensions) {
            this.extensions = new String[extensions.length];
            for (int i = 0; i < extensions.length; i++) {
                this.extensions[i] = extensions[i].toLowerCase();
            }
        }

        @Override
        public boolean accept(File dir, String name) {
            if (extensions.length == 0) {
                return true;
            }

            String lowerName = name.toLowerCase();
            for (String extension : extensions) {
                if (lowerName.endsWith(extension)) {
                    return true;
                }
            }
            return false;
        }
    }
}
