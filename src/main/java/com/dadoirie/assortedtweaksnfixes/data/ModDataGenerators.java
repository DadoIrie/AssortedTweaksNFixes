package com.dadoirie.assortedtweaksnfixes.data;

import com.dadoirie.assortedtweaksnfixes.data.condiments.CrateAssetsDataProvider;
import com.dadoirie.assortedtweaksnfixes.data.condiments.CrateTextureProvider;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.DyeDepotPigmentPainting;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.DyeDepotPigmentExtraction;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.PigmentMixer;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.PigmentMixerRemoval;
import com.dadoirie.assortedtweaksnfixes.data.refined_storage.RefinedStorageAssetsProvider;
import com.dadoirie.assortedtweaksnfixes.data.refined_storage.RefinedStorageDatagen;
import com.dadoirie.assortedtweaksnfixes.data.refined_storage.RefinedStorageTextureProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModDataGenerators {
    public record RecipeData(int extract, int paint, String file) {}

    public static final Map<String, RecipeData> TYPES = new LinkedHashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject currentPackMcmeta;

    static {
        TYPES.put("dye", new RecipeData(256, 256, "dye.json"));
        TYPES.put("banner", new RecipeData(64, 64, "banners.json"));
        TYPES.put("candle", new RecipeData(224, 224, "candle.json"));
        TYPES.put("concrete", new RecipeData(24, 24, "concrete.json"));
        TYPES.put("concrete_powder", new RecipeData(6, 6, "concrete_powder.json"));
        TYPES.put("stained_glass", new RecipeData(16, 16, "glass.json"));
        TYPES.put("stained_glass_pane", new RecipeData(6, 6, "glass_panes.json"));
        TYPES.put("wool", new RecipeData(192, 192, "wool.json"));
        TYPES.put("carpet", new RecipeData(128, 128, "carpets.json"));
        TYPES.put("terracotta", new RecipeData(24, 24, "terracotta.json"));
    }

    public static void init() throws IOException {
        System.out.println("Starting data generation...");
        generateOverlayPackMcmeta();
        PigmentMixerRemoval.removeMekanismNamespacePigmentMixing();

        DyeDepotPigmentExtraction.init(TYPES);
        DyeDepotPigmentPainting.init(TYPES);
        PigmentMixer.generateRecipes();
        addMekanismOverlayEntry();

        RefinedStorageTextureProvider.run();
        RefinedStorageAssetsProvider.generateColoredAssets();
        //RefinedStorageDatagen.run();
        addRefinedStorageOverlayEntry();

        CrateTextureProvider.run();
        CrateAssetsDataProvider.run();
        addCondimentsOverlayEntry();

        savePackMcmeta();

        System.out.println("Data generation complete!");
    }

    private static void generateOverlayPackMcmeta() {
        JsonObject pack = new JsonObject();
        pack.addProperty("description", "Compatibility resources");
        pack.addProperty("pack_format", 34);

        JsonObject overlays = new JsonObject();
        JsonArray entries = new JsonArray();
        overlays.add("entries", entries);

        currentPackMcmeta = new JsonObject();
        currentPackMcmeta.add("pack", pack);
        currentPackMcmeta.add("overlays", overlays);
    }

    private static void addRefinedStorageOverlayEntry() {
        JsonObject overlays = currentPackMcmeta.getAsJsonObject("overlays");
        JsonArray entries = overlays.getAsJsonArray("entries");
        
        JsonObject entry = new JsonObject();
        JsonArray conditions = new JsonArray();
        JsonObject rs = new JsonObject();
        rs.addProperty("type", "neoforge:mod_loaded");
        rs.addProperty("modid", "refinedstorage");
        conditions.add(rs);
        JsonObject dd = new JsonObject();
        dd.addProperty("type", "neoforge:mod_loaded");
        dd.addProperty("modid", "dye_depot");
        conditions.add(dd);
        
        entry.add("neoforge:conditions", conditions);
        entry.addProperty("directory", "overlay_rs_dd");
        
        JsonArray formats = new JsonArray();
        formats.add(0);
        formats.add(2147483647);
        entry.add("formats", formats);
        
        entries.add(entry);
    }

    private static void addMekanismOverlayEntry() {
        JsonObject overlays = currentPackMcmeta.getAsJsonObject("overlays");
        JsonArray entries = overlays.getAsJsonArray("entries");
        
        JsonObject entry = new JsonObject();
        JsonArray conditions = new JsonArray();
        JsonObject mek = new JsonObject();
        mek.addProperty("type", "neoforge:mod_loaded");
        mek.addProperty("modid", "mekanism");
        conditions.add(mek);
        JsonObject dd = new JsonObject();
        dd.addProperty("type", "neoforge:mod_loaded");
        dd.addProperty("modid", "dye_depot");
        conditions.add(dd);
        
        entry.add("neoforge:conditions", conditions);
        entry.addProperty("directory", "overlay_mek_dd");
        
        JsonArray formats = new JsonArray();
        formats.add(0);
        formats.add(2147483647);
        entry.add("formats", formats);
        
        entries.add(entry);
    }

    private static void addCondimentsOverlayEntry() {
        JsonObject overlays = currentPackMcmeta.getAsJsonObject("overlays");
        JsonArray entries = overlays.getAsJsonArray("entries");
        
        JsonObject entry = new JsonObject();
        JsonArray conditions = new JsonArray();
        JsonObject con = new JsonObject();
        con.addProperty("type", "neoforge:mod_loaded");
        con.addProperty("modid", "condiments");
        conditions.add(con);
        JsonObject dd = new JsonObject();
        dd.addProperty("type", "neoforge:mod_loaded");
        dd.addProperty("modid", "dye_depot");
        conditions.add(dd);
        
        entry.add("neoforge:conditions", conditions);
        entry.addProperty("directory", "overlay_con_dd");
        
        JsonArray formats = new JsonArray();
        formats.add(0);
        formats.add(2147483647);
        entry.add("formats", formats);
        
        entries.add(entry);
    }

    private static void savePackMcmeta() throws IOException {
        String path = "src/generated/resources/pack.mcmeta";
        Path target = Path.of(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, GSON.toJson(currentPackMcmeta));
        System.out.println("Generated: " + path);
    }

    public static void main(String[] args) throws IOException {
        init();
    }
}
