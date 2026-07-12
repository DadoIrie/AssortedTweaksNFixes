package com.dadoirie.assortedtweaksnfixes.data.compats.petrochem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ElectrolyzingRecipeGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String DATA_OUT = "src/generated/resources/overlay_pc_ee/data/petrochem/recipe/electrolyzing";

    public static void run() throws IOException {
        System.out.println("Starting Petrochem x Electro-Energetics electrolyzing recipe generation...");
        generateChlorAlkali();
        generateWaterElectrolysis();
        generateBasicDesalting();
        System.out.println("Petrochem x Electro-Energetics recipe gen complete.");
    }

    private static void generateChlorAlkali() throws IOException {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "petrochem:electrolyzing");
        recipe.addProperty("kilowatts", 5.0);
        recipe.addProperty("voltage", 220.0);

        JsonArray ingredients = new JsonArray();
        JsonObject saltDust = new JsonObject();
        saltDust.addProperty("item", "petrochem:salt_dust");
        ingredients.add(saltDust);
        ingredients.add(fluidIngredient("minecraft:water", 500));
        recipe.add("ingredients", ingredients);

        JsonArray results = new JsonArray();
        JsonObject causticSoda = new JsonObject();
        causticSoda.addProperty("id", "petrochem:caustic_soda");
        results.add(causticSoda);
        results.add(itemResult("petrochem:chlorine", 250, null));
        recipe.add("results", results);

        recipe.addProperty("heat_requirement", "heated");
        recipe.addProperty("processing_time", 240);

        write(DATA_OUT + "/chlor_alkali.json", GSON.toJson(recipe));
    }

    private static void generateWaterElectrolysis() throws IOException {
        JsonObject recipe = new JsonObject();
        recipe.add("neoforge:conditions", modLoadedCondition("petrochem_expert", false));
        recipe.addProperty("type", "petrochem:electrolyzing");
        recipe.addProperty("kilowatts", 4.0);
        recipe.addProperty("voltage", 220.0);

        JsonArray ingredients = new JsonArray();
        ingredients.add(fluidIngredient("minecraft:water", 300));
        recipe.add("ingredients", ingredients);

        JsonArray results = new JsonArray();
        results.add(itemResult("petrochem:hydrogen", 200, null));
        results.add(itemResult("petrochem:oxygen", 100, null));
        recipe.add("results", results);

        recipe.addProperty("processing_time", 200);

        write(DATA_OUT + "/water_electrolysis.json", GSON.toJson(recipe));
    }

    private static void generateBasicDesalting() throws IOException {
        JsonObject recipe = new JsonObject();
        recipe.add("neoforge:conditions", modLoadedCondition("petrochem_expert", true));
        recipe.addProperty("type", "petrochem:electrolyzing");
        recipe.addProperty("kilowatts", 3.5);
        recipe.addProperty("voltage", 220.0);

        JsonArray ingredients = new JsonArray();
        ingredients.add(fluidIngredient("petrochem:petroleum", 500));
        recipe.add("ingredients", ingredients);

        JsonArray results = new JsonArray();
        results.add(itemResult("petrochem:salt_dust", 0, 0.5));
        results.add(itemResult("petrochem:desalted_oil", 500, null));
        recipe.add("results", results);

        recipe.addProperty("heat_requirement", "heated");
        recipe.addProperty("processing_time", 200);

        write(DATA_OUT + "/basic_desalting.json", GSON.toJson(recipe));
    }

    private static JsonObject fluidIngredient(String fluid, int amount) {
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("type", "neoforge:single");
        ingredient.addProperty("amount", amount);
        ingredient.addProperty("fluid", fluid);
        return ingredient;
    }

    /**
     * amount == 0 -> "amount" key omitted (default count 1, e.g. caustic soda).
     * chance == null -> "chance" key omitted.
     */
    private static JsonObject itemResult(String id, int amount, Double chance) {
        JsonObject result = new JsonObject();
        if (chance != null)
            result.addProperty("chance", chance);
        if (amount > 0)
            result.addProperty("amount", amount);
        result.addProperty("id", id);
        return result;
    }

    private static JsonArray modLoadedCondition(String modId, boolean negate) {
        JsonObject modLoaded = new JsonObject();
        modLoaded.addProperty("type", "neoforge:mod_loaded");
        modLoaded.addProperty("modid", modId);

        JsonObject condition = modLoaded;
        if (negate) {
            condition = new JsonObject();
            condition.addProperty("type", "neoforge:not");
            condition.add("value", modLoaded);
        }

        JsonArray conditions = new JsonArray();
        conditions.add(condition);
        return conditions;
    }

    private static void write(String path, String content) throws IOException {
        Path target = Path.of(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content);
    }
}