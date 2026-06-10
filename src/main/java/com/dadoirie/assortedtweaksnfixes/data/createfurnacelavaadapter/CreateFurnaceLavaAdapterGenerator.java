package com.dadoirie.assortedtweaksnfixes.data.createfurnacelavaadapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateFurnaceLavaAdapterGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String BASE_OUT = "src/generated/resources/overlay_adapter_pipes/assets/assortedtweaksnfixes/blockstates/";

    public static final String[] COLORS = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    public static void run() throws IOException {
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        variants.add("facing=north", createModelObj(null, null));
        variants.add("facing=east",  createModelObj(null, 90));
        variants.add("facing=south", createModelObj(null, 180));
        variants.add("facing=west",  createModelObj(null, 270));
        variants.add("facing=up",    createModelObj(270, null));
        variants.add("facing=down",  createModelObj(90, null));

        blockstate.add("variants", variants);

        File dir = new File(BASE_OUT);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create directory: " + BASE_OUT);
        }

        for (String color : COLORS) {
            File target = new File(dir, color + "_furnace_lava_adapter.json");
            try (FileWriter writer = new FileWriter(target)) {
                GSON.toJson(blockstate, writer);
            }
        }
        System.out.println("Generated 16 blockstate assets for create_furnace_lava_adapter tinting layer.");
    }

    private static JsonObject createModelObj(Integer x, Integer y) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", "assortedtweaksnfixes:block/tinted_furnace_lava_adapter");
        if (x != null) obj.addProperty("x", x);
        if (y != null) obj.addProperty("y", y);
        return obj;
    }

    public static void generateModel() throws IOException {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "block/cube");

        JsonObject textures = new JsonObject();
        textures.addProperty("0", "assortedtweaksnfixes:block/furnace_lava_adapter_texture");
        textures.addProperty("particle", "assortedtweaksnfixes:block/furnace_lava_adapter_texture");
        model.add("textures", textures);

        JsonArray elements = new JsonArray();

        // The complete, self-contained geometry data
        String jsonElements = """
    [
        {"name": "base", "from": [1, 1, 0], "to": [15, 15, 2], "faces": {"north": {"uv": [0,9,7,16], "texture": "#0", "tintindex": 0}, "east": {"uv": [7.5,9,8.5,16], "texture": "#0", "tintindex": 0}, "south": {"uv": [0,9,7,16], "texture": "#0", "tintindex": 0}, "west": {"uv": [7.5,9,8.5,16], "texture": "#0", "tintindex": 0}, "up": {"uv": [7.5,9,8.5,16], "rotation": 270, "texture": "#0", "tintindex": 0}, "down": {"uv": [7.5,9,8.5,16], "rotation": 90, "texture": "#0", "tintindex": 0}}},
        {"name": "port", "from": [3, 3, 14], "to": [13, 13, 15], "faces": {"north": {"uv": [10,10,15,15], "texture": "#0", "tintindex": 0}, "east": {"uv": [6,10,6.5,15], "texture": "#0", "tintindex": 0}, "south": {"uv": [1,10,6,15], "texture": "#0", "tintindex": 0}, "west": {"uv": [0.5,10,1,15], "texture": "#0", "tintindex": 0}, "up": {"uv": [1,15,6,15.5], "texture": "#0", "tintindex": 0}, "down": {"uv": [1,9.5,6,10], "texture": "#0", "tintindex": 0}}},
        {"name": "rod", "from": [3, 3, 2], "to": [4, 4, 14], "faces": {"north": {"uv": [15.5,0,16,0.5], "rotation": 180, "texture": "#0", "tintindex": 0}, "east": {"uv": [14,0,14.5,6], "rotation": 270, "texture": "#0", "tintindex": 0}, "south": {"uv": [15.5,0,16,0.5], "texture": "#0", "tintindex": 0}, "west": {"uv": [15,0,15.5,6], "rotation": 90, "texture": "#0", "tintindex": 0}, "up": {"uv": [14.5,0,15,6], "rotation": 180, "texture": "#0", "tintindex": 0}, "down": {"uv": [15.5,0,16,6], "texture": "#0", "tintindex": 0}}},
        {"name": "rod", "from": [3, 12, 2], "to": [4, 13, 14], "faces": {"north": {"uv": [15.5,0,16,0.5], "rotation": 180, "texture": "#0", "tintindex": 0}, "east": {"uv": [14,0,14.5,6], "rotation": 270, "texture": "#0", "tintindex": 0}, "south": {"uv": [15.5,0,16,0.5], "texture": "#0", "tintindex": 0}, "west": {"uv": [15,0,15.5,6], "rotation": 90, "texture": "#0", "tintindex": 0}, "up": {"uv": [14.5,0,15,6], "rotation": 180, "texture": "#0", "tintindex": 0}, "down": {"uv": [15.5,0,16,6], "texture": "#0", "tintindex": 0}}},
        {"name": "rod", "from": [12, 12, 2], "to": [13, 13, 14], "faces": {"north": {"uv": [15.5,0,16,0.5], "rotation": 180, "texture": "#0", "tintindex": 0}, "east": {"uv": [14,0,14.5,6], "rotation": 270, "texture": "#0", "tintindex": 0}, "south": {"uv": [15.5,0,16,0.5], "texture": "#0", "tintindex": 0}, "west": {"uv": [15,0,15.5,6], "rotation": 90, "texture": "#0", "tintindex": 0}, "up": {"uv": [14.5,0,15,6], "rotation": 180, "texture": "#0", "tintindex": 0}, "down": {"uv": [15.5,0,16,6], "texture": "#0", "tintindex": 0}}},
        {"name": "rod", "from": [12, 3, 2], "to": [13, 4, 14], "faces": {"north": {"uv": [15.5,0,16,0.5], "rotation": 180, "texture": "#0", "tintindex": 0}, "east": {"uv": [14,0,14.5,6], "rotation": 270, "texture": "#0", "tintindex": 0}, "south": {"uv": [15.5,0,16,0.5], "texture": "#0", "tintindex": 0}, "west": {"uv": [15,0,15.5,6], "rotation": 90, "texture": "#0", "tintindex": 0}, "up": {"uv": [14.5,0,15,6], "rotation": 180, "texture": "#0", "tintindex": 0}, "down": {"uv": [15.5,0,16,6], "texture": "#0", "tintindex": 0}}},
        {"name": "tank", "from": [4, 4, 2], "to": [12, 12, 14], "faces": {"north": {"uv": [15.5,15.5,16,16], "rotation": 180, "texture": "#0", "tintindex": 0}, "east": {"uv": [0,0,4,6], "rotation": 270, "texture": "#0", "tintindex": 0}, "south": {"uv": [15.5,15.5,16,16], "texture": "#0", "tintindex": 0}, "west": {"uv": [0,0,4,6], "rotation": 90, "texture": "#0", "tintindex": 0}, "up": {"uv": [0,0,4,6], "rotation": 180, "texture": "#0", "tintindex": 0}, "down": {"uv": [0,0,4,6], "texture": "#0", "tintindex": 0}}},
        {"name": "piston", "from": [6, 6, 2], "to": [10, 10, 14], "faces": {"north": {"uv": [15.5,6,16,6.5], "rotation": 180, "texture": "#0", "tintindex": 0}, "east": {"uv": [14,0,16,6], "rotation": 270, "texture": "#0", "tintindex": 0}, "south": {"uv": [15.5,6,16,6.5], "texture": "#0", "tintindex": 0}, "west": {"uv": [14,0,16,6], "rotation": 90, "texture": "#0", "tintindex": 0}, "up": {"uv": [14,0,16,6], "rotation": 180, "texture": "#0", "tintindex": 0}, "down": {"uv": [14,0,16,6], "texture": "#0", "tintindex": 0}}}
    ]
    """;
        GSON.fromJson(jsonElements, JsonArray.class).forEach(elements::add);
        model.add("elements", elements);

        Path outDir = Path.of("src/generated/resources/overlay_adapter_pipes/assets/assortedtweaksnfixes/models/block/");
        Files.createDirectories(outDir);
        Files.writeString(outDir.resolve("tinted_furnace_lava_adapter.json"), GSON.toJson(model));
    }
}