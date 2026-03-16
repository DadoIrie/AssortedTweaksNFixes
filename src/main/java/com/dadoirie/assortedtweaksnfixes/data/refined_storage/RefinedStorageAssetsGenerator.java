package com.dadoirie.assortedtweaksnfixes.data.refined_storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RefinedStorageAssetsGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final List<String> COLORS = List.of(
            "maroon", "rose", "coral", "indigo", "navy", "slate",
            "olive", "amber", "beige", "teal", "mint", "aqua",
            "verdant", "forest", "ginger", "tan"
    );

    private static final String ASSET_OUT = "src/generated/resources/overlay_rs_dd/assets/refinedstorage";

    // We only need the libs path for assets in this class
    private static final String ASSETS_SRC = "libs/resources/refined-storage/assets/refinedstorage";

    public static void run() throws IOException {
        System.out.println("Starting Refined Storage ASSET generation...");
        for (String color : COLORS) {
            generateCable(color);
            generateController(color);
            generateCraftingGrid(color);
            generateDetector(color);
            generateDiskInterface(color);
            generateGrid(color);
            generateNetworkReceiver(color);
            generateNetworkTransmitter(color);
            generatePatternGrid(color);
            generateAutocrafter(color);
            generateAutocrafterManager(color);
            generateAutocraftingMonitor(color);
            generateRelay(color);
            generateSecurityManager(color);
            generateWirelessTransmitter(color);
            generateConstructor(color);
            generateDestructor(color);
            generateExporter(color);
            generateExternalStorage(color);
            generateImporter(color);
        }
        System.out.println("RS ASSET gen complete.");
    }

    // --- 1. Cable ---
    private static void generateCable(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_cable.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        if (blockstateVariants.has("")) {
            JsonObject defaultVariant = blockstateVariants.getAsJsonObject("");
            String model = defaultVariant.get("model").getAsString();
            defaultVariant.addProperty("model", model.replace("red", color));
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_cable.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelBaseSourcePath = Path.of(ASSETS_SRC, "models/block/cable/red.json");
        String blockModelBaseContent = Files.readString(blockModelBaseSourcePath);
        JsonObject blockModelBaseJson = GSON.fromJson(blockModelBaseContent, JsonObject.class);

        if (blockModelBaseJson.has("color")) {
            blockModelBaseJson.addProperty("color", color);
        }

        String blockModelBaseTargetPath = ASSET_OUT + "/models/block/cable/" + color + ".json";
        write(blockModelBaseTargetPath, GSON.toJson(blockModelBaseJson));

        Path blockModelCoreSourcePath = Path.of(ASSETS_SRC, "models/block/cable/core/red.json");
        String blockModelCoreContent = Files.readString(blockModelCoreSourcePath);
        JsonObject blockModelCoreJson = GSON.fromJson(blockModelCoreContent, JsonObject.class);

        if (blockModelCoreJson.has("textures")) {
            JsonObject blockModelCoreTextures = blockModelCoreJson.getAsJsonObject("textures");
            if (blockModelCoreTextures.has("cable")) {
                blockModelCoreTextures.addProperty("cable", blockModelCoreTextures.get("cable").getAsString().replace("red", color));
            }
            if (blockModelCoreTextures.has("particle")) {
                blockModelCoreTextures.addProperty("particle", blockModelCoreTextures.get("particle").getAsString().replace("red", color));
            }
        }

        String blockModelCoreTargetPath = ASSET_OUT + "/models/block/cable/core/" + color + ".json";
        write(blockModelCoreTargetPath, GSON.toJson(blockModelCoreJson));

        Path blockModelExtSourcePath = Path.of(ASSETS_SRC, "models/block/cable/extension/red.json");
        String blockModelExtContent = Files.readString(blockModelExtSourcePath);
        JsonObject blockModelExtJson = GSON.fromJson(blockModelExtContent, JsonObject.class);

        if (blockModelExtJson.has("textures")) {
            JsonObject blockModelExtTextures = blockModelExtJson.getAsJsonObject("textures");
            if (blockModelExtTextures.has("cable")) {
                blockModelExtTextures.addProperty("cable", blockModelExtTextures.get("cable").getAsString().replace("red", color));
            }
            if (blockModelExtTextures.has("particle")) {
                blockModelExtTextures.addProperty("particle", blockModelExtTextures.get("particle").getAsString().replace("red", color));
            }
        }

        String blockModelExtTargetPath = ASSET_OUT + "/models/block/cable/extension/" + color + ".json";
        write(blockModelExtTargetPath, GSON.toJson(blockModelExtJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_cable.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cable")) {
                itemModelTextures.addProperty("cable", itemModelTextures.get("cable").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_cable.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 2. Controller ---
    private static void generateController(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_controller.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        if (blockstateVariants.has("energy_type=on")) {
            JsonObject onVariant = blockstateVariants.getAsJsonObject("energy_type=on");
            String model = onVariant.get("model").getAsString();
            onVariant.addProperty("model", model.replace("red", color));
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_controller.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/controller/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/controller/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        String creativeBlockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_creative_controller.json";
        write(creativeBlockstateTargetPath, GSON.toJson(blockstateJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_controller.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("overrides")) {
            itemModelJson.getAsJsonArray("overrides").forEach(element -> {
                JsonObject override = element.getAsJsonObject();
                String model = override.get("model").getAsString();
                if (model.contains("refinedstorage:block/controller/red")) {
                    override.addProperty("model", model.replace("red", color));
                }
            });
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_controller.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));

        Path creativeItemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_creative_controller.json");
        String creativeItemModelContent = Files.readString(creativeItemModelSourcePath);
        JsonObject creativeItemModelJson = GSON.fromJson(creativeItemModelContent, JsonObject.class);

        if (creativeItemModelJson.has("parent")) {
            String parent = creativeItemModelJson.get("parent").getAsString();
            creativeItemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String creativeItemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_creative_controller.json";
        write(creativeItemModelTargetPath, GSON.toJson(creativeItemModelJson));
    }

    // --- 4. Crafting Grid ---
    private static void generateCraftingGrid(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_crafting_grid.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_crafting_grid.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/crafting_grid/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/crafting_grid/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_crafting_grid.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_crafting_grid.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 5. Detector ---
    private static void generateDetector(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_detector.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            JsonObject variant = blockstateVariants.getAsJsonObject(key);
            String model = variant.get("model").getAsString();
            if (model.contains("red")) {
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_detector.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/detector/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("torch")) {
                blockModelTextures.addProperty("torch", blockModelTextures.get("torch").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/detector/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_detector.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_detector.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 6. Disk Interface ---
    private static void generateDiskInterface(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_disk_interface.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        if (blockstateVariants.has("")) {
            JsonObject defaultVariant = blockstateVariants.getAsJsonObject("");
            String model = defaultVariant.get("model").getAsString();
            defaultVariant.addProperty("model", model.replace("red", color));
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_disk_interface.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/disk_interface/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("color")) {
            blockModelJson.addProperty("color", color);
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/disk_interface/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path baseModelSourcePath = Path.of(ASSETS_SRC, "models/block/disk_interface/base_red.json");
        String baseModelContent = Files.readString(baseModelSourcePath);
        String updatedBaseContent = baseModelContent.replace("red", color);
        String baseModelTargetPath = ASSET_OUT + "/models/block/disk_interface/base_" + color + ".json";
        write(baseModelTargetPath, updatedBaseContent);

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_disk_interface.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("color")) {
            itemModelJson.addProperty("color", color);
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_disk_interface.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 7. Grid ---
    private static void generateGrid(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_grid.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_grid.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/grid/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/grid/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_grid.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_grid.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 8. Network Receiver ---
    private static void generateNetworkReceiver(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_network_receiver.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        if (blockstateVariants.has("active=true")) {
            JsonObject activeVariant = blockstateVariants.getAsJsonObject("active=true");
            String model = activeVariant.get("model").getAsString();
            activeVariant.addProperty("model", model.replace("red", color));
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_network_receiver.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/network_receiver/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/network_receiver/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_network_receiver.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cutout")) {
                itemModelTextures.addProperty("cutout", itemModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_network_receiver.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 9. Network Transmitter ---
    private static void generateNetworkTransmitter(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_network_transmitter.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        if (blockstateVariants.has("state=active")) {
            JsonObject activeVariant = blockstateVariants.getAsJsonObject("state=active");
            String model = activeVariant.get("model").getAsString();
            activeVariant.addProperty("model", model.replace("red", color));
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_network_transmitter.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/network_transmitter/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/network_transmitter/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_network_transmitter.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cutout")) {
                itemModelTextures.addProperty("cutout", itemModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_network_transmitter.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 10. Pattern Grid ---
    private static void generatePatternGrid(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_pattern_grid.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_pattern_grid.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/pattern_grid/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/pattern_grid/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_pattern_grid.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_pattern_grid.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 11. Autocrafter ---
    private static void generateAutocrafter(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_autocrafter.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_autocrafter.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/autocrafter/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            for (String textureKey : blockModelTextures.keySet()) {
                if (textureKey.startsWith("cutout_")) {
                    blockModelTextures.addProperty(textureKey, blockModelTextures.get(textureKey).getAsString().replace("red", color));
                }
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/autocrafter/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_autocrafter.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_autocrafter.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 12. Autocrafter Manager ---
    private static void generateAutocrafterManager(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_autocrafter_manager.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_autocrafter_manager.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/autocrafter_manager/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/autocrafter_manager/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_autocrafter_manager.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_autocrafter_manager.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 13. Autocrafting Monitor ---
    private static void generateAutocraftingMonitor(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_autocrafting_monitor.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_autocrafting_monitor.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/autocrafting_monitor/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/autocrafting_monitor/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_autocrafting_monitor.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_autocrafting_monitor.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 14. Relay ---
    private static void generateRelay(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_relay.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_relay.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/relay/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            for (String textureKey : blockModelTextures.keySet()) {
                if (textureKey.startsWith("cutout_")) {
                    blockModelTextures.addProperty(textureKey, blockModelTextures.get(textureKey).getAsString().replace("red", color));
                }
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/relay/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_relay.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_relay.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 15. Security Manager ---
    private static void generateSecurityManager(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_security_manager.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_security_manager.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/security_manager/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            for (String textureKey : blockModelTextures.keySet()) {
                if (textureKey.startsWith("cutout_")) {
                    blockModelTextures.addProperty(textureKey, blockModelTextures.get(textureKey).getAsString().replace("red", color));
                }
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/security_manager/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_security_manager.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("parent")) {
            String parent = itemModelJson.get("parent").getAsString();
            itemModelJson.addProperty("parent", parent.replace("red", color));
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_security_manager.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 16. Wireless Transmitter ---
    private static void generateWirelessTransmitter(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_wireless_transmitter.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        JsonObject blockstateVariants = blockstateJson.getAsJsonObject("variants");
        for (String key : blockstateVariants.keySet()) {
            if (key.contains("active=true")) {
                JsonObject variant = blockstateVariants.getAsJsonObject(key);
                String model = variant.get("model").getAsString();
                variant.addProperty("model", model.replace("red", color));
            }
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_wireless_transmitter.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path blockModelSourcePath = Path.of(ASSETS_SRC, "models/block/wireless_transmitter/red.json");
        String blockModelContent = Files.readString(blockModelSourcePath);
        JsonObject blockModelJson = GSON.fromJson(blockModelContent, JsonObject.class);

        if (blockModelJson.has("textures")) {
            JsonObject blockModelTextures = blockModelJson.getAsJsonObject("textures");
            if (blockModelTextures.has("cutout")) {
                blockModelTextures.addProperty("cutout", blockModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String blockModelTargetPath = ASSET_OUT + "/models/block/wireless_transmitter/" + color + ".json";
        write(blockModelTargetPath, GSON.toJson(blockModelJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_wireless_transmitter.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cutout")) {
                itemModelTextures.addProperty("cutout", itemModelTextures.get("cutout").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_wireless_transmitter.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 17. Constructor ---
    private static void generateConstructor(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_constructor.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        if (blockstateJson.has("multipart")) {
            blockstateJson.getAsJsonArray("multipart").forEach(element -> {
                JsonObject apply = element.getAsJsonObject().getAsJsonObject("apply");
                if (apply.has("model")) {
                    String model = apply.get("model").getAsString();
                    if (model.contains("red")) {
                        apply.addProperty("model", model.replace("red", color));
                    }
                }
            });
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_constructor.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_constructor.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cable")) {
                itemModelTextures.addProperty("cable", itemModelTextures.get("cable").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_constructor.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 18. Destructor ---
    private static void generateDestructor(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_destructor.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        if (blockstateJson.has("multipart")) {
            blockstateJson.getAsJsonArray("multipart").forEach(element -> {
                JsonObject apply = element.getAsJsonObject().getAsJsonObject("apply");
                if (apply.has("model")) {
                    String model = apply.get("model").getAsString();
                    if (model.contains("red")) {
                        apply.addProperty("model", model.replace("red", color));
                    }
                }
            });
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_destructor.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_destructor.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cable")) {
                itemModelTextures.addProperty("cable", itemModelTextures.get("cable").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_destructor.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 19. Exporter ---
    private static void generateExporter(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_exporter.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        if (blockstateJson.has("multipart")) {
            blockstateJson.getAsJsonArray("multipart").forEach(element -> {
                JsonObject apply = element.getAsJsonObject().getAsJsonObject("apply");
                if (apply.has("model")) {
                    String model = apply.get("model").getAsString();
                    if (model.contains("red")) {
                        apply.addProperty("model", model.replace("red", color));
                    }
                }
            });
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_exporter.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_exporter.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cable")) {
                itemModelTextures.addProperty("cable", itemModelTextures.get("cable").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_exporter.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 20. External Storage ---
    private static void generateExternalStorage(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_external_storage.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        if (blockstateJson.has("multipart")) {
            blockstateJson.getAsJsonArray("multipart").forEach(element -> {
                JsonObject apply = element.getAsJsonObject().getAsJsonObject("apply");
                if (apply.has("model")) {
                    String model = apply.get("model").getAsString();
                    if (model.contains("red")) {
                        apply.addProperty("model", model.replace("red", color));
                    }
                }
            });
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_external_storage.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_external_storage.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cable")) {
                itemModelTextures.addProperty("cable", itemModelTextures.get("cable").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_external_storage.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }

    // --- 21. Importer ---
    private static void generateImporter(String color) throws IOException {
        Path blockstateSourcePath = Path.of(ASSETS_SRC, "blockstates", "red_importer.json");
        String blockstateContent = Files.readString(blockstateSourcePath);
        JsonObject blockstateJson = GSON.fromJson(blockstateContent, JsonObject.class);

        if (blockstateJson.has("multipart")) {
            blockstateJson.getAsJsonArray("multipart").forEach(element -> {
                JsonObject apply = element.getAsJsonObject().getAsJsonObject("apply");
                if (apply.has("model")) {
                    String model = apply.get("model").getAsString();
                    if (model.contains("red")) {
                        apply.addProperty("model", model.replace("red", color));
                    }
                }
            });
        }

        String blockstateTargetPath = ASSET_OUT + "/blockstates/" + color + "_importer.json";
        write(blockstateTargetPath, GSON.toJson(blockstateJson));

        Path itemModelSourcePath = Path.of(ASSETS_SRC, "models/item/red_importer.json");
        String itemModelContent = Files.readString(itemModelSourcePath);
        JsonObject itemModelJson = GSON.fromJson(itemModelContent, JsonObject.class);

        if (itemModelJson.has("textures")) {
            JsonObject itemModelTextures = itemModelJson.getAsJsonObject("textures");
            if (itemModelTextures.has("cable")) {
                itemModelTextures.addProperty("cable", itemModelTextures.get("cable").getAsString().replace("red", color));
            }
        }

        String itemModelTargetPath = ASSET_OUT + "/models/item/" + color + "_importer.json";
        write(itemModelTargetPath, GSON.toJson(itemModelJson));
    }


    // Utility method for writing files
    private static void write(String path, String content) throws IOException {
        Path target = Path.of(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content);
    }
}