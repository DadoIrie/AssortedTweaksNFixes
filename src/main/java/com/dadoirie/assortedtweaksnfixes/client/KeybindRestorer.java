package com.dadoirie.assortedtweaksnfixes.client;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixes;
import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.gameoptions.KeyMappingAllAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class KeybindRestorer {

    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(AssortedTweaksNFixes.class);
    private static KeybindRestorer instance;

    public static KeybindRestorer getInstance() {
        if (instance == null) instance = new KeybindRestorer();
        return instance;
    }

    private KeybindRestorer() {}

    public void restore() {
        Path syncFile = FMLPaths.CONFIGDIR.get()
                .resolve(AssortedTweaksNFixesConstants.MOD_ID + "_keysync.txt");

        if (!Files.exists(syncFile)) return;

        Map<String, String> overrides = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(syncFile)) {
                if (line.isBlank() || line.startsWith("#")) continue;
                int sep = line.indexOf('=');
                if (sep == -1) continue;
                overrides.put(line.substring(0, sep).trim(), line.substring(sep + 1).trim());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read keysync file during restore", e);
            return;
        }

        if (overrides.isEmpty()) return;

        Map<String, KeyMapping> all = KeyMappingAllAccessor.assortedtweaksnfixes$getAll();
        int restoredCount = 0;

        for (Map.Entry<String, String> entry : overrides.entrySet()) {
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
            LOGGER.info("[{}] Restored {} keybind{} from shadow config",
                    AssortedTweaksNFixesConstants.MOD_ID,
                    restoredCount,
                    restoredCount == 1 ? "" : "s");
            Minecraft.getInstance().options.save();
        }
    }
}