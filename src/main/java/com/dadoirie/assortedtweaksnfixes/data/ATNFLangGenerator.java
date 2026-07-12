package com.dadoirie.assortedtweaksnfixes.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class ATNFLangGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String LANG_DIR = "src/generated/resources/assets/assortedtweaksnfixes/lang";

    private ATNFLangGenerator() {
    }

    public static void run() throws IOException {
        System.out.println("Resetting lang assets...");
        Path enUs = Path.of(LANG_DIR, "en_us.json");
        Files.createDirectories(enUs.getParent());
        Files.writeString(enUs, "{}");

        merge("en_us", Map.of("itemGroup.assortedtweaksnfixes.main", "Assorted Tweaks N Fixes"));
    }

    public static void merge(String langCode, Map<String, String> entries) throws IOException {
        Path target = Path.of(LANG_DIR, langCode + ".json");
        Files.createDirectories(target.getParent());

        JsonObject lang = Files.exists(target)
                ? JsonParser.parseString(Files.readString(target)).getAsJsonObject()
                : new JsonObject();

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (lang.has(entry.getKey())) {
                throw new IllegalStateException("Duplicate lang key '" + entry.getKey() + "' in " + langCode
                        + ".json: existing='" + lang.get(entry.getKey()).getAsString() + "', new='" + entry.getValue() + "'");
            }
            lang.addProperty(entry.getKey(), entry.getValue());
        }

        Files.writeString(target, GSON.toJson(lang));
    }
}