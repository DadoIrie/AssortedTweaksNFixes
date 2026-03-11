package com.dadoirie.assortedtweaksnfixes.data.mekanism_compat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class PigmentMixerRemoval {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static void removeMekanismNamespacePigmentMixing() throws IOException {
        Path outDir = Path.of("src/generated/resources/data/assortedtweaksnfixes/recipe_modifier");
        File folder = outDir.toFile();
        if (!folder.exists() && !folder.mkdirs())
            throw new IOException("Failed to create recipe modifiers folder: " + folder);

        JsonObject root = getJsonObject();

        File outFile = outDir.resolve("remove_mekanism_pigment_mixing.json").toFile();
        try (FileWriter writer = new FileWriter(outFile)) {
            GSON.toJson(root, writer);
        }

        System.out.println("Generated recipe modifier to remove Mekanism pigment mixing recipes: " + outFile);
    }

    private static @NotNull JsonObject getJsonObject() {
        JsonObject root = new JsonObject();

        JsonObject targetRecipes = getObject();
        root.add("target_recipes", targetRecipes);

        JsonArray modifiers = new JsonArray();
        JsonObject remove = new JsonObject();
        remove.addProperty("type", "remove_recipe");
        modifiers.add(remove);
        root.add("modifiers", modifiers);
        return root;
    }

    private static @NotNull JsonObject getObject() {
        JsonObject targetRecipes = new JsonObject();
        targetRecipes.addProperty("type", "and");

        JsonArray filters = new JsonArray();
        JsonObject nsFilter = new JsonObject();
        nsFilter.addProperty("type", "namespace_equals");
        nsFilter.addProperty("namespace", "mekanism");

        JsonObject typeFilter = new JsonObject();
        typeFilter.addProperty("type", "is_recipe_type");
        typeFilter.addProperty("recipe_type", "mekanism:pigment_mixing");

        filters.add(nsFilter);
        filters.add(typeFilter);

        targetRecipes.add("filters", filters);
        return targetRecipes;
    }
}