package com.dadoirie.assortedtweaksnfixes.data.compats.curios;

import java.io.IOException;
import java.util.List;

public class CreateJetpackCuriosDataGenerator {

    private static final String DATA_OUT = "src/generated/resources/overlay_create_jetpacks/data";

    private static final List<String> BACKTANK_ITEMS = List.of(
            "create:copper_backtank",
            "create:netherite_backtank",
            "create_jetpack:jetpack",
            "create_jetpack:netherite_jetpack"
    );

    private static final List<String> DIVING_HELMET_ITEMS = List.of(
            "create:copper_diving_helmet",
            "create:netherite_diving_helmet"
    );

    private static final List<String> DIVING_BOOTS_ITEMS = List.of(
            "create:copper_diving_boots",
            "create:netherite_diving_boots"
    );

    public static void run() throws IOException {
        System.out.println("Starting Create Jetpack Curios data generation...");
        CuriosSlotDataGenerator.writeEntitySlot(DATA_OUT, "minecraft:player", "back", "head", "feet");

        CuriosSlotDataGenerator.writeSlotDefinition(DATA_OUT, "back", 1, true);
        CuriosSlotDataGenerator.writeItemTag(DATA_OUT, "back", BACKTANK_ITEMS);

        CuriosSlotDataGenerator.writeSlotDefinition(DATA_OUT, "head", 1, true);
        CuriosSlotDataGenerator.writeItemTag(DATA_OUT, "head", DIVING_HELMET_ITEMS);

        CuriosSlotDataGenerator.writeSlotDefinition(DATA_OUT, "feet", 1, true);
        CuriosSlotDataGenerator.writeItemTag(DATA_OUT, "feet", DIVING_BOOTS_ITEMS);
        System.out.println("Create Jetpack Curios data gen complete.");
    }
}
