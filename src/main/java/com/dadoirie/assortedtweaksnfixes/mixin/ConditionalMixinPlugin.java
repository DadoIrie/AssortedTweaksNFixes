package com.dadoirie.assortedtweaksnfixes.mixin;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;

public class ConditionalMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(ConditionalMixinPlugin.class);

    private static final Map<String, Supplier<Boolean>> TARGET_MODS = Map.of(
        "com.dadoirie." + AssortedTweaksNFixesConstants.MOD_ID + ".mixin.accessories.", () -> isModLoaded("accessories")
    );
    private static final LinkedHashMap<String, Boolean> MIXIN_CONFIG = new LinkedHashMap<>();

    private static boolean isModLoaded(String modId) {
        if (LoadingModList.get() != null) {
            return LoadingModList.get().getModFileById(modId) != null;
        }
        if (ModList.get() != null) {
            return ModList.get().isLoaded(modId);
        }
        return false;
    }

    private void createTomlConfig() throws IOException {
        LinkedHashMap<String, Boolean> mixinEntries = parseMixinJson();
        Path configPath = Paths.get("config", AssortedTweaksNFixesConstants.MOD_ID + "_mixins.toml");
        Files.createDirectories(configPath.getParent());

        LinkedHashMap<String, Boolean> existingConfig = new LinkedHashMap<>();
        if (Files.exists(configPath)) {
            List<String> lines = Files.readAllLines(configPath);
            for (String line : lines) {
                line = line.trim();
                if (line.contains("=") && !line.startsWith("#")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String valueStr = parts[1].trim();

                    boolean value;
                    if ("true".equalsIgnoreCase(valueStr)) {
                        value = true;
                    } else if ("false".equalsIgnoreCase(valueStr)) {
                        value = false;
                    } else {
                        value = true;
                    }

                    existingConfig.put(key, value);
                }
            }
        }
        for (Map.Entry<String, Boolean> entry : mixinEntries.entrySet()) {
            if (!existingConfig.containsKey(entry.getKey())) {
                existingConfig.put(entry.getKey(), true);
            }
        }
        MIXIN_CONFIG.clear();
        MIXIN_CONFIG.putAll(existingConfig);
        List<String> lines = new ArrayList<>();
        for (String mixinPath : mixinEntries.keySet()) {
            Boolean value = MIXIN_CONFIG.get(mixinPath);
            if (value != null) {
                lines.add(mixinPath + " = " + value);
            }
        }
        Files.write(configPath, lines);
    }
    private LinkedHashMap<String, Boolean> parseMixinJson() throws IOException {
        LinkedHashMap<String, Boolean> mixinNames = new LinkedHashMap<>();
        InputStream mixinEntries = getClass().getClassLoader().getResourceAsStream(AssortedTweaksNFixesConstants.MOD_ID + ".mixins.json");
        JsonObject json = JsonParser.parseReader(new InputStreamReader(mixinEntries)).getAsJsonObject();
        JsonArray clientMixins = json.getAsJsonArray("client");
        if (clientMixins != null) {
            for (JsonElement element : clientMixins) {
                String mixinPath = element.getAsString();
                mixinNames.put(mixinPath, true);
            }
        }
        JsonArray serverMixins = json.getAsJsonArray("server");
        if (serverMixins != null) {
            for (JsonElement element : serverMixins) {
                String mixinPath = element.getAsString();
                mixinNames.put(mixinPath, true);
            }
        }
        return mixinNames;
    }

    @Override
    public void onLoad(String mixinPackage) {
        LOGGER.info("preparing: {}", mixinPackage);
        try {
            createTomlConfig();
        } catch (IOException e) {
            LOGGER.error("Failed to create TOML config", e);
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixinPath = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        if (MIXIN_CONFIG.containsKey(mixinPath) && !MIXIN_CONFIG.get(mixinPath)) {
            LOGGER.info("skipping {} (disabled in config)", mixinPath);
            return false;
        }

        for (String configKey : MIXIN_CONFIG.keySet()) {
            if (mixinClassName.endsWith(configKey) && !MIXIN_CONFIG.get(configKey)) {
                LOGGER.info("skipping {} (disabled in config)", configKey);
                return false;
            }
        }

        // Then check target mod conditions
        for (var entry : TARGET_MODS.entrySet()) {
            if (mixinClassName.startsWith(entry.getKey())) {
                boolean shouldApply = entry.getValue().get();
                String prefix = entry.getKey().substring(0, entry.getKey().length() - 1);
                String lastPackageName = prefix.substring(prefix.lastIndexOf('.') + 1);
                String applyMixin = mixinClassName.substring(prefix.length());
                LOGGER.info("applying {}{}", lastPackageName, applyMixin);
                return shouldApply;
            }
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
