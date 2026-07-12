package com.dadoirie.assortedtweaksnfixes.data.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class HammerDataGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String DATA_OUT = "src/generated/resources/overlay_hammer/data";

    private static final Map<String, String> HAMMERING_RECIPES = new LinkedHashMap<>();

    static {
        HAMMERING_RECIPES.put("iron_sheet", "minecraft:iron_ingot");
        HAMMERING_RECIPES.put("copper_sheet", "minecraft:copper_ingot");
    }

    public static void run() throws IOException {
        System.out.println("Starting hammer data generation...");
        generateCraftingRecipe();
        generateHammeringRecipes();
        System.out.println("Hammer data gen complete.");
    }

    private static void generateCraftingRecipe() throws IOException {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");

        JsonArray pattern = new JsonArray();
        pattern.add("AIA");
        pattern.add("ISI");
        pattern.add("SIA");
        recipe.add("pattern", pattern);

        JsonObject key = new JsonObject();
        JsonObject rods = new JsonObject();
        rods.addProperty("tag", "c:rods/wooden");
        key.add("S", rods);
        JsonObject andesite = new JsonObject();
        andesite.addProperty("item", "create:andesite_alloy");
        key.add("A", andesite);
        JsonObject iron = new JsonObject();
        iron.addProperty("tag", "c:ingots/iron");
        key.add("I", iron);
        recipe.add("key", key);

        JsonObject result = new JsonObject();
        result.addProperty("id", "assortedtweaksnfixes:hammer");
        recipe.add("result", result);

        write(DATA_OUT + "/assortedtweaksnfixes/recipe/crafting/hammer.json", GSON.toJson(recipe));
    }

    private static void generateHammeringRecipes() throws IOException {
        for (Map.Entry<String, String> entry : HAMMERING_RECIPES.entrySet()) {
            JsonObject recipe = new JsonObject();
            recipe.addProperty("type", "assortedtweaksnfixes:hammering");

            JsonArray ingredients = new JsonArray();
            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("item", entry.getValue());
            ingredients.add(ingredient);
            recipe.add("ingredients", ingredients);

            JsonArray results = new JsonArray();
            JsonObject result = new JsonObject();
            result.addProperty("id", "create:" + entry.getKey());
            result.addProperty("chance", 0.8f);
            results.add(result);
            recipe.add("results", results);

            write(DATA_OUT + "/assortedtweaksnfixes/recipe/hammering/" + entry.getKey() + ".json", GSON.toJson(recipe));
        }
    }

    private static void write(String path, String content) throws IOException {
        Path target = Path.of(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content);
    }
}