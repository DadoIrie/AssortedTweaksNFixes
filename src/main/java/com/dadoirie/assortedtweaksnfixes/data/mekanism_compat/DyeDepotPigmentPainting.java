package com.dadoirie.assortedtweaksnfixes.data.mekanism_compat;

import com.ninni.dye_depot.registry.DDDyes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
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
            "src/generated/resources/data/assortedtweaksnfixes/recipe/mekanism_compat/dye_depot_compat/painting"
    );

    public static void init(Map<String, RecipeData> types) throws IOException {
        for (Map.Entry<String, RecipeData> entry : types.entrySet()) {
            String type = entry.getKey();
            int paintAmount = entry.getValue().paint();
            String fileName = entry.getValue().file(); // get the filename
            generateType(type, paintAmount, fileName);
        }
    }

    private static void generateType(String type, int amount, String fileName) throws IOException {

        Path typeFolder = RECIPE_OUTPUT.resolve(type);
        File folder = typeFolder.toFile();
        if (!folder.exists() && !folder.mkdirs())
            throw new IOException("Failed to create folder: " + folder);

        for (DDDyes dye : DDDyes.values()) {

            String name = dye.getSerializedName();
            String pigment = "dd_" + name;

            JsonObject json = getJsonObject(name, pigment, type, amount, fileName);

            File outFile = typeFolder.resolve(name + "_paint.json").toFile();
            try (FileWriter writer = new FileWriter(outFile)) {
                GSON.toJson(json, writer);
            }

            System.out.println("Generated painting recipe: " + outFile);
        }
    }

    private static @NotNull JsonObject getJsonObject(String name, String pigment, String type, int amount, String fileName) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "mekanism:painting");

        JsonArray conditions = new JsonArray();
        JsonObject mek = new JsonObject();
        mek.addProperty("type", "neoforge:mod_loaded");
        mek.addProperty("modid", "mekanism");
        JsonObject dd = new JsonObject();
        dd.addProperty("type", "neoforge:mod_loaded");
        dd.addProperty("modid", "dye_depot");
        conditions.add(mek);
        conditions.add(dd);
        json.add("neoforge:conditions", conditions);

        JsonObject chemicalInput = new JsonObject();
        chemicalInput.addProperty("amount", amount);
        chemicalInput.addProperty("chemical", "mekanism:" + pigment);
        json.add("chemical_input", chemicalInput);

        JsonObject itemInput = getJsonObject(name, type, fileName);
        json.add("item_input", itemInput);

        JsonObject output = new JsonObject();
        output.addProperty("count", 1);
        output.addProperty("id", "dye_depot:" + name + "_" + type);
        json.add("output", output);

        json.addProperty("per_tick_usage", false);

        return json;
    }

    private static @NotNull JsonObject getJsonObject(String name, String type, String fileName) {
        JsonObject itemInput = new JsonObject();
        itemInput.addProperty("count", 1);
        if ("dye".equals(type)) {
            itemInput.addProperty("item", "mekanism:dye_base");
        } else {
            itemInput.addProperty("type", "neoforge:difference");

            JsonObject base = new JsonObject();
            base.addProperty("tag", "mekanism:colorable/" + fileName.replace(".json", ""));

            JsonObject subtracted = new JsonObject();
            subtracted.addProperty("item", "dye_depot:" + name + "_" + type);

            itemInput.add("base", base);
            itemInput.add("subtracted", subtracted);
        }
        return itemInput;
    }
}