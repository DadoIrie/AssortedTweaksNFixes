package com.dadoirie.assortedtweaksnfixes.data;

import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.DyeDepotPigmentPainting;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.DyeDepotPigmentExtraction;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.PigmentMixer;
import com.dadoirie.assortedtweaksnfixes.data.mekanism_compat.PigmentMixerRemoval;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModDataGenerators {

    public record RecipeData(int extract, int paint, String file) {}

    public static final Map<String, RecipeData> TYPES = new LinkedHashMap<>() {{
        put("dye", new RecipeData(256, 256, "dye.json"));
        put("banner", new RecipeData(64, 64, "banners.json"));
        put("candle", new RecipeData(224, 224, "candle.json"));
        put("concrete", new RecipeData(24, 24, "concrete.json"));
        put("concrete_powder", new RecipeData(6, 6, "concrete_powder.json"));
        put("stained_glass", new RecipeData(16, 16, "glass.json"));
        put("stained_glass_pane", new RecipeData(6, 6, "glass_panes.json"));
        put("wool", new RecipeData(192, 192, "wool.json"));
        put("carpet", new RecipeData(128, 128, "carpets.json"));
        put("terracotta", new RecipeData(24, 24, "terracotta.json"));
    }};

    public static void init() throws IOException {
        System.out.println("Starting data generation...");
        PigmentMixerRemoval.removeMekanismNamespacePigmentMixing();

        DyeDepotPigmentExtraction.init(TYPES);
        DyeDepotPigmentPainting.init(TYPES);
        PigmentMixer.generateRecipes();

        System.out.println("Data generation complete!");
    }

    public static void main(String[] args) throws IOException {
        init();
    }
}