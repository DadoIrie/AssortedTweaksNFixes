package com.dadoirie.assortedtweaksnfixes.data.mekanism_compat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ninni.dye_depot.registry.DDDyes;
import com.dadoirie.assortedtweaksnfixes.data.ModDataGenerators.RecipeData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class DyeDepotPigmentExtraction {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static final Path RECIPE_OUTPUT = Path.of(
            "src/generated/resources/overlay_mek_dd/data/assortedtweaksnfixes/recipe/mekanism_compat/dye_depot_compat/pigment_extracting"
    );

    private static final Path TAG_OUTPUT = Path.of(
            "src/generated/resources/overlay_mek_dd/data/mekanism/tags/item/colorable"
    );

    public static void init(Map<String, RecipeData> types) throws IOException {
        for (Map.Entry<String, RecipeData> entry : types.entrySet()) {
            String type = entry.getKey();
            int extractAmount = entry.getValue().extract();
            String fileName = entry.getValue().file();
            generateType(type, extractAmount, fileName);
        }
    }

    private static void generateType(String type, int amount, String fileName) throws IOException {

        Path recipeFolder = RECIPE_OUTPUT.resolve(type);
        File recipeDir = recipeFolder.toFile();
        if (!recipeDir.exists() && !recipeDir.mkdirs())
            throw new IOException("Failed to create folder: " + recipeDir);

        JsonArray tagValues = new JsonArray();

        for (DDDyes dye : DDDyes.values()) {
            String name = dye.getSerializedName();
            String pigment = "dd_" + name;

            JsonObject json = getJsonObject(name, pigment, type, amount, fileName);

            File outFile = recipeFolder.resolve(pigment + ".json").toFile();
            try (FileWriter writer = new FileWriter(outFile)) {
                GSON.toJson(json, writer);
            }

            if (!"dye".equals(type)) {
                tagValues.add("dye_depot:" + name + "_" + type);
            }

            System.out.println("Generated recipe: " + outFile);
        }

        if (!"dye".equals(type)) {
            Path tagFile = TAG_OUTPUT.resolve(fileName);
            File file = tagFile.toFile();
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                throw new IOException("Failed creating tag folder: " + file.getParent());

            JsonObject json = new JsonObject();
            json.add("values", tagValues);

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(json, writer);
            }

            System.out.println("Generated tag: " + file);
        }
    }

    private static @NotNull JsonObject getJsonObject(String name, String pigment, String type, int amount, String fileName) {

        JsonObject json = new JsonObject();
        json.addProperty("type", "mekanism:pigment_extracting");

        JsonObject input = getJsonObject(name, type, fileName);

        json.add("input", input);

        JsonObject output = new JsonObject();
        output.addProperty("amount", amount);
        output.addProperty("id", "assortedtweaksnfixes:" + pigment);
        json.add("output", output);

        return json;
    }

    private static @NotNull JsonObject getJsonObject(String name, String type, String fileName) {
        JsonObject input = new JsonObject();
        input.addProperty("count", 1);

        if ("dye".equals(type)) {
            input.addProperty("tag", "c:dyes/" + name);
        } else {
            input.addProperty("type", "neoforge:intersection");

            JsonArray children = new JsonArray();
            JsonObject colorable = new JsonObject();
            colorable.addProperty("tag", "mekanism:colorable/" + fileName.replace(".json", ""));
            JsonObject dyed = new JsonObject();
            dyed.addProperty("tag", "c:dyed/" + name);

            children.add(colorable);
            children.add(dyed);
            input.add("children", children);
        }
        return input;
    }
}