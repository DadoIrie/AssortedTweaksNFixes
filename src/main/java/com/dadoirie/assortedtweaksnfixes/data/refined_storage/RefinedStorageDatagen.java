package com.dadoirie.assortedtweaksnfixes.data.refined_storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Deterministic datagen for Refined Storage Dye Depot color variants.
 *
 * Strategy:
 *   - Blockstates & models: load the red template from libs/, apply targeted
 *     regex replacement, write to output. Safe because "red" as a color token
 *     is always surrounded by / or " in RS JSON — never mid-word.
 *   - Recipes, loot tables, advancements: built programmatically — uniform
 *     enough that templates add no value, and we need neoforge:conditions.
 *   - Tags: built programmatically with replace:false so they merge at runtime.
 *
 * Output namespaces:
 *   assets/refinedstorage/  — blockstates, models
 *   data/refinedstorage/    — recipes, loot tables, advancements, RS item tags
 *   data/minecraft/         — pickaxe tag
 */
public class RefinedStorageDatagen {

    // -------------------------------------------------------------------------
    // Colors — must match DDDyes serialized names
    // -------------------------------------------------------------------------

    private static final List<String> DD_COLORS = List.of(
            "maroon", "rose", "coral", "indigo", "navy", "slate",
            "olive", "amber", "beige", "teal", "mint", "aqua",
            "verdant", "forest", "ginger", "tan"
    );

    // -------------------------------------------------------------------------
    // Output roots
    // -------------------------------------------------------------------------

    private static final String ASSET_OUT = "src/generated/resources/assets/refinedstorage";
    private static final String DATA_OUT   = "src/generated/resources/data/refinedstorage";
    private static final String MC_OUT     = "src/generated/resources/data/minecraft";

    // Template source — red assets from RS jar extracted to libs/
    private static final String BLOCKSTATE_SRC  = "libs/resources/refinedstorage/assets/refinedstorage/blockstates";
    private static final String MODEL_BLOCK_SRC  = "libs/resources/refinedstorage/assets/refinedstorage/models/block";
    private static final String MODEL_ITEM_SRC   = "libs/resources/refinedstorage/assets/refinedstorage/models/item";

    // -------------------------------------------------------------------------
    // Block registry
    //
    //   id         — item/block ID suffix, e.g. "cable"
    //   rsTag      — RS item tag name, e.g. "cables"
    //   lootEnergy — true only for controller (needs refinedstorage:energy function)
    // -------------------------------------------------------------------------

    private record BlockType(String id, String rsTag, boolean lootEnergy) {}

    private static final List<BlockType> BLOCKS = List.of(
            new BlockType("cable",                "cables",                false),
            new BlockType("controller",           "controllers",           true),
            //new BlockType("creative_controller",  "creative_controllers",  false),
            new BlockType("constructor",          "constructors",          false),
            new BlockType("destructor",           "destructors",           false),
            new BlockType("exporter",             "exporters",             false),
            new BlockType("importer",             "importers",             false),
            new BlockType("external_storage",     "external_storages",     false),
            new BlockType("detector",             "detectors",             false),
            new BlockType("disk_interface",       "disk_interfaces",       false),
            new BlockType("grid",                 "grids",                 false),
            new BlockType("crafting_grid",        "crafting_grids",        false),
            new BlockType("pattern_grid",         "pattern_grids",         false),
            new BlockType("autocrafter",          "autocrafters",          false),
            new BlockType("autocrafter_manager",  "autocrafter_managers",  false),
            new BlockType("autocrafting_monitor", "autocrafting_monitors", false),
            new BlockType("relay",                "relays",                false),
            new BlockType("network_receiver",     "network_receivers",     false),
            new BlockType("network_transmitter",  "network_transmitters",  false),
            new BlockType("security_manager",     "security_managers",     false),
            new BlockType("wireless_transmitter", "wireless_transmitters", false)
    );

    // Replace "red" only when bounded by / or " — never mid-word
    // Correctly skips: powered, unpowered, predicate, stored_in_controller
    private static final Pattern COLOR_TOKEN = Pattern.compile("(?<=[/\"])red(?=[/\"])");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    public static void run() throws IOException {
        for (BlockType block : BLOCKS) {
            for (String color : DD_COLORS) {
                writeBlockstate(block, color);
                writeBlockModels(block, color);
                writeItemModel(block, color);
                writeRecipe(block, color);
                writeLootTable(block, color);
                writeAdvancement(block, color);
            }
            writeRsItemTag(block);
        }
        writePickaxeTag();
        System.out.printf("RS datagen complete: %d blocks × %d colors = %d combinations.%n",
                BLOCKS.size(), DD_COLORS.size(), BLOCKS.size() * DD_COLORS.size());
    }

