package com.dadoirie.assortedtweaksnfixes.data.refined_storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RefinedStorageDataGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final List<String> COLORS = List.of(
            "maroon", "rose", "coral", "indigo", "navy", "slate",
            "olive", "amber", "beige", "teal", "mint", "aqua",
            "verdant", "forest", "ginger", "tan"
    );

    private record BlockDefinition(String base, String tagName) {}

    private static final List<BlockDefinition> BLOCKS = List.of(
            new BlockDefinition("autocrafter_manager", "autocrafter_managers"),
            new BlockDefinition("autocrafter", "autocrafters"),
            new BlockDefinition("autocrafting_monitor", "autocrafting_monitors"),
            new BlockDefinition("cable", "cables"),
            new BlockDefinition("constructor", "constructors"),
            new BlockDefinition("controller", "controllers"),
            new BlockDefinition("crafting_grid", "crafting_grids"),
            new BlockDefinition("creative_controller", "creative_controllers"),
            new BlockDefinition("destructor", "destructors"),
            new BlockDefinition("exporter", "exporters"),
            new BlockDefinition("external_storage", "external_storages"),
            new BlockDefinition("grid", "grids"),
            new BlockDefinition("importer", "importers"),
            new BlockDefinition("network_receiver", "network_receivers"),
            new BlockDefinition("network_transmitter", "network_transmitters"),
            new BlockDefinition("pattern_grid", "pattern_grids"),
            new BlockDefinition("relay", "relays"),
            new BlockDefinition("security_manager", "security_managers"),
            new BlockDefinition("wireless_transmitter", "wireless_transmitters"),
            new BlockDefinition("disk_interface", "disk_interfaces"),
            new BlockDefinition("detector", "detectors")
    );

    private static final String DATA_OUT = "src/generated/resources/overlay_rs_dd/data/refinedstorage";

    public static void run() throws IOException {
        System.out.println("Starting Refined Storage DATA generation...");
        generateTags();
        generateRecipes();
        generateLootTables();
        generateAdvancements();
        System.out.println("RS DATA gen complete.");
    }

    private static void generateTags() throws IOException {
        JsonArray pickaxeValues = new JsonArray();
        for (BlockDefinition block : BLOCKS) {
            JsonArray itemTagValues = new JsonArray();
            for (String color : COLORS) {
                String entry = "refinedstorage:" + color + "_" + block.base();
                itemTagValues.add(entry);
                pickaxeValues.add(entry);
            }
            JsonObject itemTagJson = new JsonObject();
            itemTagJson.addProperty("replace", false);
            itemTagJson.add("values", itemTagValues);
            write(DATA_OUT + "/tags/item/" + block.tagName() + ".json", GSON.toJson(itemTagJson));
        }
        JsonObject pickaxeJson = new JsonObject();
        pickaxeJson.addProperty("replace", false);
        pickaxeJson.add("values", pickaxeValues);
        write("src/generated/resources/overlay_rs_dd/data/minecraft/tags/block/mineable/pickaxe.json", GSON.toJson(pickaxeJson));
    }

    private static void generateRecipes() throws IOException {
        for (BlockDefinition block : BLOCKS) {
            if (block.base().contains("creative")) continue;
            for (String color : COLORS) {
                generateColoringRecipe(block.base(), color, "refinedstorage:" + block.tagName());
            }
        }
    }

    private static void generateColoringRecipe(String base, String color, String tagRef) throws IOException {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", "misc");
        JsonArray ingredients = new JsonArray();
        JsonObject tIng = new JsonObject(); tIng.addProperty("tag", tagRef);
        ingredients.add(tIng);
        JsonObject dIng = new JsonObject(); dIng.addProperty("tag", "c:dyes/" + color);
        ingredients.add(dIng);
        recipe.add("ingredients", ingredients);
        JsonObject res = new JsonObject(); res.addProperty("count", 1); res.addProperty("id", "refinedstorage:" + color + "_" + base);
        recipe.add("result", res);
        write(DATA_OUT + "/recipe/coloring/" + color + "_" + base + ".json", GSON.toJson(recipe));
    }

    private static void generateLootTables() throws IOException {
        for (BlockDefinition block : BLOCKS) {
            for (String color : COLORS) {
                String fullId = "refinedstorage:" + color + "_" + block.base();

                JsonObject root = new JsonObject();
                root.addProperty("type", "minecraft:block");

                JsonArray functions = getJsonElements(block);
                root.add("functions", functions);

                // Pools
                JsonArray pools = getArray(fullId);
                root.add("pools", pools);

                // Random Sequence
                root.addProperty("random_sequence", "refinedstorage:blocks/" + color + "_" + block.base());

                write(DATA_OUT + "/loot_table/blocks/" + color + "_" + block.base() + ".json", GSON.toJson(root));
            }
        }
    }

    private static @NotNull JsonArray getJsonElements(BlockDefinition block) {
        JsonArray functions = new JsonArray();
        if (block.base().equals("controller")) {
            JsonObject energyFunc = new JsonObject();
            energyFunc.addProperty("function", "refinedstorage:energy");
            functions.add(energyFunc);
        }

        JsonObject nameFunc = new JsonObject();
        nameFunc.addProperty("function", "minecraft:copy_components");
        JsonArray include = new JsonArray();
        include.add("minecraft:custom_name");
        nameFunc.add("include", include);
        nameFunc.addProperty("source", "block_entity");
        functions.add(nameFunc);
        return functions;
    }

    private static @NotNull JsonArray getArray(String fullId) {
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("bonus_rolls", 0.0);
        pool.addProperty("rolls", 1.0);

        JsonArray conditions = new JsonArray();
        JsonObject survive = new JsonObject();
        survive.addProperty("condition", "minecraft:survives_explosion");
        conditions.add(survive);
        pool.add("conditions", conditions);

        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", fullId);
        entries.add(entry);
        pool.add("entries", entries);

        pools.add(pool);
        return pools;
    }

    private static void generateAdvancements() throws IOException {
        for (BlockDefinition block : BLOCKS) {
            if (block.base().contains("creative")) continue;

            for (String color : COLORS) {
                JsonObject root = new JsonObject();
                root.addProperty("parent", "minecraft:recipes/root");

                JsonObject criteria = new JsonObject();

                JsonObject hasBlock = getJsonObject(block);

                criteria.add("has_" + block.tagName(), hasBlock);

                JsonObject hasRecipe = new JsonObject();
                JsonObject hasRecipeConditions = new JsonObject();
                String recipePath = "refinedstorage:coloring/" + color + "_" + block.base();
                hasRecipeConditions.addProperty("recipe", recipePath);
                hasRecipe.add("conditions", hasRecipeConditions);
                hasRecipe.addProperty("trigger", "minecraft:recipe_unlocked");
                criteria.add("has_the_recipe", hasRecipe);

                root.add("criteria", criteria);

                JsonArray requirements = new JsonArray();
                JsonArray subReq = new JsonArray();
                subReq.add("has_the_recipe");
                subReq.add("has_" + block.tagName());
                requirements.add(subReq);
                root.add("requirements", requirements);

                JsonObject rewards = new JsonObject();
                JsonArray recipes = new JsonArray();
                recipes.add(recipePath);
                rewards.add("recipes", recipes);
                root.add("rewards", rewards);

                String path = DATA_OUT + "/advancement/recipes/misc/coloring/" + color + "_" + block.base() + ".json";
                write(path, GSON.toJson(root));
            }
        }
    }

    private static @NotNull JsonObject getJsonObject(BlockDefinition block) {
        JsonObject hasBlock = new JsonObject();
        JsonObject hasBlockConditions = new JsonObject();
        JsonArray hasBlockItems = new JsonArray();
        JsonObject itemTag = new JsonObject();
        itemTag.addProperty("items", "#refinedstorage:" + block.tagName());
        hasBlockItems.add(itemTag);
        hasBlockConditions.add("items", hasBlockItems);
        hasBlock.add("conditions", hasBlockConditions);
        hasBlock.addProperty("trigger", "minecraft:inventory_changed");
        return hasBlock;
    }

    private static void write(String path, String content) throws IOException {
        Path target = Path.of(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content);
    }
}