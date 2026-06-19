package com.dadoirie.assortedtweaksnfixes.client;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixes;
import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.gameoptions.KeyMappingAllAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

@OnlyIn(Dist.CLIENT)
public final class KeybindHandler {

    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(AssortedTweaksNFixes.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String CONFIG_FILE = AssortedTweaksNFixesConstants.MOD_ID + "_keybinds.json";

    private static final String README_HIDING = "HiddenKeyPatterns: Java regex strings matched against keybind translation keys AND category strings. "
            + "Examples: 'key\\.jei\\..*' hides all JEI bindings by key name, 'jei:.*' hides them by category.";
    private static final String README_SYNCS = "syncs: Managed automatically on Options save — do not edit manually.";

    private static KeybindHandler instance;

    private List<Pattern> hiddenKeyPatterns = new ArrayList<>();
    private final Map<String, String> syncs = new LinkedHashMap<>();

    public static KeybindHandler getInstance() {
        if (instance == null) instance = new KeybindHandler();
        return instance;
    }

    private KeybindHandler() {}

    // --- I/O ---

    private Path configPath() {
        return FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
    }

    private void load() {
        Path path = configPath();
        if (!Files.exists(path)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();

            hiddenKeyPatterns = parsePatterns(root);

            syncs.clear();
            if (root.has("syncs")) {
                root.getAsJsonObject("syncs").entrySet()
                        .forEach(e -> syncs.put(e.getKey(), e.getValue().getAsString()));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read keybinds config", e);
        }
    }

    private void save() {
        JsonObject root = new JsonObject();

        // Hiding section
        root.addProperty("_readme_hiding", README_HIDING);

        Path path = configPath();
        if (Files.exists(path)) {
            try {
                JsonObject existing = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
                if (existing.has("HiddenKeyPatterns"))
                    root.add("HiddenKeyPatterns", existing.get("HiddenKeyPatterns"));
            } catch (IOException ignored) {}
        }
        if (!root.has("HiddenKeyPatterns"))
            root.add("HiddenKeyPatterns", new JsonArray());

        // Syncs section
        root.addProperty("_readme_syncs", README_SYNCS);

        JsonObject syncsObj = new JsonObject();
        syncs.forEach(syncsObj::addProperty);
        root.add("syncs", syncsObj);

        try {
            Files.writeString(path, GSON.toJson(root));
        } catch (IOException e) {
            LOGGER.error("Failed to write keybinds config", e);
        }
    }

    private List<Pattern> parsePatterns(JsonObject root) {
        List<Pattern> patterns = new ArrayList<>();
        if (!root.has("HiddenKeyPatterns")) return patterns;
        root.getAsJsonArray("HiddenKeyPatterns").forEach(el -> {
            String s = el.getAsString();
            if (s.startsWith("#")) s = s.substring(1);
            try {
                patterns.add(Pattern.compile(s));
            } catch (Exception e) {
                LOGGER.warn("Invalid pattern in HiddenKeyPatterns: {}", s);
            }
        });
        return patterns;
    }

    // --- Public API ---

    public void init() {
        load();
        save();
    }

    public void restore() {
        load();
        if (syncs.isEmpty()) return;

        Map<String, KeyMapping> all = KeyMappingAllAccessor.assortedtweaksnfixes$getAll();
        int restoredCount = 0;

        for (Map.Entry<String, String> entry : syncs.entrySet()) {
            KeyMapping binding = all.get(entry.getKey());
            if (binding == null || !binding.isDefault()) continue;

            String storedValue = entry.getValue();
            String keyName;
            KeyModifier modifier = KeyModifier.NONE;

            int modSep = storedValue.lastIndexOf(':');
            if (modSep != -1) {
                String possibleModifier = storedValue.substring(modSep + 1);
                try {
                    modifier = KeyModifier.valueOf(possibleModifier);
                    keyName = storedValue.substring(0, modSep);
                } catch (IllegalArgumentException e) {
                    keyName = storedValue;
                }
            } else {
                keyName = storedValue;
            }

            binding.setKeyModifierAndCode(modifier, InputConstants.getKey(keyName));
            restoredCount++;
        }

        if (restoredCount > 0) {
            LOGGER.info("[{}] Restored {} keybind{} from config",
                    AssortedTweaksNFixesConstants.MOD_ID,
                    restoredCount,
                    restoredCount == 1 ? "" : "s");
            Minecraft.getInstance().options.save();
        }
    }

    public void syncKeybinds() {
        load();
        for (KeyMapping binding : KeyMappingAllAccessor.assortedtweaksnfixes$getAll().values()) {
            String name = binding.getName();
            if (!binding.isDefault()) {
                String value = binding.saveString();
                KeyModifier modifier = binding.getKeyModifier();
                if (modifier != KeyModifier.NONE) value += ":" + modifier.name();
                syncs.put(name, value);
            } else {
                syncs.remove(name);
            }
        }
        save();
    }

    public boolean isKeyHidden(String value) {
        return hiddenKeyPatterns.stream().anyMatch(p -> p.matcher(value).matches());
    }
}