    // -------------------------------------------------------------------------
    // Blockstate — template from libs/, color token replaced
    // -------------------------------------------------------------------------

    private static void writeBlockstate(BlockType block, String color) throws IOException {
        String src = Files.readString(Path.of(BLOCKSTATE_SRC, "red_" + block.id() + ".json"));
        write(ASSET_OUT + "/blockstates/" + color + "_" + block.id() + ".json", recolor(src, color));
    }

    // -------------------------------------------------------------------------
    // Block models — template from libs/, color token replaced
    // disk_interface also has a base_<color> model
    // -------------------------------------------------------------------------

    private static void writeBlockModels(BlockType block, String color) throws IOException {
        String src = Files.readString(Path.of(MODEL_BLOCK_SRC, block.id(), "red.json"));
        write(ASSET_OUT + "/models/block/" + block.id() + "/" + color + ".json", recolor(src, color));

        if (block.id().equals("disk_interface")) {
            String baseSrc = Files.readString(Path.of(MODEL_BLOCK_SRC, "disk_interface", "base_red.json"));
            write(ASSET_OUT + "/models/block/disk_interface/base_" + color + ".json", recolor(baseSrc, color));
        }
    }

    // -------------------------------------------------------------------------
    // Item model — template from libs/, color token replaced
    // -------------------------------------------------------------------------

    private static void writeItemModel(BlockType block, String color) throws IOException {
        String src = Files.readString(Path.of(MODEL_ITEM_SRC, "red_" + block.id() + ".json"));
        write(ASSET_OUT + "/models/item/" + color + "_" + block.id() + ".json", recolor(src, color));
    }

    // -------------------------------------------------------------------------
    // Recipe — shapeless crafting, conditions required
    // Ingredient 1: refinedstorage:<rsTag> (any existing color of this block)
    // Ingredient 2: c:dyes/<color>
    // Result:       refinedstorage:<color>_<id>
    // -------------------------------------------------------------------------

    private static void writeRecipe(BlockType block, String color) throws IOException {
        JsonObject json = new JsonObject();
        json.add("neoforge:conditions", buildConditions());
        json.addProperty("type", "minecraft:crafting_shapeless");
        json.addProperty("category", "misc");

        JsonArray ingredients = new JsonArray();
        JsonObject blockIngredient = new JsonObject();
        blockIngredient.addProperty("tag", "refinedstorage:" + block.rsTag());
        ingredients.add(blockIngredient);
        JsonObject dyeIngredient = new JsonObject();
        dyeIngredient.addProperty("tag", "c:dyes/" + color);
        ingredients.add(dyeIngredient);
        json.add("ingredients", ingredients);

        JsonObject result = new JsonObject();
        result.addProperty("count", 1);
        result.addProperty("id", "refinedstorage:" + color + "_" + block.id());
        json.add("result", result);

        write(DATA_OUT + "/recipe/coloring/" + color + "_" + block.id() + ".json", json);
    }

    // -------------------------------------------------------------------------
    // Loot table — conditions required
    // controller gets an extra refinedstorage:energy function
    // -------------------------------------------------------------------------

    private static void writeLootTable(BlockType block, String color) throws IOException {
        JsonObject json = new JsonObject();
        json.add("neoforge:conditions", buildConditions());
        json.addProperty("type", "minecraft:block");

        JsonArray functions = new JsonArray();
        if (block.lootEnergy()) {
            JsonObject energy = new JsonObject();
            energy.addProperty("function", "refinedstorage:energy");
            functions.add(energy);
        }
        JsonObject copyComponents = new JsonObject();
        copyComponents.addProperty("function", "minecraft:copy_components");
        JsonArray include = new JsonArray();
        include.add("minecraft:custom_name");
        copyComponents.add("include", include);
        copyComponents.addProperty("source", "block_entity");
        functions.add(copyComponents);
        json.add("functions", functions);

        JsonObject pool = new JsonObject();
        pool.addProperty("bonus_rolls", 0.0);
        JsonArray poolConditions = new JsonArray();
        JsonObject survives = new JsonObject();
        survives.addProperty("condition", "minecraft:survives_explosion");
        poolConditions.add(survives);
        pool.add("conditions", poolConditions);
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", "refinedstorage:" + color + "_" + block.id());
        entries.add(entry);
        pool.add("entries", entries);
        pool.addProperty("rolls", 1.0);

        JsonArray pools = new JsonArray();
        pools.add(pool);
        json.add("pools", pools);
        json.addProperty("random_sequence", "refinedstorage:blocks/" + color + "_" + block.id());

        write(DATA_OUT + "/loot_table/blocks/" + color + "_" + block.id() + ".json", json);
    }

