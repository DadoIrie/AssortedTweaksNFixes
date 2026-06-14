package com.dadoirie.assortedtweaksnfixes.utils;

import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;

import java.lang.reflect.Field;

/**
 * Utility for unwrapping layered {@link Language} instances back to the underlying {@link ClientLanguage}.
 *
 * <p>Some mods wrap {@code Language.getInstance()} in a delegating layer at startup. A known example is
 * Server Translations API (STAPI), which injects a {@code SystemDelegatedLanguage} wrapper via a Mixin on
 * {@link Language} itself:
 * <a href="https://github.com/NucleoidMC/Server-Translations/blob/1.21/api/src/main/java/xyz/nucleoid/server/translations/mixin/LanguageMixin.java">LanguageMixin.java</a>
 *
 * <p>This causes issues in any mod that calls {@code Language.getInstance()} and then casts or instanceof-checks
 * the result as {@code ClientLanguage} — the wrapper sits in between and the check fails, breaking translation
 * lookups or config screen rendering (as was the case with puzzleslib's {@code ConfigTranslationsManager},
 * which has since fixed this on their end).
 *
 * <p>This utility is intentionally kept in the codebase even though the original puzzleslib target no longer
 * exists. The same wrapping pattern can appear in other mods, and any future Mixin addressing it can simply
 * call {@link #unwrapToClientLanguage()} rather than duplicating the reflection logic.
 */
public final class LanguageUnwrapHelper {

    private LanguageUnwrapHelper() {}

    /**
     * Attempts to unwrap a potentially layered {@link Language} instance to the underlying {@link ClientLanguage}.
     *
     * <p>Walks the delegation chain by reflectively inspecting {@link Language}-typed fields on each layer.
     * If a {@link ClientLanguage} is found, it is returned immediately. If unwrapping fails or reaches a dead end,
     * the original {@code Language.getInstance()} result is returned as a fallback so callers can handle it
     * gracefully rather than crashing.
     *
     * @return the unwrapped {@link ClientLanguage}, or the original {@link Language} instance if unwrapping failed
     */
    public static Language unwrapToClientLanguage() {
        Language instance = Language.getInstance();
        Language current = instance;

        while (true) {
            if (current instanceof ClientLanguage) {
                return current;
            }

            Language nextLayer = null;
            try {
                for (Field field : current.getClass().getDeclaredFields()) {
                    if (Language.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        Language candidate = (Language) field.get(current);
                        // prevent infinite loop on self-referential wrappers
                        if (candidate != null && candidate != current) {
                            nextLayer = candidate;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
                // reflection failed — treat as dead end
            }

            if (nextLayer == null) {
                break;
            }
            current = nextLayer;
        }

        // unwrapping failed, return original instance and let the caller handle it
        return instance;
    }
}