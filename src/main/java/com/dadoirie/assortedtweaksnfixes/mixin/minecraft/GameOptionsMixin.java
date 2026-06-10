package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixes;
import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.gameoptions.KeyMappingAllAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(Options.class)
public class GameOptionsMixin {

    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(AssortedTweaksNFixes.class);

    @Inject(method = "save", at = @At("TAIL"))
    private void onSave(CallbackInfo ci) {
        syncKeybinds();
    }

    private void syncKeybinds() {
        Path syncFile = FMLPaths.CONFIGDIR.get()
                .resolve(AssortedTweaksNFixesConstants.MOD_ID + "_keysync.txt");

        Map<String, String> overrides = new LinkedHashMap<>();
        if (Files.exists(syncFile)) {
            try {
                for (String line : Files.readAllLines(syncFile)) {
                    if (line.isBlank() || line.startsWith("#")) continue;
                    int sep = line.indexOf('=');
                    if (sep == -1) continue;
                    overrides.put(line.substring(0, sep).trim(), line.substring(sep + 1).trim());
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read keysync file", e);
            }
        }

        for (KeyMapping binding : KeyMappingAllAccessor.assortedtweaksnfixes$getAll().values()) {
            String name = binding.getName();

            if (!binding.isDefault()) {
                String value = binding.saveString();
                KeyModifier modifier = binding.getKeyModifier();
                if (modifier != KeyModifier.NONE) {
                    value += ":" + modifier.name();
                }
                overrides.put(name, value);
            } else {
                overrides.remove(name);
            }
        }

        try {
            List<String> lines = new ArrayList<>();
            lines.add("# Keybind overrides managed by " + AssortedTweaksNFixesConstants.MOD_ID);
            lines.add("# Format: binding_name=key_translation_key[:MODIFIER]");
            lines.add("# Do not edit manually unless you know what you're doing");
            lines.add("");
            for (Map.Entry<String, String> entry : overrides.entrySet()) {
                lines.add(entry.getKey() + "=" + entry.getValue());
            }
            Files.write(syncFile, lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Failed to write keysync file", e);
        }
    }
}