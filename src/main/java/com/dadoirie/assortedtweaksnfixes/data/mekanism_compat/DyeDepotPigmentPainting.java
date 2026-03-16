package com.dadoirie.assortedtweaksnfixes.data.mekanism_compat;

import com.ninni.dye_depot.registry.DDDyes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.dadoirie.assortedtweaksnfixes.data.ModDataGenerators.RecipeData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class DyeDepotPigmentPainting {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static final Path RECIPE_OUTPUT = Path.of(
            "src/generated/resources/overlay_mek_dd/data/assortedtweaksnfixes/recipe/mekanism_compat/dye_depot_compat/painting"
    );

    public static void init(Map<String, RecipeData> additional_ingredients) throws IOException {
        for (Map.Entry<String, RecipeData> entry : additional_ingredients.entrySet()) {
            String additional_ingredient = entry.getKey();
            int paintAmount = entry.getValue().paint();
            String fileName = entry.getValue().file(); // get the filename
            generateRecipe(additional_ingredient, paintAmount, fileName);
        }
    }

    private static void generateRecipe(String additional_ingredient, int amount, String fileName) throws IOException {

        Path additionalIngredientsFolder = RECIPE_OUTPUT.resolve(additional_ingredient);
        File folder = additionalIngredientsFolder.toFile();
        if (!folder.exists() && !folder.mkdirs())
            throw new IOException("Failed to create folder: " + folder);

        for (DDDyes dye : DDDyes.values()) {

            String name = dye.getSerializedName();
            String pigment = "dd_" + name;

            JsonObject json = getJsonObject(name, pigment, additional_ingredient, amount, fileName);

            File outFile = additionalIngredientsFolder.resolve(name + "_paint.json").toFile();
            try (FileWriter writer = new FileWriter(outFile)) {
                GSON.toJson(json, writer);
            }

            System.out.println("Generated painting recipe: " + outFile);
        }
    }

    private static @NotNull JsonObject getJsonObject(String name, String pigment, String additional_ingredient, int amount, String fileName) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "mekanism:painting");

        JsonObject chemicalInput = new JsonObject();
        chemicalInput.addProperty("amount", amount);
        chemicalInput.addProperty("chemical", "assortedtweaksnfixes:" + pigment);
        json.add("chemical_input", chemicalInput);

        JsonObject itemInput = getJsonObject(name, additional_ingredient, fileName);
        json.add("item_input", itemInput);

        JsonObject output = new JsonObject();
        output.addProperty("count", 1);
        output.addProperty("id", "dye_depot:" + name + "_" + additional_ingredient);
        json.add("output", output);

        json.addProperty("per_tick_usage", false);

        return json;
    }

    private static @NotNull JsonObject getJsonObject(String name, String additional_ingredient, String fileName) {
        JsonObject itemInput = new JsonObject();
        itemInput.addProperty("count", 1);
        if ("dye".equals(additional_ingredient)) {
            itemInput.addProperty("item", "mekanism:dye_base");
        } else {
            itemInput.addProperty("type", "neoforge:difference");

            JsonObject base = new JsonObject();
            base.addProperty("tag", "mekanism:colorable/" + fileName.replace(".json", ""));

            JsonObject subtracted = new JsonObject();
            subtracted.addProperty("item", "dye_depot:" + name + "_" + additional_ingredient);

            itemInput.add("base", base);
            itemInput.add("subtracted", subtracted);
        }
        return itemInput;
    }
}