package com.dadoirie.assortedtweaksnfixes.data;

import com.dadoirie.assortedtweaksnfixes.data.compats.condiments.CrateAssetsDataProvider;
import com.dadoirie.assortedtweaksnfixes.data.compats.condiments.CrateTextureProvider;
import com.dadoirie.assortedtweaksnfixes.data.compats.curios.CreateCuriosDataGenerator;
import com.dadoirie.assortedtweaksnfixes.data.compats.curios.CreateJetpackCuriosDataGenerator;
import com.dadoirie.assortedtweaksnfixes.data.compats.mekanism_compat.DyeDepotPigmentPainting;
import com.dadoirie.assortedtweaksnfixes.data.compats.mekanism_compat.DyeDepotPigmentExtraction;
import com.dadoirie.assortedtweaksnfixes.data.compats.mekanism_compat.PigmentMixer;
import com.dadoirie.assortedtweaksnfixes.data.compats.mekanism_compat.PigmentMixerRemoval;
import com.dadoirie.assortedtweaksnfixes.data.compats.petrochem.ElectrolyzingRecipeGenerator;
import com.dadoirie.assortedtweaksnfixes.data.compats.refined_storage.RefinedStorageAssetsGenerator;
import com.dadoirie.assortedtweaksnfixes.data.compats.refined_storage.RefinedStorageDataGenerator;
import com.dadoirie.assortedtweaksnfixes.data.compats.refined_storage.RefinedStorageTextureGenerator;
import com.dadoirie.assortedtweaksnfixes.data.content.HammerAssetsGenerator;
import com.dadoirie.assortedtweaksnfixes.data.content.HammerDataGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModDataGenerators {
    public record RecipeData(int extract, int paint, String file) {}

    public static final Map<String, RecipeData> PIGMENT_ADDITIONAL_INGREDIENTS = new LinkedHashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject currentPackMcmeta;

    static {
        PIGMENT_ADDITIONAL_INGREDIENTS.put("dye", new RecipeData(256, 256, "dye.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("banner", new RecipeData(64, 64, "banners.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("candle", new RecipeData(224, 224, "candle.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("concrete", new RecipeData(24, 24, "concrete.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("concrete_powder", new RecipeData(6, 6, "concrete_powder.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("stained_glass", new RecipeData(16, 16, "glass.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("stained_glass_pane", new RecipeData(6, 6, "glass_panes.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("wool", new RecipeData(192, 192, "wool.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("carpet", new RecipeData(128, 128, "carpets.json"));
        PIGMENT_ADDITIONAL_INGREDIENTS.put("terracotta", new RecipeData(24, 24, "terracotta.json"));
    }

    public static void init() throws IOException {
        System.out.println("Starting data generation...");
        ATNFLangGenerator.run();

        generateOverlayPackMcmeta();
        PigmentMixerRemoval.removeMekanismNamespacePigmentMixing();

        DyeDepotPigmentExtraction.init(PIGMENT_ADDITIONAL_INGREDIENTS);
        DyeDepotPigmentPainting.init(PIGMENT_ADDITIONAL_INGREDIENTS);
        PigmentMixer.generateRecipes();
        addMekanismOverlayEntry();

        RefinedStorageTextureGenerator.run();
        RefinedStorageAssetsGenerator.run();
        RefinedStorageDataGenerator.run();
        addRefinedStorageOverlayEntry();

        CrateTextureProvider.run();
        CrateAssetsDataProvider.run();
        addCondimentsOverlayEntry();

        ElectrolyzingRecipeGenerator.run();
        addPetrochemElectroenergeticsOverlayEntry();

        HammerAssetsGenerator.run();
        HammerDataGenerator.run();
        addHammerOverlayEntry();

        CreateCuriosDataGenerator.run();
        addCreateBacktankCuriosOverlayEntry();
        CreateJetpackCuriosDataGenerator.run();
        addCreateJetpackCuriosOverlayEntry();

        savePackMcmeta();

        System.out.println("Data generation complete!");
    }

    private static void generateOverlayPackMcmeta() throws IOException {
        Path existing = Path.of("src/main/resources/pack.mcmeta.json");
        if (Files.exists(existing)) {
            currentPackMcmeta = JsonParser.parseString(Files.readString(existing)).getAsJsonObject();
            return;
        }

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

    private static void addPetrochemElectroenergeticsOverlayEntry() {
        JsonObject overlays = currentPackMcmeta.getAsJsonObject("overlays");
        JsonArray entries = overlays.getAsJsonArray("entries");

        JsonObject entry = new JsonObject();
        JsonArray conditions = new JsonArray();
        JsonObject pc = new JsonObject();
        pc.addProperty("type", "neoforge:mod_loaded");
        pc.addProperty("modid", "petrochem");
        conditions.add(pc);
        JsonObject ee = new JsonObject();
        ee.addProperty("type", "neoforge:mod_loaded");
        ee.addProperty("modid", "electroenergetics");
        conditions.add(ee);

        entry.add("neoforge:conditions", conditions);
        entry.addProperty("directory", "overlay_pc_ee");

        JsonArray formats = new JsonArray();
        formats.add(0);
        formats.add(2147483647);
        entry.add("formats", formats);

        entries.add(entry);
    }

    private static void addHammerOverlayEntry() {
        JsonObject overlays = currentPackMcmeta.getAsJsonObject("overlays");
        JsonArray entries = overlays.getAsJsonArray("entries");

        JsonObject entry = new JsonObject();
        JsonArray conditions = new JsonArray();

        JsonObject notCdg = new JsonObject();
        notCdg.addProperty("type", "neoforge:not");
        JsonObject cdgLoaded = new JsonObject();
        cdgLoaded.addProperty("type", "neoforge:mod_loaded");
        cdgLoaded.addProperty("modid", "createdieselgenerators");
        notCdg.add("value", cdgLoaded);
        conditions.add(notCdg);

        JsonObject createLoaded = new JsonObject();
        createLoaded.addProperty("type", "neoforge:mod_loaded");
        createLoaded.addProperty("modid", "create");
        conditions.add(createLoaded);

        entry.add("neoforge:conditions", conditions);
        entry.addProperty("directory", "overlay_hammer");

        JsonArray formats = new JsonArray();
        formats.add(0);
        formats.add(2147483647);
        entry.add("formats", formats);

        entries.add(entry);
    }

    private static void addCreateBacktankCuriosOverlayEntry() {
        JsonObject overlays = currentPackMcmeta.getAsJsonObject("overlays");
        JsonArray entries = overlays.getAsJsonArray("entries");

        JsonObject entry = new JsonObject();
        JsonArray conditions = new JsonArray();

        JsonObject createLoaded = new JsonObject();
        createLoaded.addProperty("type", "neoforge:mod_loaded");
        createLoaded.addProperty("modid", "create");
        conditions.add(createLoaded);

        JsonObject curiosLoaded = new JsonObject();
        curiosLoaded.addProperty("type", "neoforge:mod_loaded");
        curiosLoaded.addProperty("modid", "curios");
        conditions.add(curiosLoaded);

        JsonObject notJetpack = new JsonObject();
        notJetpack.addProperty("type", "neoforge:not");
        JsonObject jetpackLoaded = new JsonObject();
        jetpackLoaded.addProperty("type", "neoforge:mod_loaded");
        jetpackLoaded.addProperty("modid", "create_jetpack");
        notJetpack.add("value", jetpackLoaded);
        conditions.add(notJetpack);

        entry.add("neoforge:conditions", conditions);
        entry.addProperty("directory", "overlay_create");

        JsonArray formats = new JsonArray();
        formats.add(0);
        formats.add(2147483647);
        entry.add("formats", formats);

        entries.add(entry);
    }

    private static void addCreateJetpackCuriosOverlayEntry() {
        JsonObject overlays = currentPackMcmeta.getAsJsonObject("overlays");
        JsonArray entries = overlays.getAsJsonArray("entries");

        JsonObject entry = new JsonObject();
        JsonArray conditions = new JsonArray();

        JsonObject jetpackLoaded = new JsonObject();
        jetpackLoaded.addProperty("type", "neoforge:mod_loaded");
        jetpackLoaded.addProperty("modid", "create_jetpack");
        conditions.add(jetpackLoaded);

        JsonObject curiosLoaded = new JsonObject();
        curiosLoaded.addProperty("type", "neoforge:mod_loaded");
        curiosLoaded.addProperty("modid", "curios");
        conditions.add(curiosLoaded);

        entry.add("neoforge:conditions", conditions);
        entry.addProperty("directory", "overlay_create_jetpacks");

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