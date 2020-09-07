package com.darkblade12.itemslotmachine.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    private static final Gson GSON = new Gson();

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", src.getType().name());
        obj.addProperty("amount", src.getAmount());

        if (src.hasItemMeta()) {
            ItemMeta meta = src.getItemMeta();
            JsonObject metaObj = new JsonObject();

            if (meta.hasDisplayName()) {
                metaObj.addProperty("displayName", meta.getDisplayName());
            }

            if (meta.hasLore()) {
                metaObj.add("lore", GSON.toJsonTree(meta.getLore()));
            }

            if (meta.hasEnchants()) {
                metaObj.add("enchants", serializeEnchants(meta.getEnchants()));
            }

            Set<ItemFlag> flags = meta.getItemFlags();
            if (flags.size() > 0) {
                metaObj.add("flags", GSON.toJsonTree(flags));
            }

            if (meta instanceof Damageable) {
                metaObj.addProperty("unbreakable", meta.isUnbreakable());
                metaObj.addProperty("damage", ((Damageable) meta).getDamage());
            }

            if (meta instanceof Repairable) {
                Repairable repairable = (Repairable) meta;
                if (repairable.hasRepairCost()) {
                    metaObj.addProperty("repairCost", repairable.getRepairCost());
                }
            }

            if (meta instanceof BannerMeta) {
                serializeMeta(metaObj, (BannerMeta) meta);
            } else if (meta instanceof BookMeta) {
                serializeMeta(metaObj, (BookMeta) meta);
            } else if (meta instanceof EnchantmentStorageMeta) {
                serializeMeta(metaObj, (EnchantmentStorageMeta) meta);
            } else if (meta instanceof FireworkEffectMeta) {
                serializeMeta(metaObj, (FireworkEffectMeta) meta);
            } else if (meta instanceof FireworkMeta) {
                serializeMeta(metaObj, (FireworkMeta) meta);
            } else if (meta instanceof LeatherArmorMeta) {
                serializeMeta(metaObj, (LeatherArmorMeta) meta);
            } else if (meta instanceof PotionMeta) {
                serializeMeta(metaObj, (PotionMeta) meta);
            } else if (meta instanceof SkullMeta) {
                serializeMeta(metaObj, (SkullMeta) meta);
            } else if (meta instanceof SuspiciousStewMeta) {
                serializeMeta(metaObj, (SuspiciousStewMeta) meta);
            }

            if (metaObj.entrySet().size() > 0) {
                obj.add("meta", metaObj);
            }
        }

        return obj;
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Material material = Material.getMaterial(obj.get("type").getAsString());
        if (material == null) {
            throw new JsonParseException("Invalid material name");
        }

        int amount = obj.get("amount").getAsInt();
        if (amount < 1) {
            throw new JsonParseException("Invalid amount");
        }

        ItemStack item = new ItemStack(material, amount);
        if (obj.has("meta")) {
            JsonObject metaObj = obj.get("meta").getAsJsonObject();
            ItemMeta meta = item.getItemMeta();

            if (metaObj.has("displayName")) {
                meta.setDisplayName(metaObj.get("displayName").getAsString());
            }

            if (metaObj.has("lore")) {
                JsonArray loreArray = metaObj.getAsJsonArray("lore");
                List<String> lore = new ArrayList<>(loreArray.size());
                for (JsonElement line : loreArray) {
                    lore.add(line.getAsString());
                }
                meta.setLore(lore);
            }

            if (metaObj.has("enchants")) {
                Map<Enchantment, Integer> enchants = deserializeEnchants(metaObj.getAsJsonObject("enchants"));
                for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }

            if (meta instanceof BannerMeta) {
                deserializeMeta(metaObj, (BannerMeta) meta);
            } else if (meta instanceof BookMeta) {
                deserializeMeta(metaObj, (BookMeta) meta);
            } else if (meta instanceof EnchantmentStorageMeta) {
                deserializeMeta(metaObj, (EnchantmentStorageMeta) meta);
            } else if (meta instanceof FireworkEffectMeta) {
                deserializeMeta(metaObj, (FireworkEffectMeta) meta);
            } else if (meta instanceof FireworkMeta) {
                deserializeMeta(metaObj, (FireworkMeta) meta);
            } else if (meta instanceof LeatherArmorMeta) {
                deserializeMeta(metaObj, (LeatherArmorMeta) meta);
            } else if (meta instanceof PotionMeta) {
                deserializeMeta(metaObj, (PotionMeta) meta);
            } else if (meta instanceof SkullMeta) {
                deserializeMeta(metaObj, (SkullMeta) meta);
            } else if (meta instanceof SuspiciousStewMeta) {
                deserializeMeta(metaObj, (SuspiciousStewMeta) meta);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    private static void serializeMeta(JsonObject root, BannerMeta meta) {
        root.add("patterns", GSON.toJsonTree(meta.getPatterns()));
    }

    private static void serializeMeta(JsonObject root, BookMeta meta) {
        if (meta.hasTitle()) {
            root.addProperty("title", meta.getTitle());
        }

        if (meta.hasAuthor()) {
            root.addProperty("author", meta.getAuthor());
        }

        if (meta.hasGeneration()) {
            root.addProperty("generation", meta.getGeneration().name());
        }

        if (meta.hasPages()) {
            root.add("pages", GSON.toJsonTree(meta.getPages()));
        }
    }

    private static void serializeMeta(JsonObject root, EnchantmentStorageMeta meta) {
        if (meta.hasStoredEnchants()) {
            root.add("storedEnchants", serializeEnchants(meta.getStoredEnchants()));
        }
    }

    private static void serializeMeta(JsonObject root, FireworkEffectMeta meta) {
        if (meta.hasEffect()) {
            root.add("effect", serializeFireworkEffect(meta.getEffect()));
        }
    }

    private static void serializeMeta(JsonObject root, FireworkMeta meta) {
        root.addProperty("power", meta.getPower());
        if (meta.hasEffects()) {
            JsonArray effectsArray = new JsonArray();
            for (FireworkEffect effect : meta.getEffects()) {
                effectsArray.add(serializeFireworkEffect(effect));
            }
            root.add("effects", effectsArray);
        }
    }

    private static void serializeMeta(JsonObject root, LeatherArmorMeta meta) {
        root.add("color", GSON.toJsonTree(meta.getColor()));
    }

    private static void serializeMeta(JsonObject root, PotionMeta meta) {
        root.add("basePotionData", GSON.toJsonTree(meta.getBasePotionData()));
        if (meta.hasColor()) {
            root.add("color", GSON.toJsonTree(meta.getColor()));
        }

        if (meta.hasCustomEffects()) {
            root.add("customEffects", GSON.toJsonTree(meta.getCustomEffects()));
        }
    }

    private static void serializeMeta(JsonObject root, SkullMeta meta) {
        if (meta.hasOwner()) {
            root.addProperty("owner", meta.getOwningPlayer().getUniqueId().toString());
        }
    }

    private static void serializeMeta(JsonObject root, SuspiciousStewMeta meta) {
        if (meta.hasCustomEffects()) {
            root.add("customEffects", GSON.toJsonTree(meta.getCustomEffects()));
        }
    }

    private static JsonObject serializeEnchants(Map<Enchantment, Integer> enchants) {
        JsonObject enchantsObj = new JsonObject();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            enchantsObj.addProperty(entry.getKey().getKey().getKey(), entry.getValue());
        }
        return enchantsObj;
    }

    private static JsonObject serializeFireworkEffect(FireworkEffect effect) {
        JsonObject effectObj = new JsonObject();
        effectObj.addProperty("type", effect.getType().name());
        effectObj.addProperty("flicker", effect.hasFlicker());
        effectObj.addProperty("trail", effect.hasTrail());

        List<Color> colors = effect.getColors();
        if (colors.size() > 0) {
            effectObj.add("colors", GSON.toJsonTree(colors));
        }

        List<Color> fadeColors = effect.getFadeColors();
        if (fadeColors.size() > 0) {
            effectObj.add("fadeColors", GSON.toJsonTree(fadeColors));
        }
        return effectObj;
    }

    private static void deserializeMeta(JsonObject source, BannerMeta meta) throws JsonParseException {
        if (!source.has("patterns")) {
            return;
        }

        JsonArray patternsArray = source.getAsJsonArray("patterns");
        List<Pattern> patterns = deserializeList(patternsArray, e -> deserializePattern(e.getAsJsonObject()));
        meta.setPatterns(patterns);
    }

    private static void deserializeMeta(JsonObject source, BookMeta meta) throws JsonParseException {
        if (source.has("author")) {
            meta.setAuthor(source.get("author").getAsString());
        }

        if (source.has("generation")) {
            Generation generation;
            try {
                generation = Generation.valueOf(source.get("generation").getAsString());
            } catch (IllegalArgumentException ex) {
                throw new JsonParseException("Invalid generation", ex);
            }
            meta.setGeneration(generation);
        }

        if (source.has("pages")) {
            JsonArray pagesArray = source.getAsJsonArray("pages");
            List<String> pages = deserializeList(pagesArray, e -> e.getAsString());
            meta.setPages(pages);
        }
    }

    private static void deserializeMeta(JsonObject source, EnchantmentStorageMeta meta) throws JsonParseException {
        if (!source.has("storedEnchants")) {
            return;
        }

        Map<Enchantment, Integer> storedEnchants = deserializeEnchants(source.getAsJsonObject("storedEnchants"));
        for (Entry<Enchantment, Integer> entry : storedEnchants.entrySet()) {
            meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
        }
    }

    private static void deserializeMeta(JsonObject source, FireworkEffectMeta meta) throws JsonParseException {
        if (source.has("effect")) {
            FireworkEffect effect = deserializeFireworkEffect(source.getAsJsonObject("effect"));
            meta.setEffect(effect);
        }
    }

    private static void deserializeMeta(JsonObject source, FireworkMeta meta) throws JsonParseException {
        int power = source.get("power").getAsInt();
        if (power < 0 || power > 128) {
            throw new JsonParseException("Power is not between 0 and 128");
        }
        meta.setPower(power);

        JsonArray effectsArray = source.getAsJsonArray("effects");
        List<FireworkEffect> effects = deserializeList(effectsArray, e -> deserializeFireworkEffect(e.getAsJsonObject()));
        meta.addEffects(effects);
    }

    private static void deserializeMeta(JsonObject source, LeatherArmorMeta meta) throws JsonParseException {
        Color color = deserializeColor(source.getAsJsonObject("color"));
        meta.setColor(color);
    }

    private static void deserializeMeta(JsonObject source, PotionMeta meta) throws JsonParseException {
        JsonObject dataObj = source.getAsJsonObject("basePotionData");
        PotionType type;
        try {
            type = PotionType.valueOf(dataObj.get("type").getAsString());
        } catch (IllegalArgumentException ex) {
            throw new JsonParseException("Invalid potion type");
        }
        boolean extended = dataObj.get("extended").getAsBoolean();
        boolean upgraded = dataObj.get("upgraded").getAsBoolean();
        meta.setBasePotionData(new PotionData(type, extended, upgraded));

        if (source.has("color")) {
            Color color = deserializeColor(source.getAsJsonObject("color"));
            meta.setColor(color);
        }

        if (source.has("customEffects")) {
            JsonArray effectsArray = source.getAsJsonArray("customEffects");
            List<PotionEffect> effects = deserializeList(effectsArray, e -> deserializePotionEffect(e.getAsJsonObject()));
            for (PotionEffect effect : effects) {
                meta.addCustomEffect(effect, true);
            }
        }
    }

    private static void deserializeMeta(JsonObject source, SkullMeta meta) throws JsonParseException {
        UUID ownerId;
        try {
            ownerId = UUID.fromString(source.get("owner").getAsString());
        } catch (IllegalArgumentException ex) {
            throw new JsonParseException("Invalid owner uuid", ex);
        }

        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerId);
        if (owner == null) {
            throw new JsonParseException("Invalid owner uuid");
        }
        meta.setOwningPlayer(owner);
    }

    private static void deserializeMeta(JsonObject source, SuspiciousStewMeta meta) {
        if (source.has("customEffects")) {
            JsonArray effectsArray = source.getAsJsonArray("customEffects");
            List<PotionEffect> effects = deserializeList(effectsArray, e -> deserializePotionEffect(e.getAsJsonObject()));
            for (PotionEffect effect : effects) {
                meta.addCustomEffect(effect, true);
            }
        }
    }

    private static Map<Enchantment, Integer> deserializeEnchants(JsonObject obj) throws JsonParseException {
        Set<Entry<String, JsonElement>> entries = obj.entrySet();
        Map<Enchantment, Integer> enchants = new HashMap<>(entries.size());
        for (Entry<String, JsonElement> enchant : entries) {
            Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchant.getKey()));
            if (ench == null) {
                throw new JsonParseException("Invalid enchantment name");
            }
            int level = enchant.getValue().getAsInt();
            enchants.put(ench, level);
        }
        return enchants;
    }

    private static Pattern deserializePattern(JsonObject obj) throws JsonParseException {
        DyeColor color;
        try {
            color = DyeColor.valueOf(obj.get("color").getAsString());
        } catch (IllegalArgumentException ex) {
            throw new JsonParseException("Invalid pattern color", ex);
        }

        PatternType pattern;
        try {
            pattern = PatternType.valueOf(obj.get("pattern").getAsString());
        } catch (IllegalArgumentException ex) {
            throw new JsonParseException("Invalid pattern type", ex);
        }
        return new Pattern(color, pattern);
    }

    private static FireworkEffect deserializeFireworkEffect(JsonObject obj) throws JsonParseException {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        if (obj.has("type")) {
            FireworkEffect.Type type;
            try {
                type = FireworkEffect.Type.valueOf(obj.get("type").getAsString());
            } catch (IllegalArgumentException ex) {
                throw new JsonParseException("Invalid effect type", ex);
            }
            builder.with(type);
        }

        if (obj.has("flicker")) {
            builder.flicker(obj.get("flicker").getAsBoolean());
        }

        if (obj.has("trail")) {
            builder.trail(obj.get("trail").getAsBoolean());
        }

        if (obj.has("colors")) {
            JsonArray colorsArray = obj.getAsJsonArray("colors");
            List<Color> colors = deserializeList(colorsArray, e -> deserializeColor(e.getAsJsonObject()));
            builder.withColor(colors);
        }

        if (obj.has("fadeColors")) {
            JsonArray fadeColorsArray = obj.getAsJsonArray("fadeColors");
            List<Color> fadeColors = new ArrayList<>(fadeColorsArray.size());
            for (JsonElement element : fadeColorsArray) {
                Color color = deserializeColor(element.getAsJsonObject());
                fadeColors.add(color);
            }
            builder.withFade(fadeColors);
        }
        return builder.build();
    }

    private static Color deserializeColor(JsonObject obj) throws JsonParseException {
        int red = obj.get("red").getAsInt();
        if (red < 0 || red > 255) {
            throw new JsonParseException("Red is not between 0 and 255");
        }

        int green = obj.get("green").getAsInt();
        if (green < 0 || green > 255) {
            throw new JsonParseException("Green is not between 0 and 255");
        }

        int blue = obj.get("blue").getAsInt();
        if (blue < 0 || blue > 255) {
            throw new JsonParseException("Blue is not between 0 and 255");
        }
        return Color.fromRGB(red, green, blue);
    }

    private static PotionEffect deserializePotionEffect(JsonObject obj) throws JsonParseException {
        int amplifier = obj.get("amplifier").getAsInt();
        int duration = obj.get("duration").getAsInt();
        PotionEffectType type = PotionEffectType.getByName(obj.get("type").getAsString());
        if (type == null) {
            throw new JsonParseException("Invalid potion effect type");
        }
        boolean ambient = obj.get("ambient").getAsBoolean();
        boolean particles = obj.get("particles").getAsBoolean();
        boolean icon = obj.get("icon").getAsBoolean();
        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }

    private static <T> List<T> deserializeList(JsonArray array, Converter<JsonElement, T> converter) {
        List<T> list = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            list.add(converter.convert(element));
        }
        return list;
    }

    private interface Converter<S, R> {
        R convert(S source);
    }
}
