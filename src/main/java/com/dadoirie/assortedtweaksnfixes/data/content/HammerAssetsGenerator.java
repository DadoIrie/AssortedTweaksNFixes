package com.dadoirie.assortedtweaksnfixes.data.content;

import com.dadoirie.assortedtweaksnfixes.data.ATNFLangGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class HammerAssetsGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String ASSETS_OUT = "src/generated/resources/overlay_hammer/assets/assortedtweaksnfixes";

    public static void run() throws IOException {
        System.out.println("Starting hammer assets generation...");
        generateItemModel();
        generateLang();
        System.out.println("Hammer assets gen complete.");
    }

    private static void generateItemModel() throws IOException {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/handheld");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "assortedtweaksnfixes:item/hammer");
        model.add("textures", textures);

        write(ASSETS_OUT + "/models/item/hammer.json", GSON.toJson(model));
    }

    private static void generateLang() throws IOException {
        generateEnUs();
    }

    private static void generateEnUs() throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("item.assortedtweaksnfixes.hammer", "Hammer");
        entries.put("item.assortedtweaksnfixes.hammer.tooltip.summary", "Can be used to _press_ items.");
        entries.put("item.assortedtweaksnfixes.hammer.tooltip.condition1", "When Used");
        entries.put("item.assortedtweaksnfixes.hammer.tooltip.behaviour1", "Presses items held in the _offhand_.");
        entries.put("assortedtweaksnfixes.recipe.hammering", "Hammering");
        ATNFLangGenerator.merge("en_us", entries);
    }

    private static void write(String path, String content) throws IOException {
        Path target = Path.of(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content);
    }
}