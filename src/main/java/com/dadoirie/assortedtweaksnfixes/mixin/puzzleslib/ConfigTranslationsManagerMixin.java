package com.dadoirie.assortedtweaksnfixes.mixin.puzzleslib;

import fuzs.puzzleslib.impl.config.ConfigTranslationsManager;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;

@Mixin(ConfigTranslationsManager.class)
public class ConfigTranslationsManagerMixin {

    @Redirect(
            method = "lambda$onAddResourcePackReloadListeners$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/locale/Language;getInstance()Lnet/minecraft/locale/Language;")
    )
    private static Language stapi$feedUnwrappedLanguageToPuzzlesLib() {
        Language instance = Language.getInstance();
        Language current = instance;

        // Universally unwrap any layers (Sinytra Connector, STAPI, etc.)
        while (true) {
            if (current instanceof ClientLanguage) {
                // We found the raw ClientLanguage core! Give it to Puzzles Lib.
                return current;
            }

            Language nextLayer = null;
            try {
                for (Field field : current.getClass().getDeclaredFields()) {
                    if (Language.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        Language candidate = (Language) field.get(current);

                        // Prevent infinite loops if a class holds a reference to itself
                        if (candidate != null && candidate != current) {
                            nextLayer = candidate;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
            }

            if (nextLayer == null) {
                break; // Dead end, exit the loop
            }
            current = nextLayer;
        }

        // If we couldn't unwrap it, return the original instance and let it fail naturally
        return instance;
    }
}