package com.dadoirie.assortedtweaksnfixes.data.condiments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ninni.dye_depot.registry.DDDyes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CrateAssetsDataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String ASSETS_BASE_IN = "libs/resources/condiments/assets/condiments/";
    private static final String ASSETS_BASE_OUT = "src/generated/resources/overlay_con_dd/assets/condiments/";
    private static final String DATA_BASE_OUT = "src/generated/resources/overlay_con_dd/data/condiments/";
    private static final String EMI_OUT = "src/generated/resources/overlay_con_dd/assets/emi/recipe/defaults/condiments.json";

    private static final String BASE_BLOCK_MODEL = ASSETS_BASE_IN + "blockstates/black_crate.json";
    private static final String BASE_BLOCK_MODEL_TEMPLATE = ASSETS_BASE_IN + "models/block/crates/black_crate.json";

    private static final String BLOCK_MODELS_OUT = ASSETS_BASE_OUT + "models/block/crates/";
    private static final String ITEM_MODELS_OUT = ASSETS_BASE_OUT + "models/item/";
    private static final String BLOCKSTATES_OUT = ASSETS_BASE_OUT + "blockstates/";

    public static void run() throws IOException {
        JsonObject baseBlockstate;
        try (FileReader reader = new FileReader(BASE_BLOCK_MODEL)) {
            baseBlockstate = GSON.fromJson(reader, JsonObject.class);
        }

        JsonObject baseBlockModel;
        try (FileReader reader = new FileReader(BASE_BLOCK_MODEL_TEMPLATE)) {
            baseBlockModel = GSON.fromJson(reader, JsonObject.class);
        }

        JsonObject blockTag = new JsonObject();
        blockTag.add("values", GSON.toJsonTree(new String[0]));
        JsonObject itemTag = new JsonObject();
        itemTag.add("values", GSON.toJsonTree(new String[0]));

        JsonObject emiTag = new JsonObject();
        emiTag.add("added", GSON.toJsonTree(new String[0]));

        for (DDDyes dye : DDDyes.values()) {
            String colorName = dye.getName();
            String blockModelName = colorName + "_crate";

            JsonObject blockModel = baseBlockModel.deepCopy();
            JsonObject textures = blockModel.getAsJsonObject("textures");
            textures.addProperty("0", "condiments:block/" + blockModelName);
            textures.addProperty("particle", "condiments:block/" + blockModelName);

            writeJson(new File(BLOCK_MODELS_OUT, blockModelName + ".json"), blockModel);

            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", "condiments:item/crate");

            writeJson(new File(ITEM_MODELS_OUT, blockModelName + ".json"), itemModel);

            JsonObject blockstateCopy = baseBlockstate.deepCopy();
            JsonObject variants = blockstateCopy.getAsJsonObject("variants");

            for (String key : variants.keySet()) {
                JsonElement variantElem = variants.get(key);
                if (variantElem.isJsonObject()) {
                    variantElem.getAsJsonObject().addProperty("model", "condiments:block/crates/" + blockModelName);
                } else if (variantElem.isJsonArray()) {
                    for (JsonElement e : variantElem.getAsJsonArray()) {
                        e.getAsJsonObject().addProperty("model", "condiments:block/crates/" + blockModelName);
                    }
                }
            }

            writeJson(new File(BLOCKSTATES_OUT, blockModelName + ".json"), blockstateCopy);

            blockTag.getAsJsonArray("values").add("condiments:" + blockModelName);
            itemTag.getAsJsonArray("values").add("condiments:" + blockModelName);

            emiTag.getAsJsonArray("added").add("condiments:/crate_coloring_" + colorName);

            System.out.println("Generated Conditments crate assets for: " + colorName);
        }

        writeJson(new File(DATA_BASE_OUT + "tags/block", "crates.json"), blockTag);
        writeJson(new File(DATA_BASE_OUT + "tags/item", "crates.json"), itemTag);

        writeJson(new File(EMI_OUT), emiTag);
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