package com.dadoirie.assortedtweaksnfixes.data.compats.curios;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Generic Curios slot datagen helpers: entity-slot mapping, slot definition and item tag files.
 * Knows nothing about which items or mods use it - callers supply their own entity/slot id and
 * item list, and write into their own overlay directory.
 */
public class CuriosSlotDataGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void writeEntitySlot(String dataOut, String entityId, String... slotIds) throws IOException {
        JsonObject entitySlots = new JsonObject();
        JsonArray entities = new JsonArray();
        entities.add(entityId);
        entitySlots.add("entities", entities);
        JsonArray slots = new JsonArray();
        for (String slotId : slotIds) {
            slots.add(slotId);
        }
        entitySlots.add("slots", slots);
        write(dataOut + "/assortedtweaksnfixes/curios/entities/players.json", GSON.toJson(entitySlots));
    }

    public static void writeSlotDefinition(String dataOut, String slotId, int size, boolean addCosmetic) throws IOException {
        JsonObject slotDefinition = new JsonObject();
        slotDefinition.addProperty("size", size);
        slotDefinition.addProperty("add_cosmetic", addCosmetic);
        write(dataOut + "/assortedtweaksnfixes/curios/slots/" + slotId + ".json", GSON.toJson(slotDefinition));
    }

    public static void writeItemTag(String dataOut, String slotId, List<String> items) throws IOException {
        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        JsonArray values = new JsonArray();
        items.forEach(values::add);
        tag.add("values", values);
        write(dataOut + "/curios/tags/item/" + slotId + ".json", GSON.toJson(tag));
    }

    private static void write(String path, String content) throws IOException {
        Path target = Path.of(path);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content);
    }
}
