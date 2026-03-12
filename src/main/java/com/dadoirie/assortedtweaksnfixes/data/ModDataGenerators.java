package com.dadoirie.assortedtweaksnfixes.data;

import com.dadoirie.assortedtweaksnfixes.data.condiments.CrateAssetsDataProvider;
import com.dadoirie.assortedtweaksnfixes.data.condiments.CrateTextureProvider;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.DyeDepotPigmentPainting;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.DyeDepotPigmentExtraction;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.PigmentMixer;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.PigmentMixerRemoval;
import com.dadoirie.assortedtweaksnfixes.data.refined_storage.RefinedStorageAssetsProvider;
import com.dadoirie.assortedtweaksnfixes.data.refined_storage.RefinedStorageTextureProvider;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModDataGenerators {
    public record RecipeData(int extract, int paint, String file) {}

    public static final Map<String, RecipeData> TYPES = new LinkedHashMap<>();

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
        PigmentMixerRemoval.removeMekanismNamespacePigmentMixing();

        DyeDepotPigmentExtraction.init(TYPES);
        DyeDepotPigmentPainting.init(TYPES);
        PigmentMixer.generateRecipes();

        RefinedStorageTextureProvider.run();
        RefinedStorageAssetsProvider.generateColoredAssets();

        CrateTextureProvider.run();
        CrateAssetsDataProvider.run();

        System.out.println("Data generation complete!");
    }

    public static void main(String[] args) throws IOException {
        init();
    }
}