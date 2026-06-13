package com.dadoirie.assortedtweaksnfixes.data.furnace_tank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FurnaceTankAssetsProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String ASSETS_OUT = "src/generated/resources/assets/assortedtweaksnfixes/";
    private static final String BLOCKSTATES_OUT = ASSETS_OUT + "blockstates/";
    private static final String BLOCK_MODELS_OUT = ASSETS_OUT + "models/block/";
    private static final String ITEM_MODELS_OUT = ASSETS_OUT + "models/item/";

    private record FurnaceVariant(
            String id,
            String frontOff,
            String frontOn,
            String side,
            String top,
            String bottom
    ) {}

    private static final FurnaceVariant[] VARIANTS = {
            new FurnaceVariant(
                    "furnace_tank",
                    "minecraft:block/furnace_front",
                    "minecraft:block/furnace_front_on",
                    "minecraft:block/furnace_side",
                    "minecraft:block/furnace_top",
                    "minecraft:block/furnace_top"
            ),
            new FurnaceVariant(
                    "blast_furnace_tank",
                    "minecraft:block/blast_furnace_front",
                    "minecraft:block/blast_furnace_front_on",
                    "minecraft:block/blast_furnace_side",
                    "minecraft:block/blast_furnace_top",
                    "minecraft:block/blast_furnace_top"
            ),
            new FurnaceVariant(
                    "smoker_tank",
                    "minecraft:block/smoker_front",
                    "minecraft:block/smoker_front_on",
                    "minecraft:block/smoker_side",
                    "minecraft:block/smoker_top",
                    "minecraft:block/smoker_bottom"
            )
    };

    public static void run() throws IOException {
        for (FurnaceVariant variant : VARIANTS) {
            generateBlockstate(variant);
            generateBlockModel(variant, false);
            generateBlockModel(variant, true);
            generateItemModel(variant);
        }
    }

    private static void generateBlockstate(FurnaceVariant variant) throws IOException {
        JsonObject root = new JsonObject();
        JsonObject variants = new JsonObject();

        for (boolean lit : new boolean[]{false, true}) {
            for (String facing : new String[]{"north", "south", "east", "west"}) {
                String key = "facing=" + facing + ",lit=" + lit;
                String modelId = "assortedtweaksnfixes:block/" + variant.id() + (lit ? "_on" : "");

                JsonObject entry = new JsonObject();
                entry.addProperty("model", modelId);

                int y = switch (facing) {
                    case "south" -> 180;
                    case "east"  -> 90;
                    case "west"  -> 270;
                    default      -> 0;
                };
                if (y != 0) entry.addProperty("y", y);

                variants.add(key, entry);
            }
        }

        root.add("variants", variants);
        writeJson(new File(BLOCKSTATES_OUT + variant.id() + ".json"), root);
    }

    private static void generateBlockModel(FurnaceVariant variant, boolean lit) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");

        JsonObject textures = new JsonObject();
        textures.addProperty("front", lit ? variant.frontOn() : variant.frontOff());
        textures.addProperty("side",  variant.side());
        textures.addProperty("top",   variant.top());
        textures.addProperty("bottom", variant.bottom());
        textures.addProperty("particle", variant.side());
        root.add("textures", textures);

        JsonArray elements = new JsonArray();
        elements.add(buildCube());
        root.add("elements", elements);

        String suffix = lit ? "_on" : "";
        writeJson(new File(BLOCK_MODELS_OUT + variant.id() + suffix + ".json"), root);
    }

    private static JsonObject buildCube() {
        JsonObject el = new JsonObject();
        el.add("from", jsonArray(0, 0, 0));
        el.add("to",   jsonArray(16, 16, 16));

        JsonObject faces = new JsonObject();
        faces.add("north", buildFace("#front",  "north"));
        faces.add("south", buildFace("#side",   "south"));
        faces.add("east",  buildFace("#side",   "east"));
        faces.add("west",  buildFace("#side",   "west"));
        faces.add("up",    buildFace("#top",    "up"));
        faces.add("down",  buildFace("#bottom", "down"));
        el.add("faces", faces);

        return el;
    }

    private static JsonObject buildFace(String texture, String cullface) {
        JsonObject face = new JsonObject();
        face.add("uv", jsonArray(0, 0, 16, 16));
        face.addProperty("texture", texture);
        face.addProperty("cullface", cullface);
        return face;
    }

    private static void generateItemModel(FurnaceVariant variant) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "assortedtweaksnfixes:block/" + variant.id());
        writeJson(new File(ITEM_MODELS_OUT + variant.id() + ".json"), root);
    }

    private static JsonArray jsonArray(float... values) {
        JsonArray arr = new JsonArray();
        for (float v : values) arr.add(v);
        return arr;
    }

    private static void writeJson(File file, JsonObject content) throws IOException {
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directories: " + parent.getAbsolutePath());
        }
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(content, writer);
        }
    }
}