    // -------------------------------------------------------------------------
    // Advancement — unlocked when player has any block of this type + crafts it
    // -------------------------------------------------------------------------

    private static void writeAdvancement(BlockType block, String color) throws IOException {
        String tagName     = block.rsTag();
        String criteriaKey = "has_" + tagName;

        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:recipes/root");

        JsonObject criteria = new JsonObject();

        JsonObject hasTag = new JsonObject();
        JsonObject hasTagConditions = new JsonObject();
        JsonArray items = new JsonArray();
        JsonObject tagRef = new JsonObject();
        tagRef.addProperty("items", "#refinedstorage:" + tagName);
        items.add(tagRef);
        hasTagConditions.add("items", items);
        hasTag.add("conditions", hasTagConditions);
        hasTag.addProperty("trigger", "minecraft:inventory_changed");
        criteria.add(criteriaKey, hasTag);

        JsonObject hasRecipe = new JsonObject();
        JsonObject hasRecipeConditions = new JsonObject();
        hasRecipeConditions.addProperty("recipe", "refinedstorage:coloring/" + color + "_" + block.id());
        hasRecipe.add("conditions", hasRecipeConditions);
        hasRecipe.addProperty("trigger", "minecraft:recipe_unlocked");
        criteria.add("has_the_recipe", hasRecipe);
        json.add("criteria", criteria);

        JsonArray requirements = new JsonArray();
        JsonArray req = new JsonArray();
        req.add("has_the_recipe");
        req.add(criteriaKey);
        requirements.add(req);
        json.add("requirements", requirements);

        JsonObject rewards = new JsonObject();
        JsonArray recipes = new JsonArray();
        recipes.add("refinedstorage:coloring/" + color + "_" + block.id());
        rewards.add("recipes", recipes);
        json.add("rewards", rewards);

        write(DATA_OUT + "/advancement/recipes/misc/coloring/" + color + "_" + block.id() + ".json", json);
    }

    // -------------------------------------------------------------------------
    // RS item tag — DD colors only, replace:false merges with RS's own tag
    // -------------------------------------------------------------------------

    private static void writeRsItemTag(BlockType block) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        JsonArray values = new JsonArray();
        for (String color : DD_COLORS) {
            values.add("refinedstorage:" + color + "_" + block.id());
        }
        json.add("values", values);
        write(DATA_OUT + "/tags/item/" + block.rsTag() + ".json", json);
    }

    // -------------------------------------------------------------------------
    // Minecraft pickaxe block tag — all DD colored blocks, replace:false
    // -------------------------------------------------------------------------

    private static void writePickaxeTag() throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        JsonArray values = new JsonArray();
        for (BlockType block : BLOCKS) {
            for (String color : DD_COLORS) {
                values.add("refinedstorage:" + color + "_" + block.id());
            }
        }
        json.add("values", values);
        write(MC_OUT + "/tags/block/mineable/pickaxe.json", json);
    }

    // -------------------------------------------------------------------------
    // Conditions: requires both refinedstorage and dye_depot to be loaded
    // -------------------------------------------------------------------------

    private static JsonArray buildConditions() {
        JsonArray conditions = new JsonArray();
        JsonObject rs = new JsonObject();
        rs.addProperty("type", "neoforge:mod_loaded");
        rs.addProperty("modid", "refinedstorage");
        conditions.add(rs);
        JsonObject dd = new JsonObject();
        dd.addProperty("type", "neoforge:mod_loaded");
        dd.addProperty("modid", "dye_depot");
        conditions.add(dd);
        return conditions;
    }

    // -------------------------------------------------------------------------
    // Color token replacement — safe regex, bounded by / or "
    // -------------------------------------------------------------------------

    private static String recolor(String content, String color) {
        return COLOR_TOKEN.matcher(content).replaceAll(color);
    }

    // -------------------------------------------------------------------------
    // File I/O
    // -------------------------------------------------------------------------

    private static void write(String path, JsonObject json) throws IOException {
        write(path, GSON.toJson(json));
    }

    private static void write(String path, String content) throws IOException {
        Path target = Path.of(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content);
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}