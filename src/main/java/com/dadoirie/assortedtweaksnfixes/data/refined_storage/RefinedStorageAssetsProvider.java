package com.dadoirie.assortedtweaksnfixes.data.refined_storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RefinedStorageAssetsProvider {

    private static final String BASESOURCE_PATH =
            "libs/resources/refinedstorage/assets/refinedstorage";

    private static final String BASESTORE_PATH =
            "src/generated/resources/assets/refinedstorage";

    private static final String TAGS_PATH =
            "src/generated/resources/data/refinedstorage/tags/item";

    private static final List<String> DYE_COLORS = List.of(
            "maroon", "rose", "coral", "indigo", "navy", "slate",
            "olive", "amber", "beige", "teal", "mint", "aqua",
            "verdant", "forest", "ginger", "tan"
    );

    private static final Map<String, String> TAG_BASE_NAMES = new HashMap<>();

    static {
        TAG_BASE_NAMES.put("autocrafter_managers.json", "autocrafter_manager");
        TAG_BASE_NAMES.put("autocrafters.json", "autocrafter");
        TAG_BASE_NAMES.put("autocrafting_monitors.json", "autocrafting_monitor");
        TAG_BASE_NAMES.put("cables.json", "cable");
        TAG_BASE_NAMES.put("constructors.json", "constructor");
        TAG_BASE_NAMES.put("controllers.json", "controller");
        TAG_BASE_NAMES.put("crafting_grids.json", "crafting_grid");
        TAG_BASE_NAMES.put("creative_controllers.json", "creative_controller");
        TAG_BASE_NAMES.put("destructors.json", "destructor");
        TAG_BASE_NAMES.put("detectors.json", "detector");
        TAG_BASE_NAMES.put("disk_interfaces.json", "disk_interface");
        TAG_BASE_NAMES.put("exporters.json", "exporter");
        TAG_BASE_NAMES.put("external_storages.json", "external_storage");
        TAG_BASE_NAMES.put("grids.json", "grid");
        TAG_BASE_NAMES.put("importers.json", "importer");
        TAG_BASE_NAMES.put("network_receivers.json", "network_receiver");
        TAG_BASE_NAMES.put("network_transmitters.json", "network_transmitter");
        TAG_BASE_NAMES.put("pattern_grids.json", "pattern_grid");
        TAG_BASE_NAMES.put("relays.json", "relay");
        TAG_BASE_NAMES.put("security_managers.json", "security_manager");
        TAG_BASE_NAMES.put("wireless_transmitters.json", "wireless_transmitter");
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static List<Path> getRedAssets() throws IOException {
        try (Stream<Path> files = Files.walk(Paths.get(BASESOURCE_PATH))) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .filter(p -> p.getFileName().toString().toLowerCase().contains("red"))
                    .collect(Collectors.toList());
        }
    }

    public static void generateColoredAssets() throws IOException {
        List<Path> redAssets = getRedAssets();

        for (Path sourceFile : redAssets) {
            String content = Files.readString(sourceFile);
            String relativePath = Paths.get(BASESOURCE_PATH).relativize(sourceFile).toString();
            String fileName = sourceFile.getFileName().toString().toLowerCase();

            for (String color : DYE_COLORS) {
                String recolored;

                if (fileName.contains("detector")) {
                    JsonObject json = GSON.fromJson(content, JsonObject.class);

                    JsonObject textures = json.has("textures") ? json.getAsJsonObject("textures") : null;
                    if (textures != null && textures.has("torch")) {
                        textures.addProperty("torch", "refinedstorage:block/detector/cutouts/" + color);
                    }

                    recolored = GSON.toJson(json);

                } else if (fileName.contains("controller")) {
                    JsonObject json = GSON.fromJson(content, JsonObject.class);

                    if (json.has("parent")) {
                        String parent = json.get("parent").getAsString();
                        if ("refinedstorage:block/controller/red".equals(parent)) {
                            json.addProperty("parent", "refinedstorage:block/controller/" + color);
                        }
                    }

                    if (json.has("overrides")) {
                        json.getAsJsonArray("overrides").forEach(el -> {
                            JsonObject obj = el.getAsJsonObject();
                            String model = obj.get("model").getAsString();
                            if ("refinedstorage:block/controller/red".equals(model)) {
                                obj.addProperty("model", "refinedstorage:block/controller/" + color);
                            }
                        });
                    }

                    if (json.has("variants")) {
                        JsonObject variants = json.getAsJsonObject("variants");
                        variants.entrySet().forEach(entry -> {
                            JsonObject variantObj = entry.getValue().getAsJsonObject();
                            String model = variantObj.get("model").getAsString();
                            if ("refinedstorage:block/controller/red".equals(model)) {
                                variantObj.addProperty("model", "refinedstorage:block/controller/" + color);
                            }
                        });
                    }

                    recolored = GSON.toJson(json);

                } else {
                    recolored = content.replace("red", color);
                }

                String targetRelative = relativePath.contains("red") ? relativePath.replace("red", color) : relativePath;
                Path targetFile = Paths.get(BASESTORE_PATH, targetRelative);

                Files.createDirectories(targetFile.getParent());
                Files.writeString(targetFile, recolored);
            }
        }

        generateTags();
    }

    private static void generateTags() throws IOException {
        for (Map.Entry<String, String> entry : TAG_BASE_NAMES.entrySet()) {
            String fileName = entry.getKey();
            String baseName = entry.getValue();

            JsonArray valuesArray = new JsonArray();
            for (String color : DYE_COLORS) {
                valuesArray.add("refinedstorage:" + color + "_" + baseName);
            }

            JsonObject tagObject = new JsonObject();
            tagObject.add("values", valuesArray);

            Path targetFile = Paths.get(TAGS_PATH, fileName);
            Files.createDirectories(targetFile.getParent());
            Files.writeString(targetFile, GSON.toJson(tagObject));
        }
    }
}